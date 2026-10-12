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

@Slf4j
@Hidden
@RestController
@RequestMapping("/inner/im")
public class IMInnerController {

    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    /**
     * 发送群消息或p2p消息
     */
    @Inner
    @PostMapping("/sendMsg")
    public R sendIMMsg(@RequestBody IMMsgSend imMsgSend){
        if(imMsgSend.getOpe().getType().equals(IMMsgOpe.PERSONAL.getType())){
            imService.sendP2PMsg(imMsgSend);
        }

        if(imMsgSend.getOpe().getType().equals(IMMsgOpe.GROUP.getType())){
            imService.sendGroupMsg(imMsgSend);
        }
        return R.ok();
    }

    /**
     * 注册账户
     */
    @Inner
    @PostMapping("/register")
    public R registerIMAccount(@RequestBody IMRegister imRegister){
        return R.ok(imService.createAccount(imRegister));
    }


    /**
     * 更新账户信息
     */
    @Inner
    @PutMapping("/modify")
    public R modifyIMAccount(@RequestBody IMModifyAccountModel model){
        imService.updateAccount(model);
        return R.ok();
    }


    /**
     * 查询p2p聊天记录
     */
    @Inner
    @PostMapping("/getChatLog")
    public R<JSONObject> getChatLog(@RequestBody ChatLogModel chatLogModel){
        JSONObject result = JSONUtil.createObj();
        result.set("total",0);
        result.set("history",null);
        result.set("members",null);


        JSONArray history = imService.findP2PMsgHistory(chatLogModel);
        if(!history.isEmpty()){
            result.set("total",history.size());
            result.set("history",history);
        }

        Map<String, AccountInfo> accountInfo = imService.findAccountInfo(Arrays.asList(chatLogModel.getFrom(),chatLogModel.getTo()));
        result.set("accountInfos",accountInfo);
        return R.ok(result);
    }


    /**
     * 发送服务器推送
     */
    @Inner
    @PostMapping("/push")
    public R imPush(@RequestBody IMPush imPush){
        imService.pushServerMsg(imPush);
        return R.ok();
    }

}
