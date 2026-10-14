package cn.poria.message.service;

import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.vo.request.PendingTaskModel;
import cn.poria.message.vo.request.PendingTaskQueryModel;
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
     *
     * @param id
     */
    void commitPendingTask(String id);

    /**
     * 当前用户未完成数量，站内信及公告对应未读数量
     *
     * @param type 待办类型，不传则统计全部类型
     * @return 未完成数量
     */
    Integer unDoneCount(Integer type);

    /**
     * 根据id查询详情
     *
     * @return
     */
    PendingTaskVo findDetails(String id);

    /**
     * 将当前用户的公告和站内信全部标记为已读
     */
    void setAllToReadPendingTask();
}
