package cn.poria.message.service.impl.pending;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.util.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.enums.PendingTaskType;
import cn.poria.message.service.MsgPendingTaskService;
import cn.poria.message.vo.request.PendingTaskModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service("staffImMsgService")
public class StaffImMsgServiceImpl implements PendingTaskService{

    @Lazy
    @Autowired
    private MsgPendingTaskService msgPendingTaskService;

    @Override
    public boolean support(PendingTaskType type) {
        return PendingTaskType.NULL.equals(type);
    }

    @Override
    public MsgPendingTask save(PendingTaskModel model) {

        Assert.isTrue(StrUtil.isNotBlank(model.getRelateUserId()),"关联用户ID不能为空");
        Assert.isTrue(StrUtil.isNotBlank(model.getRelateUserName()),"关联用户名称不能为空");
        Assert.isTrue(StrUtil.isNotBlank(model.getRelateUserHead()),"关联用户头像不能为空");

        /*
            医生im消息
            如果同一个医生发的消息,不额外创建新的待办任务,直接更新原来的待办任务
         */
        MsgPendingTask msgPendingTask = msgPendingTaskService.getOne(new LambdaQueryWrapper<MsgPendingTask>()
                .eq(MsgPendingTask::getRelateUserId,model.getRelateUserId())
                .eq(MsgPendingTask::getUserId,model.getUserId())
                .eq(MsgPendingTask::getStatus,"0")
                .eq(MsgPendingTask::getType,model.getType().getCode()));
        if(msgPendingTask != null){
            msgPendingTask.setTitle(model.getTitle());
            msgPendingTask.setCount(msgPendingTask.getCount() + 1);
            msgPendingTask.setTitle(StrUtil.sub(model.getTitle(),0,200));
            msgPendingTaskService.updateById(msgPendingTask);
        }else {
            msgPendingTask = new MsgPendingTask();
            BeanUtils.copyProperties(model,msgPendingTask);
            msgPendingTask.setType(model.getType().getCode());
            msgPendingTask.setStatus(0);
            msgPendingTask.setCount(1);
            msgPendingTaskService.save(msgPendingTask);
        }
        return msgPendingTask;
    }
}
