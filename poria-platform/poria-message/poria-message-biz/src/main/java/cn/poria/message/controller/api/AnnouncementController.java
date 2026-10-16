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

    @Resource
    private CenterAnnouncementService centerAnnouncementService;

    @GetMapping("/page")
    @Operation(summary = "分页查询公示公告记录", description = "按传入的公告状态查询，默认查询已发布的公示公告记录")
    public R<IPage<AnnouncementVo>> page(@Validated CenterQueryModel query) {
        return R.ok(centerAnnouncementService.page(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询公示公告记录", description = "通过ID查询公示公告详情")
    public R<AnnouncementVo> detail(@PathVariable String id) {
        return R.ok(centerAnnouncementService.detail(id));
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('message_announcement_add')")
    @Operation(summary = "新增公示公告", description = "新增公告并保存为草稿 (权限：message_announcement_add)")
    public R<String> create(@RequestBody @Validated(Create.class) CenterDraftModel draft) {
        return R.ok(centerAnnouncementService.create(draft));
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('message_announcement_edit')")
    @Operation(summary = "修改公示公告", description = "修改后恢复为草稿，原待办阅读状态保留，重新发送会生成新待办 (权限：message_announcement_edit)")
    public R<Void> update(@RequestBody @Validated(Update.class) CenterDraftModel draft) {
        centerAnnouncementService.update(draft);
        return R.ok();
    }

    @PostMapping("/send")
    @PreAuthorize("@pms.hasPermission('message_announcement_send')")
    @Operation(summary = "发送公示公告", description = "向前端传入的接收人ID列表逐人发送公示公告 (权限：message_announcement_send)")
    public R<List<String>> send(@RequestBody @Validated CenterSendModel model) {
        return R.ok(centerAnnouncementService.send(model));
    }

    @PutMapping("/status")
    @PreAuthorize("@pms.hasPermission('message_announcement_edit')")
    @Operation(summary = "修改公告发布状态", description = "0-恢复草稿，2-撤回；发送公告使用独立发送接口 (权限：message_announcement_edit)")
    public R<Void> status(@RequestBody @Validated CenterStatusModel request) {
        centerAnnouncementService.status(request);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('message_announcement_del')")
    @Operation(summary = "通过ID删除公示公告", description = "通过ID删除公示公告记录 (权限：message_announcement_del)")
    public R<Void> delete(@PathVariable String id) {
        centerAnnouncementService.delete(id);
        return R.ok();
    }
}
