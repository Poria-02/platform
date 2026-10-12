package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 * author qiaodi
 * date 2025/11/12 15:23
 * version 6.8.1
 * description
 */
@Data
public class GroupTransferRequest {

    /**
     * 必填,要被转移的群组 ID
     */
    @Alias("GroupId")
    private String groupId;


    /**
     * 必填,新群主 ID
     */
    @Alias("NewOwner_Account")
    private String newOwnerAccountId;

    /**
     * 选填,指定的操作者账号
     */
    @Alias("From_Account")
    private String fromAccountId;
}
