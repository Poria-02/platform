package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgPendingTask;
import cn.poria.message.service.MsgPendingTaskService;
import cn.poria.message.vo.request.PendingTaskModel;
import cn.poria.message.vo.request.PendingTaskQueryModel;
import cn.poria.message.vo.response.PendingTaskVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
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
@RestController
@Tag(name = "【Api】- 代办任务管理")
@RequestMapping("/api/pending/task")
public class PendingTaskApiController {

    /**
     * 待办任务服务对象
     */
    @Resource
    private MsgPendingTaskService msgPendingTaskService;

    /**
     * 新增待办任务
     *
     * @param msgPendingTask 待办任务内容
     * @return 新增结果
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('message_pending_add')")
    @Operation(summary = "新增代办任务", description = "新增待办任务 (权限：message_pending_add)")
    public R insert(@RequestBody @Validated PendingTaskModel msgPendingTask) {
        msgPendingTaskService.savePendingTask(msgPendingTask);
        return R.ok();
    }

    /**
     * 分页查询当前用户的待办列表
     *
     * @param query 查询条件
     * @return 待办任务分页数据
     */
    @GetMapping("/user/page")
    @Operation(summary = "查询我的待办列表", description = "查询当前用户的待办任务")
    public R<IPage<PendingTaskVo>> page(@Validated PendingTaskQueryModel query) {
        return R.ok(msgPendingTaskService.userPendingTask(query));
    }

    /**
     * 查询当前用户的未完成待办数量
     *
     * @param type 待办类型，98-公告，99-站内信，不传则统计全部类型
     * @return 未完成数量
     */
    @GetMapping("/unDone/count")
    @Operation(summary = "我的未完成数量", description = "查询当前用户状态为0的待办数量")
    public R<Integer> unDoneCount(@RequestParam(required = false) Integer type) {
        return R.ok(msgPendingTaskService.unDoneCount(type));
    }

    /**
     * 通过ID查询当前用户的待办详情
     *
     * @param id 待办任务ID
     * @return 待办任务详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询我的待办详情", description = "查询我的待办详情")
    public R<PendingTaskVo> detail(@PathVariable String id) {
        PendingTaskVo task = msgPendingTaskService.findDetails(id);

        return R.ok(task);
    }

    /**
     * 完成当前用户的待办任务
     *
     * @param id 待办任务ID
     * @return 完成结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "完成我的待办任务", description = "完成当前用户的待办任务并记录完成时间")
    public R<Void> complete(@PathVariable String id) {
        msgPendingTaskService.commitPendingTask(id);
        return R.ok();
    }

    /**
     * 一键已读当前用户的公告和站内信
     *
     * @return 完成结果
     */
    @PostMapping("/allToRead")
    @Operation(summary = "一键已读公告和站内信", description = "无需传参，将当前用户未读的公告（98）和站内信（99）全部标记为已读")
    public R<Void> completeAll() {
        msgPendingTaskService.setAllToReadPendingTask();
        return R.ok();
    }
}
