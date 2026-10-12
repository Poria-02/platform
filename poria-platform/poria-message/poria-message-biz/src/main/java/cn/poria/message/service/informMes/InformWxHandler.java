package cn.poria.message.service.informMes;

import cn.hutool.core.collection.CollectionUtil;
import cn.poria.message.service.WxService;
import cn.poria.message.vo.request.InformMesModel;
import cn.poria.message.vo.request.WxMsgSend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InformWxHandler implements InformMessageHandler {

    private final WxService wxService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerInformMes(InformMesModel model) {
        log.info("微信小程序通知。。。");

        WxMsgSend wxMsgSend = new WxMsgSend();
        wxMsgSend.setKey(model.getMsgTemplateKey());//模板ID
        wxMsgSend.setAccountId(model.getAccountId());//用户ID
        wxMsgSend.setOrgId(model.getOrgId().longValue());//组织ID
        wxMsgSend.setSource("weixin_ma");
        if(CollectionUtil.isNotEmpty(model.getParams())) {
            wxMsgSend.setParam(model.getParams());
        }

        wxService.sendMaSubScribeMsg(wxMsgSend);
    }
}
