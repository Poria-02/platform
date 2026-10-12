package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class AccountDeleteRequest {


    @Alias("DeleteItem")
    private List<DelUserId> deleteItem;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DelUserId {
        @Alias("UserID")
        private String userId;
    }

}
