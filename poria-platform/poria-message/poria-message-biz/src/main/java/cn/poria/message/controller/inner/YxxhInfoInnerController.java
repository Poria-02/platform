package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.entity.MsgYxxhInfo;
import cn.poria.message.service.MsgYxxhInfoService;
import cn.poria.message.vo.request.WxMsgSend;
import cn.poria.message.vo.request.yxxh.YxxhAXBModel;
import cn.poria.message.vo.request.yxxh.YxxhRealNameModel;
import cn.poria.message.vo.request.yxxh.YxxhUnbindModel;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/inner/yxxh")
public class YxxhInfoInnerController {

    @Autowired
    private MsgYxxhInfoService msgYxxhInfoService;

    /**
     * 云信小号AXB绑定
     *
     * @param model
     */
    @Inner
    @PostMapping("/yxxhAXB")
    public R<String> yxxhAXBMsg(@RequestBody @Validated YxxhAXBModel model) {

        return R.ok(msgYxxhInfoService.yxxhAXB(model));
    }

    /**
     * @param: 云信小号AXB解绑
     * @author: wuyifan
     * @date:2024/3/26 14:09
     */

    @Inner
    @PutMapping("/yxxhUnbind")
    @Operation(summary = "云信小号AXB解绑", description = "云信小号AXB解绑")
    public R yxxhUnbindMsg(@RequestBody @Validated YxxhUnbindModel yxxhUnbindModel) {
        this.msgYxxhInfoService.yxxhUnbind(yxxhUnbindModel);
        return R.ok();
    }

    @Inner
    @PostMapping("/realNameAuth")
    public R<String> realNameAuth(@RequestBody YxxhRealNameModel yxxhRealNameModel) {
        return R.ok(this.msgYxxhInfoService.realName(yxxhRealNameModel));
    }

}
