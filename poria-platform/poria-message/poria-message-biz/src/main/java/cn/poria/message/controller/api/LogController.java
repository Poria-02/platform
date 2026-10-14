package cn.poria.message.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.vo.response.PendingTaskVo;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.service.LogService;
import cn.poria.message.vo.request.LogQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "【Api】- 消息发送记录log")
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/stationLetter")
    @Operation(summary = "站内信记录", description = "通过待办功能分页查询当前用户类型99的站内信记录")
    public R<IPage<PendingTaskVo>> stationLetters(LogQuery query) {
        return R.ok(logService.selectStationLetters(query));
    }

    @Operation(summary = "短信消息记录",description = "短信发送记录(message_log_sms)")
    @GetMapping("/sms")
    @PreAuthorize("@pms.hasPermission('message_log_sms')")
    public R<IPage<MsgSmsLog>> smsLogs (LogQuery query){
        return R.ok(logService.selectSmsLogs(query));
    }

    @Operation(summary = "微信模版消息记录",description = "微信模版消息记录(message_log_wx)")
    @GetMapping("/wxTemplate")
    @PreAuthorize("@pms.hasPermission('message_log_wx')")
    public R<IPage<MsgWxLog>> wxTemplate (LogQuery query){
        return R.ok(logService.selectWxTemplate(query));
    }

}
