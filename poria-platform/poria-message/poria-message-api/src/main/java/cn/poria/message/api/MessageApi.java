package cn.poria.message.api;

import cn.hutool.json.JSONObject;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.vo.request.*;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import cn.poria.message.vo.request.form.FormListModel;
import cn.poria.message.vo.request.form.FormPatientQueryModel;
import cn.poria.message.vo.request.im.*;
import cn.poria.message.vo.request.ma.MaAccountMessageModel;
import cn.poria.message.vo.request.outbound.OutBoundModel;
import cn.poria.message.vo.request.template.StationLetterModel;
import cn.poria.message.vo.request.yxxh.YxxhAXBModel;
import cn.poria.message.vo.request.yxxh.YxxhRealNameModel;
import cn.poria.message.vo.request.yxxh.YxxhUnbindModel;
import cn.poria.message.vo.response.FormListVO;
import cn.poria.message.vo.response.im.GroupInfoVo;
import cn.poria.message.vo.response.im.RoomInfoVo;
import cn.poria.message.vo.response.ma.MaAccountMessageVo;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface MessageApi {

    String SERVICE_ID = "poria-message-biz";

    @PostMapping("/inner/sms/send")
    R sendSms(@RequestBody @Validated SmsSend req, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/dd/sendText")
    R sendDingText(@RequestBody DingSend dingSend, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 发送微信模版消息
     */
    @PostMapping("/inner/wx/sendTemplateMsg")
    R sendWxTemplateMsg(@RequestBody @Validated WxMsgSend msgSend, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 新增待办任务
     */
    @PostMapping("/inner/pending/task")
    R savePendingTask(@RequestBody @Validated PendingTaskModel msgPendingTask, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 完成待办任务
     */
    @PutMapping("/inner/pending/task/{id}")
    R completePendingTask(@PathVariable("id") @Validated String id);

    /**
     * 发送站内信
     */
    @PostMapping("/inner/pending/task/stationLetter")
    R sendStationLetter(@RequestBody @Validated StationLetterModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/template/key/{key}")
    R<TemplateVo> getTemplateByKey(@PathVariable("key")String key, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * im账号注册
     */
    @PostMapping("/inner/im/register")
    R<String> registerIMAccount(@RequestBody IMRegister imRegister, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 更新im账号信息
     */
    @PutMapping("/inner/im/modify")
    R modifyIMAccount(@RequestBody IMModifyAccountModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 发送IM消息
     */
    @PostMapping("/inner/im/sendMsg")
    R sendIMMsg(@RequestBody IMMsgSend imMsgSend, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取聊天记录
     */
    @PostMapping("/inner/im/getChatLog")
    R<JSONObject> getChatLog(@RequestBody ChatLogModel chatLogModel, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 推送
     */
    @PostMapping("/inner/im/push")
    R imPush(@RequestBody IMPush imPush, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 建群
     */
    @PostMapping("/inner/im/group/create")
    R<String> createIMGroup(@RequestBody CreateGroupModel createGroupModel, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 添加群成员
     */
    @PostMapping("/inner/im/group/member/add")
    R addGroupMember(@RequestBody AddGroupPersonModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/im/group/memberExt/add")
    R addGroupMemberExt(@RequestBody AddMemberExtModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 移除群成员
     */
    @DeleteMapping("/inner/im/group/remove/member")
    R removeGroupMember(@RequestBody DelGroupMemberModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 解散群
     */
    @DeleteMapping("/inner/im/group/destroy")
    R destroyGroup(@RequestBody DestroyGroupModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取群聊天记录
     */
    @PostMapping("/inner/im/group/getChatLog")
    R<JSONObject> getGroupChatLog(@RequestBody GroupChatLogModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 修改群员昵称
     */
    @PutMapping("/inner/im/group/member/nickName")
    R updateMemberNickName(@RequestBody GroupMemberNickModel groupNickModel, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 修改群名称
     */
    @PutMapping("/inner/im/group/name")
    R updateGroupName(@RequestBody GroupNameModifyModel model,  @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查看群详情
     */
    @GetMapping("/inner/im/group/detail")
    R<GroupInfoVo> query(@RequestParam("groupId") String groupId, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取表单sn
     */
    @PostMapping("/inner/questionnaire/sn")
    R<Long> getUserFormSN(@RequestBody UserFormSNModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/questionnaire/list")
    R<List<FormListVO>> getFromList(@RequestBody FormListModel formListModel, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/questionnaire/form/data/{sn}")
    R<Map<String, Object>> queryFormDataBySn(@PathVariable("sn") String sn, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询患者id
     */
    @PostMapping("/inner/questionnaire/patientId")
    R<String> findUserPatientIdFromForm(@RequestBody FormPatientQueryModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/ma/sendMaSubScribeMsg")
    R sendMaSubScribeMsg(@RequestBody @Validated WxMsgSend msgSend,@RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询ma账号信息
     */
    @PostMapping("/inner/ma/account/info")
    R<MaAccountMessageVo> findMaAccountInfo(@RequestBody MaAccountMessageModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/im/nertc/token/{channelName}/{uid}")
    R<String> nertcToken(@PathVariable("channelName") String channelName,@PathVariable("uid")String uid);

    /**
     * 发送微信小程序、短信等通知
     */
    @PostMapping("/inner/inform")
    R informMessage(@RequestBody InformMesModel model,@RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/questionnaire/item/list/{formKey}")
    R queryFormItems(@PathVariable("formKey") String formKey, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/out/bound")
    R bound(@RequestBody OutBoundModel request, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/yxxh/yxxhAXB")
    R<String> yxxhAXBMsg(@RequestBody @Validated YxxhAXBModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @PutMapping("/inner/yxxh/yxxhUnbind")
    R yxxhUnbindMsg(@RequestBody @Validated YxxhUnbindModel yxxhUnbindModel, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/sms/convert/shortUrl")
    R<String> convertToShortUrl(@RequestBody @NotBlank(message = "链接不能为空") String longUrl, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/im/room/create")
    R createRoom(@RequestBody @Validated CreateRoomModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/im/room/destroy")
    R destroyRoom(@RequestBody @Validated DestroyRoomModel roomId, @RequestHeader(SecurityConstants.FROM) String from);
    @GetMapping("/inner/im/room/roomInfo")
    R<RoomInfoVo> getRoom(@RequestParam String roomId, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/im/video/createCloudRecord")
    R createCloudRecording(@RequestBody ImCreateCloudRecordModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/missionLog/message/log")
    R saveMissionLog(@RequestBody MissionLogModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 隐私通话实名认证
     * @param yxxhRealNameModel
     * @param from
     * @return
     */
    @PostMapping("/inner/yxxh/realNameAuth")
    R realNameAuth(@RequestBody YxxhRealNameModel yxxhRealNameModel, @RequestHeader(SecurityConstants.FROM) String from);

    @DeleteMapping("/inner/im/video/file/{id}")
    R delRoomRecFile(@PathVariable("id") String id, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/content/security")
    R audit(@RequestBody @Validated ContentSecurityModel model, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/im/group/transfer")
    R transferGroup(@RequestBody @Validated TransferGroupModel model, @RequestHeader(SecurityConstants.FROM) String from);
}
