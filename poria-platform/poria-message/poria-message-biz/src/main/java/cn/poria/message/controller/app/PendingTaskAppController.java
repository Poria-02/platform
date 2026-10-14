package cn.poria.message.controller.app;

import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.vo.request.PendingTaskQueryModel;
import cn.poria.message.vo.response.PendingTaskVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.poria.message.service.MsgPendingTaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 代办任务(MsgPendingTask)表控制层
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

    /**
     * 查询用户代办列表
     * @param req
     * @return
     */
    @Operation(summary = "查询代办列表",description = "查询当前用户的待办")
    @GetMapping("/user/page")
    public R<IPage<PendingTaskVo>> userPendingTask(@Validated PendingTaskQueryModel req){
        return R.ok(msgPendingTaskService.userPendingTask(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "完成待办任务", description = "完成待办任务")
    public R update(@PathVariable("id") @Validated String id) {
        msgPendingTaskService.commitPendingTask(id);
        return R.ok();
    }

    @Operation(summary = "未完成数量", description = "查询当前用户指定类型的未完成数量")
    @GetMapping("/unDone/count")
    public R<Integer> unDoneCount(@RequestParam(required = false) Integer type){
        return R.ok(msgPendingTaskService.unDoneCount(type));
    }

	@GetMapping("/{pid}")
	@Operation(summary = "根据id查询详情", description = "根据id查询详情")
	public R<PendingTaskVo> findUser(@PathVariable("pid") @Validated String pid) {
		return R.ok(msgPendingTaskService.findDetails(pid));
	}

    @PostMapping("/allToRead")
    @Operation(summary = "一键已读公告和站内信", description = "无需传参，将当前用户未读的公告（98）和站内信（99）全部标记为已读")
    public R setAllToReadPendingTask() {
        msgPendingTaskService.setAllToReadPendingTask();
        return R.ok();
    }

}
