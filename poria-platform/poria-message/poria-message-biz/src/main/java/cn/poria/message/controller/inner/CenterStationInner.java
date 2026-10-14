package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.CenterStationService;
import cn.poria.message.vo.request.center.StationLetterModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 服务间站内信模板发送控制层
 */
@RestController
@RequestMapping({"/inner/station/letter", "/inner/pending/task"})
@Tag(name = "【Inner】- 站内信")
public class CenterStationInner {

    /**
     * 站内信服务对象
     */
    @Resource
    private CenterStationService centerStationService;

    /**
     * 使用通用模板发送站内信，兼容原待办站内信发送入口
     *
     * @param model 接收人、模板key及模板参数
     * @return 待办任务ID列表
     */
    @Inner
    @PostMapping({"/sendStationLetter", "/stationLetter"})
    @Operation(summary = "发送站内信", description = "发送站内信")
    public R<List<String>> sendStationLetter(@RequestBody @Validated StationLetterModel model) {
        return R.ok(centerStationService.sendStationLetter(model));
    }

}
