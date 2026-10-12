package cn.poria.message.service;

import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.vo.request.PendingTaskModel;
import cn.poria.message.vo.request.PendingTaskQueryModel;
import cn.poria.message.vo.request.template.StationLetterModel;
import cn.poria.message.vo.response.PendingTaskVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 代办任务(MsgPendingTask)表服务接口
 *
 * @author makejava
 * @since 2020-07-13 15:54:27
 */
public interface MsgPendingTaskService extends IService<MsgPendingTask> {

    IPage<PendingTaskVo> userPendingTask(PendingTaskQueryModel queryModel);

    void savePendingTask(PendingTaskModel msgPendingTask);

    /**
     * 完成待办任务
     * @param id
     */
    void commitPendingTask(String  id);

    /**
     * 发送站内信
     * @param model
     */
    String sendStationLetter(StationLetterModel model);

    /**
     * 未完成数量
     * @return
     */
    Integer unDoneCount();

	/**
	 * 根据id查询详情
	 * @return
	 */
	MsgPendingTask  findDetails(String id);

    void setAllToReadPendingTask();
}
