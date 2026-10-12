package cn.poria.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.config.YxxhProperties;
import cn.poria.message.dao.MsgYxxhInfoDao;
import cn.poria.message.entity.MsgYxxhInfo;
import cn.poria.message.entity.MsgYxxhLog;
import cn.poria.message.service.MsgYxxhInfoService;
import cn.poria.message.service.MsgYxxhLogService;
import cn.poria.message.util.YxxhUtil;
import cn.poria.message.vo.request.yxxh.YxxhAXBModel;
import cn.poria.message.vo.request.yxxh.YxxhPushModel;
import cn.poria.message.vo.request.yxxh.YxxhRealNameModel;
import cn.poria.message.vo.request.yxxh.YxxhUnbindModel;
import cn.poria.message.vo.response.YxxhResponseResult;
import cn.poria.upms.api.util.ParamResolver;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * (MsgYxxhInfo)表服务实现类
 *
 * @author wuyifan
 * @since 2024-03-25 16:15:25
 */
@Service("msgYxxhInfoService")
@Slf4j
public class MsgYxxhInfoServiceImpl extends ServiceImpl<MsgYxxhInfoDao, MsgYxxhInfo> implements MsgYxxhInfoService {

    @Autowired
    private YxxhProperties yxxhProperties;

    @Autowired
    private MsgYxxhLogService msgYxxhLogService;

    /**
     * @param: 云信小号AXB绑定
     * @author: wuyifan
     * @date:2024/3/25 17:35
     */
    @Override
    @SneakyThrows
    public String yxxhAXB(YxxhAXBModel model) {
        MsgYxxhInfo one = this.getOne(new LambdaQueryWrapper<MsgYxxhInfo>()
                .eq(MsgYxxhInfo::getBusinessId, model.getCustomerData()));
        if (ObjectUtil.isNotEmpty(one)){
            throw new ServiceException("业务已绑定云信小号");
        }
        if (ObjectUtil.equals(model.getBindNumberA(),model.getBindNumberB())){
            throw new ServiceException("手机参数异常");
        };
        String appId = yxxhProperties.getAppId();
        String token = yxxhProperties.getToken();
        String url = ParamResolver.getStr("API_DOMAIN") + "/message/api/msgYxxhInfo/yxxhPush";
        model.setCallbackUrl(url);
        log.info("云信小号绑定请求参数:{},appId:{},token:{}", model, appId, token);
        YxxhResponseResult response = YxxhUtil.httpsSendYxxhAXB(model, appId, token, yxxhProperties.getBaseUrl());
        if (!Objects.equals(response.getResult(), "000000")) {
            throw new ServiceException("云信小号绑定异常:" + response.getResult() + ",message:" + response.getMessage());
        }
        log.info("云信小号绑定结果:{}", response);
        MsgYxxhInfo info = new MsgYxxhInfo();
        info.setBindId(response.getBindId());
        info.setMiddleNumber(response.getMiddleNumber());
        info.setBusinessId(model.getCustomerData());
        info.setPatientMobile(model.getBindNumberA());
        info.setStaffMobile(model.getBindNumberB());
        this.save(info);
        return info.getMiddleNumber();

    }

    @Override
    @SneakyThrows
    @Transactional
    public void yxxhUnbind(YxxhUnbindModel model) {
        MsgYxxhInfo one = this.getOne(new LambdaQueryWrapper<MsgYxxhInfo>()
                .eq(MsgYxxhInfo::getBusinessId, model.getCustomerData()));
        if (ObjectUtil.isEmpty(one)) {
            throw new ServiceException("暂无该云信小号绑定");
        }
        model.setBindNumberA(one.getPatientMobile());
        model.setBindNumberB(one.getStaffMobile());
        model.setMiddleNumber(one.getMiddleNumber());
        model.setMode("0");
        String appId = yxxhProperties.getAppId();
        String token = yxxhProperties.getToken();
        log.info("云信小号解绑请求参数:{},appId:{},token:{}", model, appId, token);
        YxxhResponseResult response = YxxhUtil.httpsSendYxxhUnbind(model, appId, token, yxxhProperties.getBaseUrl());
        if (!Objects.equals(response.getResult(), "000000")) {
            throw new ServiceException("云信小号解绑异常:" + response.getResult() + ",message:" + response.getMessage());
        }
        log.info("云信小号解绑结果:{}", response);
        this.remove(new LambdaUpdateWrapper<MsgYxxhInfo>().eq(MsgYxxhInfo::getMiddleNumber, model.getMiddleNumber()));

    }


    /**
     * 隐私通话实名认证
     *
     * @param yxxhRealNameModel
     */
    @Override
    @SneakyThrows
    public String realName(YxxhRealNameModel yxxhRealNameModel) {
        AES aes = new AES(StrUtil.sub(yxxhProperties.getToken(),0,16).getBytes());
        JSONObject param = new JSONObject();
        param.putOnce("phoneNo",aes.encryptBase64(yxxhRealNameModel.getMobile()));
        param.putOnce("name",aes.encryptBase64(yxxhRealNameModel.getName()));
        param.putOnce("identifyCardNO",aes.encryptBase64(yxxhRealNameModel.getIdCard()));
        String url = ParamResolver.getStr("API_DOMAIN") + "/message/api/msgYxxhInfo/realNameAUth/result";
        param.putOnce("callbackUrl",url);
        log.info("隐私通话实名请求:{}", JSONUtil.toJsonStr(param));
        param.putOnce("photo",aes.encryptBase64(yxxhRealNameModel.getPhoto()));
        JSONObject result = YxxhUtil.realNameAuth(param, yxxhProperties.getAppId(), yxxhProperties.getToken(), yxxhProperties.getBaseUrl());
        if (!Objects.equals(result.getStr("result"), "000000")) {
            throw new ServiceException("实名认证错误,message:" + result.getStr("message"));
        }
        return result.getStr("authId");
    }

    @Override
    @Transactional
    public void yxxhPush(YxxhPushModel model) {
        /** 呼叫状态码
         * 0 正常
         * 1 空号
         * 2 用户忙
         * 3 拒接
         * 4 无人接听
         * 5 无法接通
         * 6 呼叫被拦截 呼叫被运营商拦截
         * 7 关机
         * 8 停机
         * 9 其他 呼叫失败或无法识别的状态
         * 10 主叫挂机
         * 11 被叫挂机
         * 12 主叫放弃
         * 13 小号状态异常
         * 14 订单超期
         * 15 平台异常
         */
        MsgYxxhLog msgYxxhLog = new MsgYxxhLog();
        BeanUtil.copyProperties(model, msgYxxhLog);
        msgYxxhLog.setBusinessId(model.getCustomerData());
        msgYxxhLogService.save(msgYxxhLog);
    }

}

