package cn.poria.message.service.im.tx.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.enums.txim.TxMsgUrlConstant;
import cn.poria.message.service.im.tx.bean.AccountInfo;
import cn.poria.message.service.im.tx.config.TxIMProperties;
import cn.poria.message.service.im.tx.request.AccountCreateRequest;
import cn.poria.message.service.im.tx.request.AccountDeleteRequest;
import cn.poria.message.service.im.tx.request.AccountInfoRequest;
import cn.poria.message.service.im.tx.response.AccountInfoResponse;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import cn.poria.message.vo.request.im.IMModifyAccountModel;
import cn.poria.message.vo.request.im.IMRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TxIMAccountService {

    @Autowired
    private TxIMProperties txIMProperties;

    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;

    public String createAccount(IMRegister imRegister){
        AccountCreateRequest request = new AccountCreateRequest();
        request.setFaceUrl(imRegister.getFaceUrl());
        request.setNickName(imRegister.getName());
        request.setAccountId(imRegister.getAccountId());
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.ACCOUNT_CREATE,txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(request));
        return imRegister.getAccountId();
    }


    public void updateAccount(IMModifyAccountModel model){
        AccountCreateRequest request = new AccountCreateRequest();
        request.setFaceUrl(model.getFaceUrl());
        request.setNickName(model.getName());
        request.setAccountId(model.getAccountId());
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.ACCOUNT_CREATE,txIMProperties.getAdminAccountName(),JSONUtil.toJsonStr(request));
    }


    public void deleteAccount(List<String> accountIds){
        AccountDeleteRequest requestBody = new AccountDeleteRequest();
        requestBody.setDeleteItem(accountIds.stream().map(AccountDeleteRequest.DelUserId::new).collect(Collectors.toList()));
        txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.ACCOUNT_DELETE,txIMProperties.getAdminAccountName(),JSONUtil.toJsonStr(requestBody));
        txIMMsgSendUtil.deleteSig(accountIds);
    }


    public Map<String, AccountInfo> findAccountInfo(List<String> accountIds){
        Map<String, AccountInfo>  accountInfoMap = new HashMap<>();
        AccountInfoRequest requestBody = new AccountInfoRequest();
        requestBody.setAccountIds(accountIds);
        JSONObject response = txIMMsgSendUtil.sendPostReq(TxMsgUrlConstant.ACCOUNT_INFO, txIMProperties.getAdminAccountName(), JSONUtil.toJsonStr(requestBody));
        if(response.getInt("ErrorCode") != 0){
            throw new ServiceException("腾讯im请求失败");
        }
        List<AccountInfoResponse> userProfileItems = response.getJSONArray("UserProfileItem").toList(AccountInfoResponse.class);
        if(CollUtil.isEmpty(userProfileItems)){
            return accountInfoMap;
        }

        for (AccountInfoResponse userProfileItem : userProfileItems) {
            String accountId = userProfileItem.getAccountId();
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setAccountId(accountId);
            if(userProfileItem.getProfiles() != null){
                List<AccountInfoResponse.ProfileItem> nickProfile = userProfileItem.getProfiles().stream().filter(e -> e.getTag().equals("Tag_Profile_IM_Nick")).collect(Collectors.toList());
                List<AccountInfoResponse.ProfileItem> faceUrlProfile = userProfileItem.getProfiles().stream().filter(e -> e.getTag().equals("Tag_Profile_IM_Image")).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(nickProfile)){
                    accountInfo.setNickName(nickProfile.get(0).getValue());
                }
                if(CollUtil.isNotEmpty(faceUrlProfile)){
                    accountInfo.setFaceUrl(faceUrlProfile.get(0).getValue());
                }
            }
            accountInfoMap.put(accountId,accountInfo);
        }
        return accountInfoMap;
    }
}
