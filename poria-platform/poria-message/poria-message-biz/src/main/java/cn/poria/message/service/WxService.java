package cn.poria.message.service;

import cn.poria.message.vo.request.WxMsgSend;
import cn.poria.message.vo.request.ma.MaAccountMessageModel;
import cn.poria.message.vo.response.ma.MaAccountMessageVo;
import org.springframework.web.bind.annotation.RequestBody;

public interface WxService {


    /**
     * 发送微信模版消息
     * @param msgSend
     */
    void sendTemplateMsg(WxMsgSend msgSend);

    /**
     * 给指定用户关注的所有公众号推送消息
     * @param msgSend
     */
    void sendAllWxTemplateMsg(WxMsgSend msgSend);
    /**
     * 发送小程序订阅消息
     * @param wxMsgSend
     */
    void sendMaSubScribeMsg(WxMsgSend wxMsgSend);

    /**
     * 根据过滤条件筛选小程序账号
     * @param model
     * @return
     */
    MaAccountMessageVo findMaAccountInfo(@RequestBody MaAccountMessageModel model);
}
