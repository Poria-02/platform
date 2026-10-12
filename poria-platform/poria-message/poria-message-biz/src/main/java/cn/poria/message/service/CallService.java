package cn.poria.message.service;

import cn.poria.message.vo.request.outbound.OutBoundModel;

public interface CallService {

    public void outBound(OutBoundModel model);

    Object boundResult(String businessId);

    void callResult(String request);
}
