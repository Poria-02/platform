package cn.poria.message.service;

import cn.poria.message.vo.request.center.StationLetterModel;

import java.util.List;

/**
 * 管理平台站内信发送服务接口
 */
public interface CenterStationService {

    /**
     * 发送站内信
     *
     * @param model 接收人、模板key及模板参数
     * @return 待办任务ID列表，停用模板不发送
     */
    List<String> sendStationLetter(StationLetterModel model);
}
