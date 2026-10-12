package cn.poria.message.controller.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.api.RemoteStaffService;
import cn.poria.message.entity.MsgYxxhLog;
import cn.poria.message.service.MsgYxxhInfoService;
import cn.poria.message.vo.request.yxxh.YxxhAXBModel;
import cn.poria.message.vo.request.yxxh.YxxhPushModel;
import cn.poria.message.vo.request.yxxh.YxxhUnbindModel;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * (MsgYxxhInfo)表控制层
 *
 * @author wuyifan
 * @since 2024-03-25 16:15:24
 */
@RestController
@Slf4j
@Tag(name = "云信小号信息")
@RequestMapping("/api/msgYxxhInfo")
public class YxxhInfoApiController {

    @Autowired
    private MsgYxxhInfoService msgYxxhInfoService;

    @Autowired
    private RemoteStaffService remoteStaffService;

    @PostMapping("/yxxhAXB")
    @SysLog("云信小号AXB绑定")
    @Operation(summary = "云信小号AXB绑定", description = "云信小号AXB绑定")
    public R YxxhAXB(@RequestBody @Validated YxxhAXBModel yxxhAXBModel) {
        this.msgYxxhInfoService.yxxhAXB(yxxhAXBModel);
        return R.ok();
    }

    @PutMapping("/yxxhUnbind")
    @SysLog("云信小号AXB解绑")
    @Inner(value = false)
    @Operation(summary = "云信小号AXB解绑", description = "云信小号AXB解绑")
    public R YxxhUnbind(@RequestBody @Validated YxxhUnbindModel yxxhUnbindModel) {
        this.msgYxxhInfoService.yxxhUnbind(yxxhUnbindModel);
        return R.ok();
    }

    @PostMapping("/yxxhPush")
    @SysLog("云信小号推送结果")
    @Hidden
    @Inner(value = false)
    @Operation(summary = "云信小号推送结果", description = "云信小号推送结果")
    public JSONObject YxxhPush(@RequestBody Object object) {
        log.info("云信小号通话参数:{}", object);
        YxxhPushModel bean = BeanUtil.toBean(object, YxxhPushModel.class);
        Map object1 = (Map) object;
        bean.setAppId(object1.get("accountId").toString());
        this.msgYxxhInfoService.yxxhPush(bean);
        JSONObject entries = new JSONObject();
        entries.append("resultCode", "200");
        return entries;
    }

    @Inner(value = false)
    @Hidden
    @SysLog("云信小号实名认证结果")
    @PostMapping("/realNameAUth/result")
    public JSONObject realNameAuth(@RequestBody JSONObject object) {
        log.info("云信小号实名认证结果:{}", object);
        remoteStaffService.updateRealNameAuthResult(object, SecurityConstants.FROM_IN);
        JSONObject entries = new JSONObject();
        entries.append("resultCode", "200");
        return entries;
    }

}

