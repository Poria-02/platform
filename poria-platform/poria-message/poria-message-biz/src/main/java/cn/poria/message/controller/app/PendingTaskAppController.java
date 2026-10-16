package cn.poria.message.controller.app;

import cn.poria.common.core.util.R;
import cn.poria.message.service.MsgPendingTaskService;
import cn.poria.message.vo.request.PendingTaskQueryModel;
import cn.poria.message.vo.response.PendingTaskVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * App端待办任务控制层
 *
 * @author makejava
 * @since 2020-07-13 15:54:27
 */
@RestController
@Tag(name = "【App】- 代办任务管理")
@RequestMapping("/app/pending/task")
public class PendingTaskAppController {

    @Resource
    private MsgPendingTaskService msgPendingTaskService;

    @GetMapping("/user/page")
    @Operation(summary = "分页查询当前用户的待办列表", description = "分页查询当前用户的待办列表")
    public R<IPage<PendingTaskVo>> userPendingTask(@Validated PendingTaskQueryModel req) {
        return R.ok(msgPendingTaskService.userPendingTask(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "完成当前用户的待办任务", description = "完成当前用户的待办任务")
    public R update(@PathVariable("id") @Validated String id) {
        msgPendingTaskService.commitPendingTask(id);
        return R.ok();
    }

    @GetMapping("/unDone/count")
    @Operation(summary = "查询当前用户的未完成待办数量", description = "查询当前用户的未完成待办数量")
    public R<Integer> unDoneCount(@RequestParam(required = false) Integer type) {
        return R.ok(msgPendingTaskService.unDoneCount(type));
    }

    @GetMapping("/{pid}")
    @Operation(summary = "通过ID查询当前用户的待办详情", description = "通过ID查询当前用户的待办详情")
    public R<PendingTaskVo> findUser(@PathVariable("pid") @Validated String pid) {
        return R.ok(msgPendingTaskService.findDetails(pid));
    }

    @PostMapping("/allToRead")
    @Operation(summary = "一键已读当前用户的公告和站内信", description = "无需传参，将当前用户未读的公告（98）和站内信（99）全部标记为已读")
    public R setAllToReadPendingTask() {
        msgPendingTaskService.setAllToReadPendingTask();
        return R.ok();
    }
}
