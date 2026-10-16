package cn.poria.message.controller.inner;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.enums.im.IMMsgOpe;
import cn.poria.message.service.im.IMService;
import cn.poria.message.service.im.tx.bean.AccountInfo;
import cn.poria.message.vo.request.im.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 服务间IM账号与消息控制层
 */
@Slf4j
@Hidden
@RestController
@RequestMapping("/inner/im")
public class IMInnerController {

    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    @Inner
    @PostMapping("/sendMsg")
    @Operation(summary = "发送IM单聊消息或群消息", description = "发送IM单聊消息或群消息")
    public R sendIMMsg(@RequestBody IMMsgSend imMsgSend) {
        if (imMsgSend.getOpe().getType().equals(IMMsgOpe.PERSONAL.getType())) {
            imService.sendP2PMsg(imMsgSend);
        }

        if (imMsgSend.getOpe().getType().equals(IMMsgOpe.GROUP.getType())) {
            imService.sendGroupMsg(imMsgSend);
        }
        return R.ok();
    }

    @Inner
    @PostMapping("/register")
    @Operation(summary = "注册IM账号", description = "注册IM账号")
    public R registerIMAccount(@RequestBody IMRegister imRegister) {
        return R.ok(imService.createAccount(imRegister));
    }

    @Inner
    @PutMapping("/modify")
    @Operation(summary = "修改IM账号信息", description = "修改IM账号信息")
    public R modifyIMAccount(@RequestBody IMModifyAccountModel model) {
        imService.updateAccount(model);
        return R.ok();
    }

    @Inner
    @PostMapping("/getChatLog")
    @Operation(summary = "查询IM单聊记录", description = "查询IM单聊记录")
    public R<JSONObject> getChatLog(@RequestBody ChatLogModel chatLogModel) {
        JSONObject result = JSONUtil.createObj();
        result.set("total", 0);
        result.set("history", null);
        result.set("members", null);

        JSONArray history = imService.findP2PMsgHistory(chatLogModel);
        if (!history.isEmpty()) {
            result.set("total", history.size());
            result.set("history", history);
        }

        Map<String, AccountInfo> accountInfo = imService.findAccountInfo(Arrays.asList(chatLogModel.getFrom(), chatLogModel.getTo()));
        result.set("accountInfos", accountInfo);
        return R.ok(result);
    }

    @Inner
    @PostMapping("/push")
    @Operation(summary = "发送IM服务器推送消息", description = "发送IM服务器推送消息")
    public R imPush(@RequestBody IMPush imPush) {
        imService.pushServerMsg(imPush);
        return R.ok();
    }
}
