package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class AccountCreateRequest {

    @Alias("UserID")
    private String accountId;

    @Alias("Nick")
    private String nickName;

    @Alias("FaceUrl")
    private String faceUrl;
}
