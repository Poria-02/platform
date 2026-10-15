package cn.poria.message.service.im.tx;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.message.service.im.bean.GroupOwn;
import cn.poria.message.service.im.pojo.GroupInfo;
import cn.poria.message.service.im.tx.bean.AccountInfo;
import cn.poria.message.service.im.tx.response.GroupMemberDetailResponse;
import cn.poria.message.service.im.tx.response.CallBackResponse;
import cn.poria.message.service.im.tx.response.GroupOwnResponse;
import cn.poria.message.service.im.IMService;
import cn.poria.message.service.im.tx.request.GroupMemberInfoUpdateRequest;
import cn.poria.message.service.im.tx.request.GroupOwnRequest;
import cn.poria.message.service.im.tx.request.GroupSystemMsgRequest;
import cn.poria.message.service.im.tx.service.*;
import cn.poria.message.vo.request.im.*;
import cn.poria.message.vo.response.im.GroupInfoVo;
import cn.poria.message.vo.response.im.GroupMemberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TxIMService implements IMService {

    @Autowired
    private TxIMAccountService txIMAccountService;

    @Autowired
    private TxIMP2PService txIMP2PService;

    @Autowired
    private TxIMGroupService txIMGroupService;

    @Autowired
    private TXMsgCallBackService txMsgCallBackService;

    @Override
    public String createAccount(IMRegister imRegister){
        return txIMAccountService.createAccount(imRegister);
    }

    @Override
    public Map<String, AccountInfo> findAccountInfo(List<String> accountIds) {
        return txIMAccountService.findAccountInfo(accountIds);
    }

    @Override
    public void updateAccount(IMModifyAccountModel model){
        txIMAccountService.updateAccount(model);
    }

    @Override
    public void deleteAccount(List<String> accountIds){
        txIMAccountService.deleteAccount(accountIds);
    }

    public String getUserSig(){
        return txIMP2PService.getUserSig();
    }

    @Override
    public void sendP2PMsg(IMMsgSend imMsgSend) {
        txIMP2PService.sendP2PMsg(imMsgSend);
    }

    @Override
    public JSONArray findP2PMsgHistory(ChatLogModel model){
        return txIMP2PService.findP2PMsgHistory(model);
    }

    @Override
    public void pushServerMsg(@RequestBody IMPush imPush) {
        txIMP2PService.pushServerMsg(imPush);
    }

    @Override
    public String createGroup(CreateGroupModel model){
        return txIMGroupService.createGroup(model);
    }

    @Override
    public GroupInfoVo info(String groupId){
        GroupInfoVo vo = new GroupInfoVo();
        GroupInfo info = txIMGroupService.info(groupId);
        BeanUtils.copyProperties(info, vo);
        List<GroupMemberVo> memberVoList = new ArrayList<>(new ArrayList<>(CollectionUtil.isNotEmpty(info.getMembers()) ? info.getMembers().stream().map(e -> {
            GroupMemberVo memberVo = new GroupMemberVo();
            BeanUtils.copyProperties(e, memberVo);
            return memberVo;
        }).toList() : new ArrayList<>()));

        List<GroupMemberVo> adminList = CollectionUtil.isNotEmpty(info.getAdmins()) ? info.getAdmins().stream().map(e -> {
            GroupMemberVo memberVo = new GroupMemberVo();
            BeanUtils.copyProperties(e, memberVo);
            return memberVo;
        }).toList() : new ArrayList<>();
        memberVoList.addAll(adminList);
        vo.setMembers(memberVoList);

        return vo;
    }

    @Override
    public List<GroupInfo> listGroupInfo(List<String> groupIds){
        return txIMGroupService.listGroupInfo(groupIds);
    }

    public List<GroupOwn> ownGroup(GroupOwnRequest groupOwnRequest){
        List<GroupOwnResponse> groupOwnResponses = txIMGroupService.ownGroup(groupOwnRequest);
        return groupOwnResponses.stream().map(e -> {
            GroupOwn vo = new GroupOwn();
            BeanUtils.copyProperties(e,vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateGroupInfo(GroupNameModifyModel model){
        txIMGroupService.updateGroupInfo(model);
    }

    @Override
    public void updateGroupMemberInfo(GroupMemberNickModel model){
        GroupMemberInfoUpdateRequest request = new GroupMemberInfoUpdateRequest();
        request.setGroupId(model.getGroupId());
        request.setMemberAccount(model.getMember());
        request.setNameCard(model.getNickName());
        if (CollectionUtil.isNotEmpty(model.getExt())){
            JSONArray jsonArray = new JSONArray();
            model.getExt().forEach((k,v)->{
                JSONObject o = new JSONObject();
                o.set("Key", k);
                o.set("Value", v);
                jsonArray.add(o);
            });
            request.setAppMemberDefinedData(jsonArray);
        }
        txIMGroupService.updateMemberInfo(request);
    }

    @Override
    public List<GroupMemberDetail> getMemberDetail(String groupId) {
        List<GroupMemberDetailResponse> memberDetails = txIMGroupService.getMemberDetail(groupId);
        return memberDetails.stream().map( e -> {
            GroupMemberDetail vo = new GroupMemberDetail();
            BeanUtils.copyProperties(e,vo);
            vo.setNickName(e.getNameCard());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void sendGroupMsg(IMMsgSend imMsgSend){
        txIMGroupService.sendGroupMsg(imMsgSend);
    }

    public void sendSystemMsg(GroupSystemMsgRequest groupMsgRequest){
        txIMP2PService.sendSystemMsg(groupMsgRequest);
    }

    @Override
    public JSONArray findGroupHistory(GroupChatLogModel model){
        return txIMGroupService.findGroupHistory(model);
    }

    @Override
    public void addGroupMember(AddGroupPersonModel model){
        txIMGroupService.addGroupMember(model);
    }

    @Override
    public void addGroupMemberExt(AddMemberExtModel model) {
        txIMGroupService.addGroupMemberExt(model);
    }

    @Override
    public void removeGroupMember(DelGroupMemberModel model){
        txIMGroupService.removeGroupMember(model);
    }

    @Override
    public void destroyGroup(DestroyGroupModel model){
        txIMGroupService.destroyGroup(model);
    }

    @Override
    public CallBackResponse callBackMsgResolve(String appId, String command, String requestTime, String sign, JSONObject body) {
        return txMsgCallBackService.callBackMsgResolve(appId, command, requestTime, sign, body);
    }

    @Override
    public void transferGroup(TransferGroupModel model) { txIMGroupService.transferGroup(model); }
}
