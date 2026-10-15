package cn.poria.message.controller.inner;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.message.enums.im.IMMsgOpe;
import cn.poria.message.service.im.IMService;
import cn.poria.message.service.im.tx.bean.AccountInfo;
import cn.poria.message.vo.request.im.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
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

    /**
     * IM服务对象
     */
    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    /**
     * 发送IM单聊消息或群消息
     *
     * @param imMsgSend 会话类型、接收对象及消息内容
     * @return 发送结果
     */
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

    /**
     * 注册IM账号
     *
     * @param imRegister 待注册的IM账号信息
     * @return IM账号ID
     */
    @Inner
    @PostMapping("/register")
    @Operation(summary = "注册IM账号", description = "注册IM账号")
    public R registerIMAccount(@RequestBody IMRegister imRegister) {
        return R.ok(imService.createAccount(imRegister));
    }

    /**
     * 修改IM账号信息
     *
     * @param model IM账号及待更新资料
     * @return 修改结果
     */
    @Inner
    @PutMapping("/modify")
    @Operation(summary = "修改IM账号信息", description = "修改IM账号信息")
    public R modifyIMAccount(@RequestBody IMModifyAccountModel model) {
        imService.updateAccount(model);
        return R.ok();
    }

    /**
     * 查询IM单聊记录
     *
     * @param chatLogModel 单聊双方账号及查询条件
     * @return 消息数量、聊天记录及账号资料
     */
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

    /**
     * 发送IM服务器推送消息
     *
     * @param imPush 接收账号及推送内容
     * @return 推送结果
     */
    @Inner
    @PostMapping("/push")
    @Operation(summary = "发送IM服务器推送消息", description = "发送IM服务器推送消息")
    public R imPush(@RequestBody IMPush imPush) {
        imService.pushServerMsg(imPush);
        return R.ok();
    }
}
