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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 服务间IM群组管理控制层
 */
@Slf4j
@Hidden
@RestController
@RequestMapping("/inner/im/group")
public class IMGroupInnerController {

    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    @Inner
    @PostMapping("/create")
    @Operation(summary = "创建IM群组", description = "创建IM群组")
    public R<String> createIMGroup(@RequestBody CreateGroupModel model) {
        return R.ok(imService.createGroup(model));
    }

    @Inner
    @PostMapping("/member/add")
    @Operation(summary = "添加群成员", description = "添加群成员")
    public R addGroupMember(@RequestBody AddGroupPersonModel model) {
        imService.addGroupMember(model);
        return R.ok();
    }

    @Inner
    @PostMapping("/memberExt/add")
    @Operation(summary = "添加带扩展信息的群成员", description = "添加带扩展信息的群成员")
    public R addGroupMemberExt(@RequestBody AddMemberExtModel model) {
        imService.addGroupMemberExt(model);
        return R.ok();
    }

    @Inner
    @DeleteMapping("/remove/member")
    @Operation(summary = "移除群成员", description = "移除群成员")
    public R removeGroupMember(@RequestBody DelGroupMemberModel model) {
        imService.removeGroupMember(model);
        return R.ok();
    }

    @Inner
    @DeleteMapping("/destroy")
    @Operation(summary = "解散群组", description = "解散群组")
    public R destroyGroup(@RequestBody DestroyGroupModel model) {
        imService.destroyGroup(model);
        return R.ok();
    }

    @Inner
    @GetMapping("/query")
    @Operation(summary = "批量查询群组信息", description = "批量查询群组信息")
    public R query(@RequestParam("groupIds") List<String> groupIds) {
        return R.ok(imService.listGroupInfo(groupIds));
    }

    @Inner
    @GetMapping("/detail")
    @Operation(summary = "查询群组详情", description = "查询群组详情")
    public R<GroupInfoVo> query(@RequestParam("groupId") String groupId) {
        return R.ok(imService.info(groupId));
    }

    @Inner
    @PostMapping("/getChatLog")
    @Operation(summary = "按游标查询群聊天记录", description = "按游标查询群聊天记录")
    public R<JSONObject> getGroupChatLog(@RequestBody @Validated GroupChatLogModel model) {
        JSONObject result = JSONUtil.createObj();
        result.set("total", 0);
        result.set("history", null);
        result.set("members", null);
        result.set("nextMsgSeq", null);

        JSONArray history = imService.findGroupHistory(model);
        if (!history.isEmpty()) {
            // 游标按原始消息计算，时间筛选后没有结果时仍可继续查询。
            Long minMsgSeq = history.stream().map(JSONUtil::parseObj).map(message -> message.getLong("MsgSeq"))
                    .filter(Objects::nonNull).min(Long::compareTo).orElse(null);
            if (minMsgSeq != null && minMsgSeq > 1 && (model.getMsgSeq() == null || minMsgSeq <= model.getMsgSeq())) {
                result.set("nextMsgSeq", minMsgSeq - 1);
            }
            List<GroupHistoryResponse> groupHistoryResponses = history.toList(GroupHistoryResponse.class);
            groupHistoryResponses = groupHistoryResponses.stream()
                    .filter(e -> model.getBeginTime() == null || (e.getMsgTimeStamp() != null && e.getMsgTimeStamp() >= model.getBeginTime()))
                    .filter(e -> model.getEndTime() == null || (e.getMsgTimeStamp() != null && e.getMsgTimeStamp() <= model.getEndTime()))
                    .sorted(Comparator.comparing(GroupHistoryResponse::getMsgTimeStamp, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            result.set("total", groupHistoryResponses.size());
            result.set("history", JSONUtil.parseArray(groupHistoryResponses));
        }

        List<GroupMemberDetail> members = imService.getMemberDetail(model.getGroupId());
        if (CollUtil.isNotEmpty(members)) {
            Map<String, String> memberMap = new HashMap<>();
            for (GroupMemberDetail member : members) {
                memberMap.put(member.getAccountId(), StrUtil.isBlank(member.getNickName()) ? "" : member.getNickName());
            }
            result.set("members", memberMap);

            Map<String, AccountInfo> accountInfo = imService.findAccountInfo(members.stream().map(GroupMemberDetail::getAccountId).collect(Collectors.toList()));
            result.set("accountInfos", accountInfo);
        }

        return R.ok(result);
    }

    @Inner
    @PutMapping("/member/nickName")
    @Operation(summary = "修改群成员昵称", description = "修改群成员昵称")
    public R updateMemberNickName(@RequestBody GroupMemberNickModel model) {
        log.info("修改群成员资料：{}", JSONUtil.toJsonStr(model));
        imService.updateGroupMemberInfo(model);
        return R.ok();
    }

    @Inner
    @PutMapping("/name")
    @Operation(summary = "修改群组名称", description = "修改群组名称")
    public R updateGroupName(@RequestBody GroupNameModifyModel model) {
        imService.updateGroupInfo(model);
        return R.ok();
    }

    @Inner
    @PostMapping("/transfer")
    @Operation(summary = "转让群主", description = "转让群主")
    public R transferGroup(@RequestBody TransferGroupModel model) {
        log.info("群主转让：{}", JSONUtil.toJsonStr(model));
        imService.transferGroup(model);
        return R.ok();
    }
}
