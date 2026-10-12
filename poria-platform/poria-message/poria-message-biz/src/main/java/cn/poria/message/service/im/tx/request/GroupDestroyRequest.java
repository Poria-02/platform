package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class GroupDestroyRequest {

    @Alias("GroupId")
    private String groupId;

}
