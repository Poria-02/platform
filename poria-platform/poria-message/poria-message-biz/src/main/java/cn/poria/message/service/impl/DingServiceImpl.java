package cn.poria.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.TemplateUtil;
import cn.poria.message.dao.MsgDdLogDao;
import cn.poria.message.entity.MsgDdLog;
import cn.poria.message.model.DingModel;
import cn.poria.message.service.DingService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.vo.request.DingSend;
import cn.poria.message.vo.response.template.TemplateVo;
import cn.poria.upms.api.util.ParamResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Date;

@Slf4j
@Service
public class DingServiceImpl implements DingService {

    @Autowired
    private MsgTemplateService templateService;

    @Autowired
    private MsgDdLogDao ddLogDao;

    /**
     * 发送钉钉文本消息
     *
     * @param dingSend
     */
    @Override
    public void sendDingText(DingSend dingSend) {
        TemplateVo template = templateService.selectByKey(dingSend.getKey());
        Assert.notNull(template,"模板{}不存在",dingSend.getKey());
        Assert.isTrue(template.getStatus() == 1,"模版已停用");
        if(dingSend.getOrgId() == null){
            dingSend.setOrgId(ParamResolver.getLong("PLAT_DEFAULT_ORG_ID"));
        }
        log.info("发送钉钉消息:{}",template);

        JSONObject templateConfig = JSONUtil.parseObj(template.getContent());
        String content = TemplateUtil.process(templateConfig.getStr("content"),dingSend.getParam());
        JSONArray configs = templateConfig.getJSONArray("config");
        JSONObject config = null;
        for (int i = 0; i < configs.size(); i++) {
            if(ObjectUtil.equal(configs.getJSONObject(i).getLong("orgId"),dingSend.getOrgId())){
                config = configs.getJSONObject(i);
                break;
            }
        }
        Assert.notNull(config,"未查询到可用的钉钉模版配置");

        //发送钉钉消息
        DingModel dingModel = new DingModel();
        dingModel.getText().setContent(content);
        if(config.getInt("isAtAll") == 1){
            dingModel.getAt().setAtAll(true);
        }else if(StrUtil.isNotBlank(config.getStr("mobiles"))){
            dingModel.getAt().setAtMobiles(config.getStr("mobiles").split(","));
        }

        HttpRequest request = HttpUtil.createPost(sign(config));
        request.body(JSONUtil.toJsonStr(dingModel));
        HttpResponse response  =  request.execute();
        log.info("发送结果{}",response);

        //记录发送日志
        MsgDdLog ddLog = new MsgDdLog();
        ddLog.setContent(content);
        ddLog.setCreateTime(new Date());
        ddLog.setResult(response.body());
        ddLog.setTemplateKey(dingSend.getKey());
        if(response.getStatus() == HttpStatus.HTTP_OK){
            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            if(jsonObject.getInt("errcode") == 0){
                ddLog.setStatus(1);//成功
            }
        }else {
            ddLog.setStatus(2);//失败            ddLog.setStatus(2);//失败

        }
        ddLogDao.insert(ddLog);
    }

    private String  sign (JSONObject config){
        try{

            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + config.getStr("ddSgin");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(config.getStr("ddSgin").getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");

            return String.format("%s&timestamp=%d&sign=%s",config.getStr("url"),timestamp,sign);
        }catch (Exception e){
            log.error("生成钉钉签名错误",e);
            throw new ServiceException("发送失败。");
        }
    }




}
