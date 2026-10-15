package cn.poria.message.service.im.tx.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.message.enums.txim.TxMsgUrlConstant;
import cn.poria.message.service.im.tx.config.TxIMProperties;
import cn.poria.message.service.im.tx.request.GroupSystemMsgRequest;
import cn.poria.message.service.im.tx.request.P2PMsgRecordRequest;
import cn.poria.message.service.im.tx.request.P2PMsgRequest;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import cn.poria.message.service.im.tx.util.TxIMMsgWrapUtil;
import cn.poria.message.vo.request.im.IMMsgSend;
import cn.poria.message.vo.request.im.IMPush;
import cn.poria.message.vo.request.im.ChatLogModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class TxIMP2PService {


    @Autowired
    private TxIMProperties txIMProperties;

    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;


    public void sendP2PMsg(IMMsgSend imMsgSend) {
        P2PMsgRequest request = new P2PMsgRequest();
        request.setFromAccount(imMsgSend.getFrom());
        request.setToAccount(imMsgSend.getTo());
        request.setMsgRandom(ThreadLocalRandom.current().nextLong(1L << 32));
        request.setMsgBody(Arrays.asList(TxIMMsgWrapUtil.wrapBody(imMsgSend.getBody())));
        request.setOfflinePushInfo(imMsgSend.getPushInfo());
        request.setCloudCustomData(imMsgSend.getCloudCustomData());
        JSONObject result = txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.P2P_MSG_URL,txIMProperties.getAdminAccountName(),JSONUtil.toJsonStr(request));
    }

    public JSONArray findP2PMsgHistory(ChatLogModel model){
        P2PMsgRecordRequest request = new P2PMsgRecordRequest();
        request.setStartTime(model.getBeginTime());
        request.setEndTime(model.getEndTime());
        request.setFrom(model.getFrom());
        request.setTo(model.getTo());
        request.setMsgCount(model.getLimit());
        JSONObject result = txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.P2P_MSG_RECORD, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
        return result.getJSONArray("MsgList");
    }


    public void sendSystemMsg(GroupSystemMsgRequest groupMsgRequest){
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_SYSTEM_MSG, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupMsgRequest));
    }

    public String getUserSig(){
        Assert.notBlank(SecurityUtils.getSId(),"未查询到当前账户信息");
        return txIMMsgSendUtil.getSig(SecurityUtils.getSId());
    }


    public void pushServerMsg(@RequestBody IMPush imPush) {
        List<String> failedAccounts = new ArrayList<>();
        for (String to : imPush.getTo()) {
            try{
                IMMsgSend imMsgSend = new IMMsgSend();
                imMsgSend.setTo(to);
                imMsgSend.setBody(imPush.getMsgBody());
                imMsgSend.setPushInfo(imPush.getPushInfo());
                this.sendP2PMsg(imMsgSend);
            }catch (Exception e){
                log.error("推送异常:",e);
                failedAccounts.add(to);
            }
        }
        if (!failedAccounts.isEmpty()) {
            throw new ServiceException("IM推送失败，接收账号：" + String.join(",", failedAccounts));
        }
    }
}
