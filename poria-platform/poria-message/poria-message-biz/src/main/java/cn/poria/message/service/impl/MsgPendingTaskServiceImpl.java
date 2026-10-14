package cn.poria.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.TemplateUtil;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.message.enums.im.type.IMMsgTypeCustom;
import cn.poria.message.enums.im.type.IMMsgTypeTXT;
import cn.poria.message.service.im.tx.service.TxIMP2PService;
import cn.poria.message.vo.request.PendingTaskQueryModel;
import cn.poria.message.vo.request.im.IMPush;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgPendingTaskDao;
import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.enums.PendingTaskType;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.service.MsgPendingTaskService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.PendingTaskManager;
import cn.poria.message.vo.request.PendingTaskModel;
import cn.poria.message.vo.request.template.StationLetterModel;
import cn.poria.message.vo.response.PendingTaskVo;
import cn.poria.message.vo.response.template.TemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
        String accountId = SecurityUtils.getUser().getId();
        Page page = new Page(queryModel.getCurrent(),queryModel.getSize());
        page.addOrder(OrderItem.desc("create_time"));
        Page<MsgPendingTask> result =  this.page(page,new LambdaQueryWrapper<MsgPendingTask>()
                .eq(MsgPendingTask::getUserId,accountId)
                .eq(queryModel.getStatus() != null,MsgPendingTask::getStatus,queryModel.getStatus())//查询未确认的
                .eq(queryModel.getType()!=null ,MsgPendingTask::getType,queryModel.getType()));

        return result.convert(pt -> {
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
     * 发送站内信
     * @param model
     */
    @Override
    public String sendStationLetter(StationLetterModel model) {

        PendingTaskModel ptm = new PendingTaskModel();
        ptm.setUserId(model.getUserId());
        ptm.setBusinessId(model.getBusinessId());

        TemplateVo templateVo = msgTemplateService.selectByKey(model.getTemplateKey());
        Assert.notNull(templateVo,"模版不存在");
        Assert.isTrue(TemplateType.STATION.getCode().equals(templateVo.getType()), "模板类型必须为站内信");
        if(templateVo.getStatus() != 1){
            return null;
        }

        JSONObject jsonObject = JSONUtil.parseObj(templateVo.getContent());
        ptm.setTitle(TemplateUtil.process(jsonObject.getStr("title"),model.getParam()));
        ptm.setContent(TemplateUtil.process(jsonObject.getStr("content"),model.getParam()));
        ptm.setRedirectType(jsonObject.getInt("type"));
        ptm.setUrl(TemplateUtil.process(jsonObject.getStr("params"),model.getParam()));
        ptm.setType(PendingTaskType.STATION_LETTER);
        ptm.setImPush(model.getImPush());

        MsgPendingTask msgPendingTask = pendingTaskManager.savePendingTask(ptm);
        return msgPendingTask.getId();
    }

    /**
     * 完成待办任务
     *
     * @param id
     */
    @Override
    public void commitPendingTask(String id) {

        MsgPendingTask task = this.getById(id);

        if(task == null){
            return;
        }
        Assert.isTrue(StrUtil.equals(task.getUserId(),SecurityUtils.getSId()),"权限不足");


        task.setStatus(1);
        task.setCommitTime(new Date());
        this.updateById(task);
    }


    @Override
    public Integer unDoneCount() {
        String accountId = SecurityUtils.getSId();
        return (int)this.count(new LambdaQueryWrapper<MsgPendingTask>().eq(MsgPendingTask::getUserId,accountId)
                .eq(MsgPendingTask::getStatus,0));
    }

	@Override
	/*
	 * 根据id查询详情
	 * */
	public MsgPendingTask  findDetails(String id) {
		MsgPendingTask pendingTask = this.getOne(new LambdaQueryWrapper<MsgPendingTask>()
				.eq(MsgPendingTask::getUserId, SecurityUtils.getUser().getId())
				.eq(MsgPendingTask::getId, id));
		return pendingTask;
	}

    @Override
    public void setAllToReadPendingTask() {
        List<MsgPendingTask> list = this.list(new LambdaQueryWrapper<MsgPendingTask>()
                        .eq(MsgPendingTask::getStatus, 0)
                        .eq(MsgPendingTask::getUserId, SecurityUtils.getSId()))
                .stream().peek(task -> task.setStatus(1)).toList();

        int batchSize = 500;
        int ceil = (int) Math.ceil((double) list.size() / batchSize);
        for (int i = 0; i < ceil; i++){
            int fromIndex = i * batchSize;
            int toIndex = Math.min((i + 1) * batchSize, list.size());
            List<MsgPendingTask> msgPendingTasks = list.subList(fromIndex, toIndex);
            this.updateBatchById(msgPendingTasks);
        }
    }
}
