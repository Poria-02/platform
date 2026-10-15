package cn.poria.message.api;

import cn.hutool.json.JSONObject;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.vo.request.*;
import cn.poria.message.vo.request.form.FormListModel;
import cn.poria.message.vo.request.form.FormPatientQueryModel;
import cn.poria.message.vo.request.im.*;
import cn.poria.message.vo.request.ma.MaAccountMessageModel;
import cn.poria.message.vo.request.center.StationLetterModel;
import cn.poria.message.vo.response.FormListVO;
import cn.poria.message.vo.response.im.GroupInfoVo;
import cn.poria.message.vo.response.ma.MaAccountMessageVo;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息服务内部调用契约
 */
public interface MessageApi {

    /**
     * 消息服务标识
     */
    String SERVICE_ID = "poria-message-biz";

    /**
     * 发送通用短信
     *
     * @param req 接收手机号、模板及短信参数
     * @param from 内部调用来源标识
     * @return 发送结果
     */
    @PostMapping("/inner/sms/send")
    R sendSms(@RequestBody @Validated SmsSend req, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 发送微信公众号模板消息
     *
     * @param msgSend 接收人、模板及消息参数
     * @param from 内部调用来源标识
     * @return 发送结果
     */
    @PostMapping("/inner/wx/sendTemplateMsg")
    R sendWxTemplateMsg(@RequestBody @Validated WxMsgSend msgSend, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 新增待办任务
     *
     * @param msgPendingTask 待办任务内容
     * @param from 内部调用来源标识
     * @return 新增结果
     */
    @PostMapping("/inner/pending/task")
    R savePendingTask(@RequestBody @Validated PendingTaskModel msgPendingTask, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 完成待办任务
     *
     * @param id 待办任务ID
     * @return 完成结果
     */
    @PutMapping("/inner/pending/task/{id}")
    R completePendingTask(@PathVariable("id") @Validated String id);

    /**
     * 使用通用模板发送站内信
     *
     * @param model 接收人、模板Key及模板参数
     * @param from 内部调用来源标识
     * @return 待办任务ID列表
     */
    @PostMapping("/inner/station/letter/sendStationLetter")
    R<List<String>> sendStationLetter(@RequestBody @Validated StationLetterModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 通过模板Key查询通用消息模板
     *
     * @param key 模板Key
     * @param from 内部调用来源标识
     * @return 消息模板详情
     */
    @GetMapping("/inner/template/key/{key}")
    R<TemplateVo> getTemplateByKey(@PathVariable("key") String key, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 注册IM账号
     *
     * @param imRegister 待注册的IM账号信息
     * @param from 内部调用来源标识
     * @return IM账号ID
     */
    @PostMapping("/inner/im/register")
    R<String> registerIMAccount(@RequestBody IMRegister imRegister, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 修改IM账号信息
     *
     * @param model IM账号及待更新资料
     * @param from 内部调用来源标识
     * @return 修改结果
     */
    @PutMapping("/inner/im/modify")
    R modifyIMAccount(@RequestBody IMModifyAccountModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 发送IM单聊消息或群消息
     *
     * @param imMsgSend 会话类型、接收对象及消息内容
     * @param from 内部调用来源标识
     * @return 发送结果
     */
    @PostMapping("/inner/im/sendMsg")
    R sendIMMsg(@RequestBody IMMsgSend imMsgSend, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询IM单聊记录
     *
     * @param chatLogModel 单聊双方账号及查询条件
     * @param from 内部调用来源标识
     * @return 消息数量、聊天记录及账号资料
     */
    @PostMapping("/inner/im/getChatLog")
    R<JSONObject> getChatLog(@RequestBody ChatLogModel chatLogModel, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 发送IM服务器推送消息
     *
     * @param imPush 接收账号及推送内容
     * @param from 内部调用来源标识
     * @return 推送结果
     */
    @PostMapping("/inner/im/push")
    R imPush(@RequestBody IMPush imPush, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 创建IM群组
     *
     * @param createGroupModel 群组名称、群主及成员信息
     * @param from 内部调用来源标识
     * @return 群组ID
     */
    @PostMapping("/inner/im/group/create")
    R<String> createIMGroup(@RequestBody CreateGroupModel createGroupModel, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 添加群成员
     *
     * @param model 群组及待添加成员信息
     * @param from 内部调用来源标识
     * @return 添加结果
     */
    @PostMapping("/inner/im/group/member/add")
    R addGroupMember(@RequestBody AddGroupPersonModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 添加带扩展信息的群成员
     *
     * @param model 群组及带扩展信息的成员列表
     * @param from 内部调用来源标识
     * @return 添加结果
     */
    @PostMapping("/inner/im/group/memberExt/add")
    R addGroupMemberExt(@RequestBody AddMemberExtModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 移除群成员
     *
     * @param model 群组及待移除成员信息
     * @param from 内部调用来源标识
     * @return 移除结果
     */
    @DeleteMapping("/inner/im/group/remove/member")
    R removeGroupMember(@RequestBody DelGroupMemberModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 解散群组
     *
     * @param model 待解散的群组信息
     * @param from 内部调用来源标识
     * @return 解散结果
     */
    @DeleteMapping("/inner/im/group/destroy")
    R destroyGroup(@RequestBody DestroyGroupModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 按游标查询群聊天记录
     *
     * @param model 群组、消息序号、查询条数及时间筛选条件
     * @param from 内部调用来源标识
     * @return 本批消息数量、聊天记录、成员信息及下一页游标
     */
    @PostMapping("/inner/im/group/getChatLog")
    R<JSONObject> getGroupChatLog(@RequestBody GroupChatLogModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 修改群成员昵称
     *
     * @param groupNickModel 群组、成员账号及新昵称
     * @param from 内部调用来源标识
     * @return 修改结果
     */
    @PutMapping("/inner/im/group/member/nickName")
    R updateMemberNickName(@RequestBody GroupMemberNickModel groupNickModel, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 修改群组名称
     *
     * @param model 群组ID及新名称
     * @param from 内部调用来源标识
     * @return 修改结果
     */
    @PutMapping("/inner/im/group/name")
    R updateGroupName(@RequestBody GroupNameModifyModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询群组详情
     *
     * @param groupId 群组ID
     * @param from 内部调用来源标识
     * @return 群组详情
     */
    @GetMapping("/inner/im/group/detail")
    R<GroupInfoVo> query(@RequestParam("groupId") String groupId, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取用户问卷流水号
     *
     * @param model 用户及问卷信息
     * @param from 内部调用来源标识
     * @return 问卷流水号
     */
    @PostMapping("/inner/questionnaire/sn")
    R<Long> getUserFormSN(@RequestBody UserFormSNModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询问卷列表
     *
     * @param formListModel 问卷筛选条件
     * @param from 内部调用来源标识
     * @return 问卷列表
     */
    @PostMapping("/inner/questionnaire/list")
    R<List<FormListVO>> getFromList(@RequestBody FormListModel formListModel, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 通过流水号查询问卷填写信息
     *
     * @param sn 问卷流水号
     * @param from 内部调用来源标识
     * @return 问卷填写信息
     */
    @GetMapping("/inner/questionnaire/form/data/{sn}")
    R<Map<String, Object>> queryFormDataBySn(@PathVariable("sn") String sn, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询问卷关联的患者ID
     *
     * @param model 患者ID查询条件
     * @param from 内部调用来源标识
     * @return 患者ID
     */
    @PostMapping("/inner/questionnaire/patientId")
    R<String> findUserPatientIdFromForm(@RequestBody FormPatientQueryModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 发送微信小程序订阅消息
     *
     * @param msgSend 接收人、模板及消息参数
     * @param from 内部调用来源标识
     * @return 发送结果
     */
    @PostMapping("/inner/ma/sendMaSubScribeMsg")
    R sendMaSubScribeMsg(@RequestBody @Validated WxMsgSend msgSend, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 根据筛选条件查询小程序账号
     *
     * @param model 组织ID及小程序AppID等筛选条件
     * @param from 内部调用来源标识
     * @return 小程序账号信息
     */
    @PostMapping("/inner/ma/account/info")
    R<MaAccountMessageVo> findMaAccountInfo(@RequestBody MaAccountMessageModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 通过表单Key查询表单项列表
     *
     * @param formKey 表单Key
     * @param from 内部调用来源标识
     * @return 表单项列表
     */
    @GetMapping("/inner/questionnaire/item/list/{formKey}")
    R queryFormItems(@PathVariable("formKey") String formKey, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 将长链接转换为短链接
     *
     * @param longUrl 待转换的长链接
     * @param from 内部调用来源标识
     * @return 转换后的短链接
     */
    @PostMapping("/inner/sms/convert/shortUrl")
    R<String> convertToShortUrl(@RequestBody @NotBlank(message = "链接不能为空") String longUrl, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 转让群主
     *
     * @param model 群组及新群主信息
     * @param from 内部调用来源标识
     * @return 转让结果
     */
    @PostMapping("/inner/im/group/transfer")
    R transferGroup(@RequestBody @Validated TransferGroupModel model, @RequestHeader(SecurityConstants.FROM) String from);
}
