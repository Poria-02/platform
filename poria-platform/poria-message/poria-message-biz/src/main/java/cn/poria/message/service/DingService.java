package cn.poria.message.service;

import cn.poria.message.vo.request.DingSend;

public interface DingService  {

    /**
     * 发送钉钉文本消息
     * @param dingSend
     */
    void sendDingText(DingSend dingSend);
}
