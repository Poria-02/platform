package cn.poria.message.service.im.tx.response;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

@Data
public class  AccountInfoResponse {

    @Alias("To_Account")
    private String accountId;

    @Alias("ProfileItem")
    private List<ProfileItem> profiles;


    @Data
    public static class ProfileItem {
        @Alias("Value")
        private String value;

        @Alias("Tag")
        private String Tag;
    }
}
