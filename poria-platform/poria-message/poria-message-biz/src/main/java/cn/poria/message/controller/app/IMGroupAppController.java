package cn.poria.message.controller.app;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.message.enums.txim.TxMsgUrlConstant;
import cn.poria.message.service.im.bean.GroupITeamsRespone;
import cn.poria.message.service.im.bean.ITeamInfo;
import cn.poria.message.service.im.tx.config.TxIMProperties;
import cn.poria.message.service.im.tx.request.GroupDetailRequest;
import cn.poria.message.service.im.tx.request.GroupOwnRequest;
import cn.poria.message.service.im.tx.response.GroupInfoResponse;
import cn.poria.message.service.im.tx.response.GroupOwnResponse;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import cn.poria.message.vo.request.im.GroupInfosModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchunlei
 * @date 2021年12月27日 2:04 下午
 */
@Slf4j
@RestController
@Tag(name = "【App】- 群聊")
@RequestMapping("/app/im/group")
public class IMGroupAppController {

    @Autowired
    private TxIMProperties txIMProperties;

    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;

    @Operation(summary = "获取我的群组列表",description = "获取我的群组列表")
    @GetMapping("/iteams")
    public R iteams(){
        GroupOwnRequest groupOwnRequest = new GroupOwnRequest();
        groupOwnRequest.setAccountId(SecurityUtils.getSId());
        JSONObject responseFilter = JSONUtil.createObj();
        responseFilter.set("GroupBaseInfoFilter",new String[]{"Type","Name","Introduction","Notification","Owner_Account"});
        responseFilter.set("SelfInfoFilter",new String[]{"Role","JoinTime"});
        groupOwnRequest.setResponseFilter(responseFilter.toBean(Object.class));
        JSONObject result =  txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_OWN, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupOwnRequest));
        List<GroupOwnResponse> groupOwnResponses = result.getJSONArray("GroupIdList").toList(GroupOwnResponse.class);

        GroupITeamsRespone vo = new GroupITeamsRespone();
        vo.setInfos(
                groupOwnResponses.stream().map(e -> {
                    ITeamInfo groupInfo = new ITeamInfo();
                    groupInfo.setTid(e.getGroupId());
                    groupInfo.setOwner(e.getOwnerAccountId());
                    groupInfo.setTname(e.getName());
                    return groupInfo;
                }).collect(Collectors.toList())
        );
        vo.setCount(groupOwnResponses.size());
        return R.ok(vo);
    }

    @Operation(summary = "批量查询群信息",description = "批量查询群信息")
    @PostMapping("/infos")
    public R<List<GroupInfoResponse>> query(@RequestBody GroupInfosModel model){
        GroupDetailRequest groupDetailRequest = new GroupDetailRequest();
        groupDetailRequest.setGroupIdList(model.getGroupIds());
        JSONObject result =  txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.GROUP_INFO, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(groupDetailRequest));
        List<GroupInfoResponse> groupInfoes = result.getJSONArray("GroupInfo").toList(GroupInfoResponse.class);
        return R.ok(groupInfoes);
    }
}
