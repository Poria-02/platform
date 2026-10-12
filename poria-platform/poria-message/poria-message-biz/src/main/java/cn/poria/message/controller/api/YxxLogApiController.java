package cn.poria.message.controller.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.poria.common.core.util.R;
import cn.poria.message.entity.MsgYxxhLog;
import cn.poria.message.service.MsgYxxhLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "云信小号通话记录")
@RequestMapping("/api/msgYxxhLog")
public class YxxLogApiController {

    @Autowired
    private MsgYxxhLogService msgYxxhLogService;

    @GetMapping("/{businessId}")
    @Operation(summary = "业务id查询查询通话记录", description = "业务id查询查询通话记录(message_yxxlog_get)")
    @PreAuthorize("@pms.hasPermission('message_yxxlog_get')")
    public R<List<MsgYxxhLog>> YxxhAXB(@PathVariable String businessId) {

        List<MsgYxxhLog> list = this.msgYxxhLogService.list(new LambdaQueryWrapper<MsgYxxhLog>().eq(MsgYxxhLog::getBusinessId, businessId)
                .ne(MsgYxxhLog::getRecUrl, ""));
        if (CollectionUtil.isNotEmpty(list)) {
            return R.ok(list);
        }
        return R.ok();

    }
}
