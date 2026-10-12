package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.MsgPendingTaskService;
import cn.poria.message.vo.request.PendingTaskModel;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 代办任务(MsgPendingTask)表控制层
 *
 * @author makejava
 * @since 2020-07-13 15:54:27
 */
@Hidden
@RestController
@Tag(name = "【Api】- 代办任务管理")
@RequestMapping("/api/pending/task")
public class PendingTaskApiController {

    @Resource
    private MsgPendingTaskService msgPendingTaskService;


    @Inner
    @PostMapping
    @Operation(summary = "新增代办任务", description = "新增代办任务")
    @PreAuthorize("@pms.hasPermission('message_pending_add')" )
    public R insert(@RequestBody @Validated PendingTaskModel msgPendingTask) {
        msgPendingTaskService.savePendingTask(msgPendingTask);
        return R.ok();
    }





}
