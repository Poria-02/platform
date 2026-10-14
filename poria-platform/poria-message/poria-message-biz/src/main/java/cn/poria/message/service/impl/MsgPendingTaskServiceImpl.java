package cn.poria.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.message.vo.request.PendingTaskQueryModel;
import cn.poria.message.enums.PendingTaskType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgPendingTaskDao;
import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.service.MsgPendingTaskService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.PendingTaskManager;
import cn.poria.message.vo.request.PendingTaskModel;
import cn.poria.message.vo.response.PendingTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 代办任务(MsgPendingTask)表服务实现类
 *
 * @author makejava
 * @since 2020-07-13 15:54:27
 */
@Service
@Slf4j
public class MsgPendingTaskServiceImpl extends ServiceImpl<MsgPendingTaskDao, MsgPendingTask> implements MsgPendingTaskService {

    @Autowired
    private PendingTaskManager pendingTaskManager;

    @Autowired
    private MsgTemplateService msgTemplateService;

    @Override
    public IPage<PendingTaskVo> userPendingTask(PendingTaskQueryModel queryModel) {
        return this.page(new Page<>(queryModel.getCurrent(), queryModel.getSize()), new LambdaQueryWrapper<MsgPendingTask>()
                .eq(MsgPendingTask::getUserId, SecurityUtils.getSId())
                .eq(queryModel.getStatus() != null, MsgPendingTask::getStatus, queryModel.getStatus())
                .eq(queryModel.getType() != null, MsgPendingTask::getType, queryModel.getType())
                .and(StrUtil.isNotBlank(queryModel.getKeyword()), condition -> condition.like(MsgPendingTask::getTitle, queryModel.getKeyword())
                        .or().like(MsgPendingTask::getContent, queryModel.getKeyword()))
                .orderByDesc(MsgPendingTask::getCreateTime).orderByDesc(MsgPendingTask::getId)).convert(pt -> {
            PendingTaskVo taskVo = new PendingTaskVo();
            BeanUtils.copyProperties(pt,taskVo);
            return taskVo;
        });
    }

    @Override
    public void savePendingTask(PendingTaskModel msgPendingTask) {
        pendingTaskManager.savePendingTask(msgPendingTask);
    }


    /**
     * 完成待办任务
     *
     * @param id
     */
    @Override
    public void commitPendingTask(String id) {

        MsgPendingTask task = this.getById(id);

        if(ObjectUtil.isEmpty(task)){
            return;
        }
        Assert.isTrue(StrUtil.equals(task.getUserId(),SecurityUtils.getSId()),"权限不足");

        task.setStatus(1);
        task.setCommitTime(new Date());
        this.updateById(task);
    }


    @Override
    public Integer unDoneCount(Integer type) {
        return (int)this.count(new LambdaQueryWrapper<MsgPendingTask>().eq(MsgPendingTask::getUserId, SecurityUtils.getSId())
                .eq(type != null, MsgPendingTask::getType, type)
                .eq(MsgPendingTask::getStatus, 0));
    }

	@Override
	public PendingTaskVo findDetails(String id) {
        MsgPendingTask task = this.getOne(new LambdaQueryWrapper<MsgPendingTask>()
                .eq(MsgPendingTask::getId, id));
        if (task == null) {
            return null;
        }
        PendingTaskVo vo = new PendingTaskVo();
        BeanUtils.copyProperties(task, vo);
        return vo;
	}

    @Override
    public void setAllToReadPendingTask() {
        int announcement = PendingTaskType.ANNOUNCEMENT.getCode();
        int stationLetter = PendingTaskType.STATION_LETTER.getCode();
        this.update(new LambdaUpdateWrapper<MsgPendingTask>().eq(MsgPendingTask::getUserId, SecurityUtils.getSId())
                .in(MsgPendingTask::getType, announcement, stationLetter)
                .eq(MsgPendingTask::getStatus, 0)
                .set(MsgPendingTask::getStatus, 1).set(MsgPendingTask::getCommitTime, new Date()));
    }
}
