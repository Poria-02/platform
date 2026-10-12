package cn.poria.message.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.common.core.util.TemplateUtil;
import cn.poria.message.integration.customer.vo.UserAccountBindVo;
import cn.poria.message.integration.customer.vo.response.company.CusCompanyInnerVo;
import cn.poria.message.api.RemoteAliMaService;
import cn.poria.message.api.RemoteCustomerApi;
import cn.poria.message.api.RemoteMaService;
import cn.poria.message.api.RemoteMpService;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.MsgWxLogService;
import cn.poria.message.service.WxService;
import cn.poria.message.util.TemplateParamUtil;
import cn.poria.message.vo.request.WxMsgSend;
import cn.poria.message.vo.request.ma.MaAccountMessageModel;
import cn.poria.message.vo.response.ma.MaAccountMessageVo;
import cn.poria.message.vo.response.template.TemplateVo;
import cn.poria.message.integration.visual.vo.request.TemplateMessageModel;
import cn.poria.message.integration.visual.vo.request.alima.TemplateMsgModel;
import cn.poria.message.integration.visual.vo.request.ma.MaAccountModel;
import cn.poria.message.integration.visual.vo.response.WxAccountInfo;
import cn.poria.message.integration.visual.vo.response.ma.MaAccountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WxServiceImpl implements WxService {

    @Autowired
    private MsgTemplateService templateService;

    @Autowired
    private RemoteMpService remoteMpAPi;

    @Autowired
    private MsgWxLogService wxLogService;

    @Autowired
    private RemoteMaService remoteMaAPi;

    @Autowired
    private RemoteCustomerApi remoteCustomerApi;

    @Autowired
    private RemoteAliMaService remoteAliMaAPi;

    /**
     * 发送微信模版消息
     *
     * @param msgSend
     */
    @Override
    @Transactional
    public void sendTemplateMsg(WxMsgSend msgSend) {

        TemplateVo templateVo = templateService.selectByKey(msgSend.getKey());
        Assert.notNull(templateVo, "模版不存在");
        Assert.isTrue(StrUtil.equals(templateVo.getType(), TemplateType.WX.getCode()), "模版类型错误");
        Assert.isTrue(templateVo.getStatus() == 1, "模版已停用");

        String appId = msgSend.getAppId();
        //查询企业公众号配置
        if (StrUtil.isBlank(appId) && StrUtil.isNotBlank(msgSend.getCompanyId())) {
            //查询appId;
            CusCompanyInnerVo companyInnerVo = remoteCustomerApi.findCompanyInfo(msgSend.getCompanyId(), SecurityConstants.FROM_IN).getData();
            if (companyInnerVo != null) {
                appId = companyInnerVo.getAppId();
            }
        }
        if (StrUtil.isBlank(appId) && msgSend.getOrgId() != null) {
            //查询机构公众号配置
            WxAccountInfo wxAccountInfo = remoteMpAPi.getWxAccountByOrg(msgSend.getOrgId(), SecurityConstants.FROM_IN).getData();
            if (wxAccountInfo != null) {
                appId = wxAccountInfo.getAppid();
            }
        }
        if (StrUtil.isBlank(appId)) {
            log.error("未查询到有效公众号配置");
            return;
        }


        //填充一下公共参数
        msgSend.getParam().put("orgId", "" + msgSend.getOrgId());
        msgSend.getParam().put("appId", "" + appId);


        TemplateMessageModel messageModel = new TemplateMessageModel();
        String openId = remoteCustomerApi.getBindOpenId(msgSend.getAccountId(), appId, SecurityConstants.FROM_IN).getData();
        if (StrUtil.isBlank(openId)) {
            log.error("未查询到openId,账号:{},appId:{}", msgSend.getAccountId(), appId);
            return;
        }
        messageModel.setOpenId(openId);
        messageModel.setAppId(appId);

        //获取机构公众号维护的模版id
        JSONObject jsonObject = JSONUtil.parseObj(templateVo.getContent());
        String wxTemplateId = jsonObject.getStr("" + appId);
        Assert.isTrue(StrUtil.isNotBlank(wxTemplateId), "当前机构未设置模版消息");
        messageModel.setTemplateId(wxTemplateId);
        String url = TemplateUtil.process(templateVo.getUrl(), msgSend.getParam());
        messageModel.setUrl(url);
        messageModel.setParam(msgSend.getParam());
        R<String> r = remoteMpAPi.sendTemplateMessage(messageModel, SecurityConstants.FROM_IN);

        //保存发送日志
        MsgWxLog wxLog = new MsgWxLog();
        wxLog.setContent(templateVo.getContent());
        wxLog.setMsgId(r.getData());
        wxLog.setStatus(1);
        wxLog.setParams(JSONUtil.toJsonStr(msgSend.getParam()));
        wxLog.setTemplateKey(msgSend.getKey());
        if (!r.isSuccess()) {
            wxLog.setResult(r.getMsg());
            wxLog.setStatus(2);//发送失败
        }
        wxLogService.saveLogs(wxLog);

        //如果失败抛出异常
        if (!r.isSuccess()) {
            throw new ServiceException(r.getMsg());
        }
    }

    @Override
    public void sendAllWxTemplateMsg(WxMsgSend msgSend) {
        List<UserAccountBindVo> list = remoteCustomerApi.findUserBinds(msgSend.getAccountId(), SecurityConstants.FROM_IN).getData();
        if (CollectionUtil.isEmpty(list)) return;

        list.forEach(bind -> {
            if (StrUtil.equals(bind.getSubscribeStatus(), "1")) {
                WxMsgSend send = new WxMsgSend();
                BeanUtils.copyProperties(msgSend, send);
                send.setAppId(bind.getAppId());
                try {
                    this.sendTemplateMsg(send);
                } catch (Exception e) {
                    log.error("消息发送失败", e);
                }
            }
        });

    }


    @Override
    public void sendMaSubScribeMsg(WxMsgSend msgSend) {

        TemplateVo templateVo = templateService.selectByKey(msgSend.getKey());
        Assert.notNull(templateVo, "模版不存在");
        Assert.isTrue(StrUtil.equals(templateVo.getType(), TemplateType.MA.getCode()), "模版类型错误");
        Assert.isTrue(templateVo.getStatus() == 1, "模版已停用");

        //动态设置模板参数
//        Map<String, String> param = TemplateParamUtil.settingTemplateParam(templateVo, msgSend.getParam());
//        msgSend.setParam(param);

        //发送微信小程序订阅消息
        if (StrUtil.isEmpty(msgSend.getSource()) || StrUtil.equals(msgSend.getSource(), "weixin_ma")) {
            sendWxMaSubScribeMsg(templateVo, msgSend);
        }

        //发送支付宝小程序通知
        if (StrUtil.isEmpty(msgSend.getSource()) || StrUtil.equals(msgSend.getSource(), "ali_ma")) {
            sendAliMaSubScribeMsg(templateVo, msgSend);
        }

    }

    private void sendWxMaSubScribeMsg(TemplateVo templateVo, WxMsgSend msgSend) {

        //填充下appId
        if (StrUtil.isBlank(msgSend.getAppId()) && msgSend.getOrgId() != null) {
            log.info("templateVo:{} , msgSend:{}" , JSONUtil.toJsonStr(templateVo), JSONUtil.toJsonStr(msgSend));
            MaAccountModel model = new MaAccountModel();
            model.setOrgId(msgSend.getOrgId());
            MaAccountVo maAccountVo = remoteMaAPi.findMaAccountInfo(model, SecurityConstants.FROM_IN).getData();
            if (maAccountVo != null) {
                msgSend.setAppId(maAccountVo.getAppid());
                log.info("appId:{}",maAccountVo.getAppid());
            }
        }

        if (StrUtil.isBlank(msgSend.getAppId())) {
            log.error("未查询到有效的小程序配置");
            return;
        }

        if(!StrUtil.contains(templateVo.getContent(),msgSend.getAppId())){
            log.error("小程序未配置该模版参数,账号{}发送失败",msgSend.getAccountId());
            return;
        }

        //填充一下公共参数
        msgSend.getParam().put("orgId", "" + msgSend.getOrgId());
        msgSend.getParam().put("appId", "" + msgSend.getAccountId());

        TemplateMessageModel messageModel = new TemplateMessageModel();
        String openId = remoteCustomerApi.getBindOpenId(msgSend.getAccountId(), msgSend.getAppId(), SecurityConstants.FROM_IN).getData();
        if (StrUtil.isBlank(openId)) {
            log.error("未查询到openId,账号:{},appId:{}", msgSend.getAccountId(), msgSend.getAppId());
            return;
        }
        messageModel.setOpenId(openId);
        messageModel.setAppId(msgSend.getAppId());

        //获取对应小程序的配置信息
        JSONArray configArray = JSONUtil.parseArray(templateVo.getContent());
        JSONObject config = null;
        for (int i = 0; i < configArray.size(); i++) {
            if(StrUtil.equals(configArray.getJSONObject(i).getStr("appId"),msgSend.getAppId())){
                config = configArray.getJSONObject(i);
                break;
            }
        }


        String wxTemplateId = config.getStr("wxTemplateId");

        messageModel.setTemplateId(wxTemplateId);

        String url = TemplateUtil.process(templateVo.getUrl(), msgSend.getParam());
        messageModel.setUrl(url);

        messageModel.setParam(matchParam(config.getJSONArray("config"),msgSend.getParam()));
        R<String> r = remoteMaAPi.sendMaSubScribeMsg(messageModel, SecurityConstants.FROM_IN);

        //保存发送日志
        MsgWxLog wxLog = new MsgWxLog();
        wxLog.setContent(templateVo.getContent());
        wxLog.setMsgId(r.getData());
        wxLog.setStatus(1);
        wxLog.setParams(JSONUtil.toJsonStr(msgSend.getParam()));
        wxLog.setTemplateKey(msgSend.getKey());
        wxLog.setAccountId(msgSend.getAccountId());
        if (!r.isSuccess()) {
            wxLog.setResult(r.getMsg());
            wxLog.setStatus(2);//发送失败
        }
        wxLogService.saveLogs(wxLog);

        //如果失败抛出异常
        if (!r.isSuccess() || (r.isSuccess() && StrUtil.isNotBlank(r.getMsg()))) {
            throw new ServiceException(r.getMsg());
        }
    }

    private Map<String,String> matchParam(JSONArray config,Map<String,String> param){
        Map<String,String> data = new HashMap<>();
        for (int i = 0; i < config.size(); i++) {
            JSONObject json = config.getJSONObject(i);
            data.put(json.getStr("key"),param.get(StrUtil.isBlank(json.getStr("valueKey"))?json.getStr("key"):json.getStr("valueKey")));
        }
        return data;
    }

    private void sendAliMaSubScribeMsg(TemplateVo templateVo, WxMsgSend msgSend) {

        String userId = remoteCustomerApi.getBindOpenId(msgSend.getAccountId(), msgSend.getAppId(), SecurityConstants.FROM_IN).getData();
        if (StrUtil.isBlank(userId)) {
            log.error("未查询到userId,账号:{},appId:{}", msgSend.getAccountId(), msgSend.getAppId());
            return;
        }

        //获取机构公众号维护的模版id
        JSONObject jsonObject = JSONUtil.parseObj(templateVo.getContent());
        String wxTemplateId = jsonObject.getStr("" + msgSend.getAppId());
        if (StrUtil.isEmpty(wxTemplateId)) {
            //未查询到模版配置直接返回
            return;
        }

        TemplateMsgModel templateMsgModel = TemplateMsgModel.builder().appId(msgSend.getAppId()).templateId(wxTemplateId)
                .url(TemplateUtil.process(templateVo.getUrl(), msgSend.getParam()))
                .param(msgSend.getParam())
                .userId(userId)
                .build();
        remoteAliMaAPi.sendTemplateMessage(templateMsgModel, SecurityConstants.FROM_IN);

    }


    @Override
    public MaAccountMessageVo findMaAccountInfo(@RequestBody MaAccountMessageModel model) {
        MaAccountModel maModel = new MaAccountModel();
        BeanUtils.copyProperties(model, maModel);
        MaAccountVo maAccountVo = remoteMaAPi.findMaAccountInfo(maModel, SecurityConstants.FROM_IN).getData();
        if (maAccountVo == null) {
            return null;
        }
        MaAccountMessageVo vo = new MaAccountMessageVo();
        BeanUtils.copyProperties(maAccountVo, vo);
        return vo;
    }
}
