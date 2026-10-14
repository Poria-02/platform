package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.message.service.CenterStationService;
import cn.poria.message.vo.request.center.StationLetterModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * 管理平台站内信发送控制层，查询和已读操作使用待办接口
 */
@RestController
@RequestMapping("/api/station/letter")
@Tag(name = "【Api】- 站内信")
public class CenterStationController {
    
    @Resource
    private CenterStationService centerStationService;

    @PostMapping("/sendStationLetter")
    @Operation(summary = "新增发送站内信", description = "选择类型2的通用模板，传入接收人ID列表和模板参数，为每个接收人生成类型99的待办任务 (权限：message_station_add)")
    @PreAuthorize("@pms.hasPermission('message_station_add')")
    public R<List<String>> sendStationLetter(@RequestBody @Validated StationLetterModel model) {
        return R.ok(centerStationService.sendStationLetter(model));
    }

}
