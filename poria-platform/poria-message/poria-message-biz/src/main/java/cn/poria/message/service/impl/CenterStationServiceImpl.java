package cn.poria.message.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.TemplateUtil;
import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.enums.PendingTaskType;
import cn.poria.message.enums.TemplateType;
import cn.poria.message.service.CenterStationService;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.PendingTaskManager;
import cn.poria.message.vo.request.PendingTaskModel;
import cn.poria.message.vo.request.center.StationLetterModel;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理平台站内信发送服务实现类
 */
@Service
public class CenterStationServiceImpl implements CenterStationService {

    @Resource
    private PendingTaskManager pendingTaskManager;

    @Resource
    private MsgTemplateService msgTemplateService;

    /**
     * 发送站内信
     *
     * @param model 接收人、模板key及模板参数
     * @return 待办任务ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> sendStationLetter(StationLetterModel model) {

        PendingTaskModel ptm = new PendingTaskModel();
        ptm.setBusinessId(model.getBusinessId());

        TemplateVo templateVo = msgTemplateService.selectByKey(model.getTemplateKey());
        Assert.notNull(templateVo, "模版不存在");
        Assert.isTrue(TemplateType.STATION.getCode().equals(templateVo.getType()), "模板类型必须为站内信");
        if (!Integer.valueOf(1).equals(templateVo.getStatus())) {
            return null;
        }

        JSONObject jsonObject = JSONUtil.parseObj(templateVo.getContent());
        ptm.setTitle(TemplateUtil.process(jsonObject.getStr("title"), model.getParam()));
        ptm.setContent(TemplateUtil.process(jsonObject.getStr("content"), model.getParam()));
        ptm.setRedirectType(jsonObject.getInt("type"));
        ptm.setUrl(TemplateUtil.process(jsonObject.getStr("params"), model.getParam()));
        ptm.setType(PendingTaskType.STATION_LETTER);
        ptm.setImPush(model.getImPush());

        List<String> ids = new ArrayList<>();
        for (String userId : model.getUserIds().stream().map(String::trim).distinct().toList()) {
            ptm.setUserId(userId);
            MsgPendingTask msgPendingTask = pendingTaskManager.savePendingTask(ptm);
            ids.add(msgPendingTask.getId());
        }
        return ids;
    }
}
