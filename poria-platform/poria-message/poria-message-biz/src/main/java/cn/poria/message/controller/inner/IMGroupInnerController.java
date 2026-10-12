package cn.poria.message.controller.inner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.im.IMService;
import cn.poria.message.service.im.tx.bean.AccountInfo;
import cn.poria.message.service.im.tx.response.GroupHistoryResponse;
import cn.poria.message.vo.request.im.*;
import cn.poria.message.vo.response.im.GroupInfoVo;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Hidden
@RestController
@RequestMapping("/inner/im/group")
public class IMGroupInnerController {

    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    /**
     * 创建群
     */
    @Inner
    @PostMapping("/create")
    public R<String> createIMGroup(@RequestBody CreateGroupModel model){
        return R.ok(imService.createGroup(model));
    }

    /**
     * 添加群成员
     */
    @Inner
    @PostMapping("/member/add")
    public R addGroupMember(@RequestBody AddGroupPersonModel model){
        imService.addGroupMember(model);
        return R.ok();
    }

    /**
     * 添加带扩展信息群成员
     * @param model
     * @return
     */
    @Inner
    @PostMapping("/memberExt/add")
    public R addGroupMemberExt(@RequestBody AddMemberExtModel model){
        imService.addGroupMemberExt(model);
        return R.ok();
    }

    /**
     * 踢人
     */
    @Inner
    @DeleteMapping("/remove/member")
    public R removeGroupMember(@RequestBody DelGroupMemberModel model){
        imService.removeGroupMember(model);
        return R.ok();
    }

    /**
     * 解散群
     */
    @Inner
    @DeleteMapping("/destroy")
    public R destroyGroup(@RequestBody DestroyGroupModel model){
        imService.destroyGroup(model);
        return R.ok();
    }

    /**
     * 查询多个群信息
     */
    @Inner
    @GetMapping("/query")
    public R query(@RequestParam("groupIds") List<String> groupIds){
        return R.ok(imService.listGroupInfo(groupIds));
    }

    /**
     * 查询群详情
     */
    @Inner
    @GetMapping("/detail")
    public R<GroupInfoVo> query(@RequestParam("groupId") String groupId){
        return R.ok(imService.info(groupId));
    }

    /***
     * 获取群聊天记录
     */
    @Inner
    @PostMapping("/getChatLog")
    public R<JSONObject> getGroupChatLog(@RequestBody GroupChatLogModel model){
        JSONObject result = JSONUtil.createObj();
        result.set("total",0);
        result.set("history",null);
        result.set("members",null);

        JSONArray history = imService.findGroupHistory(model);
        if(!history.isEmpty()){
            List<GroupHistoryResponse> groupHistoryResponses = history.toList(GroupHistoryResponse.class);
            if(model.getEndTime() != null && model.getBeginTime() != null){
                groupHistoryResponses = groupHistoryResponses.stream().filter(e -> e.getMsgTimeStamp() >= model.getBeginTime() && e.getMsgTimeStamp() <= model.getEndTime())
                        .sorted(Comparator.comparing(GroupHistoryResponse::getMsgTimeStamp,Comparator.reverseOrder())).collect(Collectors.toList());
            } else {
                groupHistoryResponses = groupHistoryResponses.stream().sorted(Comparator.comparing(GroupHistoryResponse::getMsgTimeStamp,Comparator.reverseOrder())).collect(Collectors.toList());
            }
            result.set("total",groupHistoryResponses.size());
            result.set("history",JSONUtil.parseArray(groupHistoryResponses));
        }

        List<GroupMemberDetail> members = imService.getMemberDetail(model.getGroupId());
        if(CollUtil.isNotEmpty(members)){
            Map<String,String> memberMap = new HashMap<>();
            for (GroupMemberDetail member : members) {
                memberMap.put(member.getAccountId(), StrUtil.isBlank(member.getNickName())? "" : member.getNickName());
            }
            result.set("members",memberMap);

            Map<String, AccountInfo> accountInfo = imService.findAccountInfo(members.stream().map(GroupMemberDetail::getAccountId).collect(Collectors.toList()));
            result.set("accountInfos",accountInfo);
        }

        return R.ok(result);
    }

    /**
     * 更新群员昵称
     */
    @Inner
    @PutMapping("/member/nickName")
    public R updateMemberNickName(@RequestBody GroupMemberNickModel model){
        log.info("修改群成员资料：{}", JSONUtil.toJsonStr(model));
        imService.updateGroupMemberInfo(model);
        return R.ok();
    }


    /**
     * 更新群名称
     */
    @Inner
    @PutMapping("/name")
    public R updateGroupName(@RequestBody GroupNameModifyModel model){
        imService.updateGroupInfo(model);
        return R.ok();
    }

    /**
     * 转让群主
     */
    @Inner
    @PostMapping("/transfer")
    public R transferGroup(@RequestBody TransferGroupModel model){
        log.info("群主转让：{}", JSONUtil.toJsonStr(model));
        imService.transferGroup(model);
        return R.ok();
    }
}
