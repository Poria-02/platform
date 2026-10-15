package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import cn.poria.message.service.CenterAnnouncementService;
import cn.poria.message.vo.request.center.CenterDraftModel;
import cn.poria.message.vo.request.center.CenterQueryModel;
import cn.poria.message.vo.request.center.CenterSendModel;
import cn.poria.message.vo.request.center.CenterStatusModel;
import cn.poria.message.vo.response.center.AnnouncementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * 管理平台公示公告控制层
 */
@RestController
@RequestMapping("/api/announcement")
@Tag(name = "【Api】- 公示公告")
public class AnnouncementController {

    /**
     * 公示公告服务对象
     */
    @Resource
    private CenterAnnouncementService centerAnnouncementService;

    /**
     * 分页查询公示公告
     *
     * @param query 查询条件
     * @return 公示公告分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询公示公告记录", description = "按传入的公告状态查询，默认查询已发布的公示公告记录")
    public R<IPage<AnnouncementVo>> page(@Validated CenterQueryModel query) {
        return R.ok(centerAnnouncementService.page(query));
    }

    /**
     * 通过ID查询公示公告详情
     *
     * @param id 公告ID
     * @return 公示公告详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询公示公告记录", description = "通过ID查询公示公告详情")
    public R<AnnouncementVo> detail(@PathVariable String id) {
        return R.ok(centerAnnouncementService.detail(id));
    }

    /**
     * 新增公示公告草稿
     *
     * @param draft 公示公告内容
     * @return 公告ID
     */
    @PostMapping
    @Operation(summary = "新增公示公告", description = "新增公告并保存为草稿 (权限：message_announcement_add)")
    @PreAuthorize("@pms.hasPermission('message_announcement_add')")
    public R<String> create(@RequestBody @Validated(Create.class) CenterDraftModel draft) {
        return R.ok(centerAnnouncementService.create(draft));
    }

    /**
     * 修改公示公告内容
     *
     * @param draft 公告ID及公示公告内容
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改公示公告", description = "修改后恢复为草稿，原待办阅读状态保留，重新发送会生成新待办 (权限：message_announcement_edit)")
    @PreAuthorize("@pms.hasPermission('message_announcement_edit')")
    public R<Void> update(@RequestBody @Validated(Update.class) CenterDraftModel draft) {
        centerAnnouncementService.update(draft);
        return R.ok();
    }

    /**
     * 通过待办功能发送公示公告
     *
     * @param model 公告ID及接收人用户ID列表
     * @return 待办任务ID列表
     */
    @PostMapping("/send")
    @Operation(summary = "发送公示公告", description = "向前端传入的接收人ID列表逐人发送公示公告 (权限：message_announcement_send)")
    @PreAuthorize("@pms.hasPermission('message_announcement_send')")
    public R<List<String>> send(@RequestBody @Validated CenterSendModel model) {
        return R.ok(centerAnnouncementService.send(model));
    }

    /**
     * 恢复草稿或撤回公示公告
     *
     * @param request 公告ID及状态请求，0-草稿，2-已撤回
     * @return 状态修改结果
     */
    @PutMapping("/status")
    @Operation(summary = "修改公告发布状态", description = "0-恢复草稿，2-撤回；发送公告使用独立发送接口 (权限：message_announcement_edit)")
    @PreAuthorize("@pms.hasPermission('message_announcement_edit')")
    public R<Void> status(@RequestBody @Validated CenterStatusModel request) {
        centerAnnouncementService.status(request);
        return R.ok();
    }

    /**
     * 通过ID删除公示公告
     *
     * @param id 公告ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "通过ID删除公示公告", description = "通过ID删除公示公告记录 (权限：message_announcement_del)")
    @PreAuthorize("@pms.hasPermission('message_announcement_del')")
    public R<Void> delete(@PathVariable String id) {
        centerAnnouncementService.delete(id);
        return R.ok();
    }
}
