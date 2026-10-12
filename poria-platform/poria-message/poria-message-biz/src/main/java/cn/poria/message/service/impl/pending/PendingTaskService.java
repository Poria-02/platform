package cn.poria.message.service.impl.pending;

import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.enums.PendingTaskType;
import cn.poria.message.vo.request.PendingTaskModel;

public interface PendingTaskService {

     public boolean support(PendingTaskType type);

     public MsgPendingTask save(PendingTaskModel model);
}
