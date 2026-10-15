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
 * App端待办任务控制层
 *
 * @author makejava
 * @since 2020-07-13 15:54:27
 */
@RestController
@Tag(name = "【App】- 代办任务管理")
@RequestMapping("/app/pending/task")
public class PendingTaskAppController {

    /**
     * 待办任务服务对象
     */
    @Resource
    private MsgPendingTaskService msgPendingTaskService;

    /**
     * 分页查询当前用户的待办列表
     *
     * @param req 分页及筛选条件
     * @return 待办任务分页数据
     */
    @GetMapping("/user/page")
    @Operation(summary = "分页查询当前用户的待办列表", description = "分页查询当前用户的待办列表")
    public R<IPage<PendingTaskVo>> userPendingTask(@Validated PendingTaskQueryModel req) {
        return R.ok(msgPendingTaskService.userPendingTask(req));
    }

    /**
     * 完成当前用户的待办任务
     *
     * @param id 待办任务ID
     * @return 完成结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "完成当前用户的待办任务", description = "完成当前用户的待办任务")
    public R update(@PathVariable("id") @Validated String id) {
        msgPendingTaskService.commitPendingTask(id);
        return R.ok();
    }

    /**
     * 查询当前用户的未完成待办数量
     *
     * @param type 待办类型，98-公告，99-站内信，不传则统计全部类型
     * @return 未完成数量
     */
    @GetMapping("/unDone/count")
    @Operation(summary = "查询当前用户的未完成待办数量", description = "查询当前用户的未完成待办数量")
    public R<Integer> unDoneCount(@RequestParam(required = false) Integer type) {
        return R.ok(msgPendingTaskService.unDoneCount(type));
    }

    /**
     * 通过ID查询当前用户的待办详情
     *
     * @param pid 待办任务ID
     * @return 待办任务详情
     */
    @GetMapping("/{pid}")
    @Operation(summary = "通过ID查询当前用户的待办详情", description = "通过ID查询当前用户的待办详情")
    public R<PendingTaskVo> findUser(@PathVariable("pid") @Validated String pid) {
        return R.ok(msgPendingTaskService.findDetails(pid));
    }

    /**
     * 一键已读当前用户的公告和站内信
     *
     * @return 完成结果
     */
    @PostMapping("/allToRead")
    @Operation(summary = "一键已读当前用户的公告和站内信", description = "无需传参，将当前用户未读的公告（98）和站内信（99）全部标记为已读")
    public R setAllToReadPendingTask() {
        msgPendingTaskService.setAllToReadPendingTask();
        return R.ok();
    }
}
