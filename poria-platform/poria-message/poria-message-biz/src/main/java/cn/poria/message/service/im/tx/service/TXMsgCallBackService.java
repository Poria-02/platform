package cn.poria.message.service.im.tx.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.poria.commom.kafka.KafkaUtil;
import cn.poria.message.enums.txim.TxCallBackMsgCommandEnum;
import cn.poria.message.service.im.tx.config.TxIMProperties;
import cn.poria.message.service.im.tx.response.CallBackResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Slf4j
@Component
public class TXMsgCallBackService {

    @Resource
    private TxIMProperties txIMProperties;

    public CallBackResponse callBackMsgResolve(String appId, String command, String requestTime, String sign, JSONObject body) {
        if (txIMProperties.getSdkAppId() <= 0 || !String.valueOf(txIMProperties.getSdkAppId()).equals(appId)) {
            return CallBackResponse.fail("回调应用ID不匹配");
        }
        if (StrUtil.isBlank(txIMProperties.getCallbackToken())) {
            return CallBackResponse.fail("IM回调鉴权Token未配置");
        }
        if (StrUtil.isBlank(requestTime) || StrUtil.isBlank(sign)) {
            return CallBackResponse.fail("回调鉴权参数缺失");
        }
        long timestamp;
        try {
            timestamp = Long.parseLong(requestTime);
        } catch (NumberFormatException e) {
            return CallBackResponse.fail("回调时间戳无效");
        }
        long now = System.currentTimeMillis() / 1000L;
        if (timestamp < now - 60 || timestamp > now + 60) {
            return CallBackResponse.fail("回调时间戳已过期");
        }
        // 腾讯回调签名：SHA-256(Token + RequestTime)。
        String expectedSign = DigestUtil.sha256Hex(txIMProperties.getCallbackToken() + requestTime);
        if (!MessageDigest.isEqual(expectedSign.getBytes(StandardCharsets.US_ASCII), sign.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII))) {
            return CallBackResponse.fail("回调签名不匹配");
        }
        if (StrUtil.isBlank(command) || (StrUtil.isNotBlank(body.getStr("CallbackCommand")) && !command.equals(body.getStr("CallbackCommand")))) {
            return CallBackResponse.fail("回调命令不匹配");
        }
        body.set("CallbackCommand", command);
        TxCallBackMsgCommandEnum commandEnum = TxCallBackMsgCommandEnum.getEnum(command);
        if (commandEnum == null) {
            log.info("忽略未知腾讯IM回调命令：{}", command);
            return CallBackResponse.ok();
        }
        switch (commandEnum) {
            //用户状态 登录 / 注销
            case STATUS_CHANGE:
//                ActivemqUtil.sendToTopic("im_tencent_state_change",body.toString());
                break;
            case SEND_P2P_MSG_BEFORE: //发单聊消息之前回调
                KafkaUtil.send("im_tencent_p2p_msg_before_callback",body.toString());
                break;
            case P2P_MSG_BACK_AFTER: //单聊消息撤回后回调
                KafkaUtil.send("im_tencent_p2p_backAfter_callback",body.toString());
                break;
            case GROUP_CREATE_AFTER: //创建群组之后回调
                KafkaUtil.send("im_tencent_group_create_after_callback",body.toString());
                break;
            case SEND_GROUP_MSG_AFTER: //发送群消息之后回调
                KafkaUtil.send("im_tencent_group_sendMsgAfter_callback",body.toString());
                break;
            case NEW_MEMBER_ADD_AFTER: //新成员入群之后回调
                KafkaUtil.send("im_tencent_group_newMember_after_callback",body.toString());
                break;
            case MEMBER_OUT_AFTER: //群成员离开之后回调
                KafkaUtil.send("im_tencent_group_exitMember_after_callback",body.toString());
                break;
            case GROUP_PROFILE_CHANGE_AFTER: //群组资料变动之后回调
                KafkaUtil.send("im_tencent_group_profileChange_after_callback",body.toString());
                break;
            case GROUP_RECALL_AFTER: //撤回群消息之后回调
                KafkaUtil.send("im_tencent_group_recall_after_callback",body.toString());
                break;
            default:
                break;
        }
        return CallBackResponse.ok();
    }
}
