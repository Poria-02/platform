package cn.poria.message.service.im.tx.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.enums.txim.TxMsgUrlConstant;
import cn.poria.message.service.im.pojo.GroupInfo;
import cn.poria.message.service.im.tx.bean.NewMember;
import cn.poria.message.service.im.tx.config.TxIMProperties;
import cn.poria.message.service.im.tx.request.*;
import cn.poria.message.service.im.tx.response.GroupInfoResponse;
import cn.poria.message.service.im.tx.response.GroupMemberDetailResponse;
import cn.poria.message.service.im.tx.response.GroupOwnResponse;
import cn.poria.message.service.im.tx.util.CustomerDataUtil;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import cn.poria.message.service.im.tx.util.TxIMMsgWrapUtil;
import cn.poria.message.vo.request.im.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class TxIMGroupService {

    @Autowired
    private TxIMProperties txIMProperties;

    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;


    public String createGroup(CreateGroupModel model) {

        try {
            if(StrUtil.isNotBlank(model.getName()) && model.getName().getBytes(StandardCharsets.UTF_8).length > 30){
                throw new ServiceException("群名称超过30字节");
            }

            if(StrUtil.isNotBlank(model.getIntroduction()) && model.getIntroduction().getBytes(StandardCharsets.UTF_8).length > 240){
                throw new ServiceException("群简介超过240字节");
            }

            if(StrUtil.isNotBlank(model.getNotification()) && model.getNotification().getBytes(StandardCharsets.UTF_8).length > 300){
                throw new ServiceException("群公告超过300字节");
            }

        }catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

        GroupMsgCreateRequest request = new GroupMsgCreateRequest();
        request.setGroupId(model.getGroupId());
        request.setName(model.getName());
        request.setIntroduction(model.getIntroduction());
        request.setNotification(model.getNotification());
        request.setOwnerAccountId(model.getOwner());
        if (CollectionUtil.isNotEmpty(model.getMembers())){
            request.setMemberList(model.getMembers().stream().map(e -> {
                NewMember member = new NewMember();
                member.setUserId(e);
                member.setRole("Admin");
                return member;
            }).collect(Collectors.toList()));
        }
        request.setAppDefinedData(CustomerDataUtil.convertToTxCustomerData(model.getCustomer()));
        request.setMaxMemberCount(200);
        request.setApplyJoinOption("FreeAccess");
        request.setType("Public");
        JSONObject result =  txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_CREATE, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
        return result.getStr("GroupId");
    }



    public GroupInfo info(String groupId){
        GroupDetailRequest groupDetailRequest = new GroupDetailRequest();
        groupDetailRequest.setGroupIdList(Arrays.asList(groupId));
        JSONObject result =  txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_INFO, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupDetailRequest));
        List<GroupInfoResponse> groupInfoes = result.getJSONArray("GroupInfo").toList(GroupInfoResponse.class);
        if(CollUtil.isEmpty(groupInfoes)){
            return null;
        }
        GroupInfoResponse respone = groupInfoes.get(0);
        GroupInfo vo = new GroupInfo();
        vo.setFaceUrl(respone.getFaceUrl());
        vo.setMaxMemberNum(respone.getMaxMemberNum());
        vo.setIntroduction(respone.getIntroduction());
        vo.setName(respone.getName());
        vo.setJoinOption(respone.getApplyJoinOption());
        vo.setGroupId(respone.getGroupId());
        vo.setNotification(respone.getNotification());
        vo.setCreateTime(respone.getCreateTime());
        vo.setCustomer(CustomerDataUtil.convertToCommonCustomerData(respone.getAppDefinedData()));
        vo.fillMemberInfo(respone.getMemberList());
        return vo;
    }



    public List<GroupInfo> listGroupInfo(List<String> groupIds){
        List<GroupInfo> vos = new ArrayList<>();
        for (String groupId : groupIds) {
            GroupInfo info = this.info(groupId);
            vos.add(info);
        }
        return vos;
    }



    public List<GroupOwnResponse> ownGroup(GroupOwnRequest groupOwnRequest){
        JSONObject responseFilter = JSONUtil.createObj();
        responseFilter.set("GroupBaseInfoFilter",new String[]{"Type","Name","Introduction","Notification","Owner_Account"});
        responseFilter.set("SelfInfoFilter",new String[]{"Role","JoinTime"});
        groupOwnRequest.setResponseFilter(responseFilter.toBean(Object.class));
        JSONObject result =  txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_OWN, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupOwnRequest));
        List<GroupOwnResponse> ownGroupInfoes = result.getJSONArray("GroupIdList").toList(GroupOwnResponse.class);
        return ownGroupInfoes;
    }


    public void updateGroupInfo(GroupNameModifyModel model){
        GroupUpdateRequest request = new GroupUpdateRequest();
        request.setGroupId(model.getGroupId());
        request.setName(model.getName());
        request.setIntroduction(model.getIntroduction());
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_UPDATE, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
    }


    public void updateMemberInfo(GroupMemberInfoUpdateRequest groupMemberInfoUpdateRequest){
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_MEMBER_UPDATE, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupMemberInfoUpdateRequest));
    }


    public void sendGroupMsg(IMMsgSend imMsgSend){
        GroupMsgRequest request = new GroupMsgRequest();
        request.setGroupId(imMsgSend.getTo());
        request.setFrom(imMsgSend.getFrom());
        request.setMsgBody(Arrays.asList(TxIMMsgWrapUtil.wrapBody(imMsgSend.getBody())));
        if(CollUtil.isNotEmpty(imMsgSend.getReceiveMsgAccountIds())){
            request.setTo(imMsgSend.getReceiveMsgAccountIds());
        }
        request.setRandom(ThreadLocalRandom.current().nextLong(1L << 32));
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_SEND_MSG, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
    }


    public JSONArray findGroupHistory(GroupChatLogModel model){
        GroupMsgRecordRequest request = new GroupMsgRecordRequest();
        request.setGroupId(model.getGroupId());
        request.setReqMsgNumber(model.getLimit());
        request.setReqMsgSeq(model.getMsgSeq());
        JSONObject response = txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_MSG_RECORD, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
        JSONArray msgList = response.getJSONArray("RspMsgList");
        return msgList == null ? JSONUtil.createArray() : msgList;
    }


    public void addGroupMember(AddGroupPersonModel model){
        GroupMemberAddRequest request = new GroupMemberAddRequest();
        request.setGroupId(model.getGroupId());
        request.setMemberList(model.getMembers().stream().map(e -> {
            NewMember member = new NewMember();
            member.setUserId(e);
            member.setRole("Member");
            return member;
        }).collect(Collectors.toList()));
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_MEMBER_ADD, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
    }

    public void addGroupMemberExt(AddMemberExtModel model){
        GroupMemberAddRequest request = new GroupMemberAddRequest();
        request.setGroupId(model.getGroupId());
        request.setMemberList(model.getMembers().stream().map(e -> {
            NewMember member = new NewMember();
            member.setUserId(e.getAccountId());
            member.setRole(e.getRole());
            if (CollectionUtil.isNotEmpty(e.getExt())){
                JSONArray jsonArray = new JSONArray();
                e.getExt().forEach((k,v)->{
                    JSONObject o = new JSONObject();
                    o.set("Key", k);
                    o.set("Value", v);
                    jsonArray.add(o);
                });
                member.setAppMemberDefinedData(jsonArray);
            }
            return member;
        }).collect(Collectors.toList()));
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_MEMBER_ADD, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
    }


    public void removeGroupMember(DelGroupMemberModel model){
        GroupMemberDelRequest request = new GroupMemberDelRequest();
        request.setGroupId(model.getGroupId());
        request.setMemberToDelAccount(Arrays.asList(model.getMember()));
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_MEMBER_DEL, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
    }

    public void destroyGroup(DestroyGroupModel model){
        GroupDestroyRequest groupMsgRecordRequest = new GroupDestroyRequest();
        groupMsgRecordRequest.setGroupId(model.getGroupId());
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_DESTROY, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupMsgRecordRequest));
    }

    public List<GroupMemberDetailResponse> getMemberDetail(String groupId){
        JSONObject request = JSONUtil.createObj();
        request.set("GroupId",groupId);
        JSONObject response = txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_MEMBER_INFO, txIMProperties.getAdminAccountName(), request.toString());
        List<GroupMemberDetailResponse> members = response.getJSONArray("MemberList").toList(GroupMemberDetailResponse.class);
        return members;
    }

    public void transferGroup(TransferGroupModel model) {
        GroupTransferRequest groupTransferRequest = new GroupTransferRequest();
        groupTransferRequest.setGroupId(model.getGroupId());
        groupTransferRequest.setNewOwnerAccountId(model.getNewOwnerAccountId());
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_TRANSFER, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupTransferRequest));
    }
}
