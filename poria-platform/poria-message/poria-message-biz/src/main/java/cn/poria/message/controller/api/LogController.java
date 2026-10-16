package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.service.LogService;
import cn.poria.message.vo.request.LogQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理平台消息发送记录控制层
 */
@RestController
@Tag(name = "【Api】- 消息发送记录log")
@RequestMapping("/api/log")
public class LogController {

    @Resource
    private LogService logService;

    @GetMapping("/sms")
    @PreAuthorize("@pms.hasPermission('message_log_sms')")
    @Operation(summary = "分页查询短信发送记录", description = "分页查询短信发送记录 (权限：message_log_sms)")
    public R<IPage<MsgSmsLog>> smsLogs(LogQuery query) {
        return R.ok(logService.selectSmsLogs(query));
    }

    @GetMapping("/wxTemplate")
    @PreAuthorize("@pms.hasPermission('message_log_wx')")
    @Operation(summary = "分页查询微信公众号模板消息发送记录", description = "分页查询微信公众号模板消息发送记录 (权限：message_log_wx)")
    public R<IPage<MsgWxLog>> wxTemplate(LogQuery query) {
        return R.ok(logService.selectWxTemplate(query));
    }
}
