package cn.poria.message.service.im.tx.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.poria.commom.kafka.KafkaUtil;
import cn.poria.message.enums.txim.TxCallBackMsgCommandEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TXMsgCallBackService {

    public void callBackMsgResolve(JSONObject body) {
        String command = body.getStr("CallbackCommand");
        if(StrUtil.isBlank(command)){
            return;
        }
        TxCallBackMsgCommandEnum commandEnum = TxCallBackMsgCommandEnum.getEnum(command);
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
    }
}
