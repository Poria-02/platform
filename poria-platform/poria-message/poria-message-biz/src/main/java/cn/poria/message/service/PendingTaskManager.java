package cn.poria.message.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.enums.im.type.IMMsgTypeCustom;
import cn.poria.message.service.im.tx.service.TxIMP2PService;
import cn.poria.message.service.impl.pending.PendingTaskService;
import cn.poria.message.vo.request.PendingTaskModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class PendingTaskManager {

    @Lazy
    @Autowired
    private MsgPendingTaskService msgPendingTaskService;

    @Autowired
    private TxIMP2PService txIMP2PService;

    List<PendingTaskService> pendingTaskServices;

    public PendingTaskManager(List<PendingTaskService> pendingTaskServices){
        this.pendingTaskServices = pendingTaskServices;
    }

    public MsgPendingTask savePendingTask(PendingTaskModel model){
        log.info("保存待办任务:{}", JSONUtil.toJsonStr(model));
        MsgPendingTask mpt = null;

        //适配不同待办任务
        if(CollectionUtil.isNotEmpty(pendingTaskServices)){
            for(PendingTaskService pendingTaskService : pendingTaskServices){
                if(pendingTaskService.support(model.getType())){
                    mpt =  pendingTaskService.save(model);
                    break;
                }
            }
        }

        //默认待办任务处理
        if(mpt == null){
            mpt = new MsgPendingTask();
            BeanUtils.copyProperties(model,mpt);
            mpt.setType(model.getType().getCode());
            mpt.setStatus(0);
            msgPendingTaskService.save(mpt);
        }
        if (ObjectUtil.isNotNull(model.getImPush())){
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", "NOTIFY_MESSAGE_LETTER");
            map.put("businessId", mpt.getId());
            model.getImPush().setMsgBody(IMMsgTypeCustom.builder().content(map).build());
            log.info("站内信推送im消息:{}",JSONUtil.toJsonStr(model.getImPush()));
            txIMP2PService.pushServerMsg(model.getImPush());
        }
        return mpt;
    }


}
