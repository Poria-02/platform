package cn.poria.message.controller.inner;

import cn.poria.message.service.MsgPendingTaskService;
import cn.poria.message.vo.request.PendingTaskModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.annotation.Resource;

/**
 * 服务间待办任务控制层
 *
 * @author makejava
 * @since 2020-07-13 15:54:27
 */
@Hidden
@RestController
@Tag(name = "代办任务管理")
@RequestMapping("/inner/pending/task")
public class PendingTaskInnerController {

    @Resource
    private MsgPendingTaskService msgPendingTaskService;

    @Inner
    @PostMapping
    @Operation(summary = "新增待办任务", description = "新增待办任务")
    public R insert(@RequestBody @Validated PendingTaskModel msgPendingTask) {
        msgPendingTaskService.savePendingTask(msgPendingTask);
        return R.ok();
    }

    @Inner
    @PutMapping("/{id}")
    @Operation(summary = "完成待办任务", description = "完成待办任务")
    public R update(@PathVariable("id") @Validated String id) {
        msgPendingTaskService.commitPendingTask(id);
        return R.ok();
    }
}
