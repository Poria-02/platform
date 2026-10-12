package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author qiaodi
 * date 2025/11/12 15:18
 * version 6.8.1
 * description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferGroupModel {

    @Schema(description = "被转让的群组id")
    private String groupId;

    @Schema(description = "新群主账号id")
    private String newOwnerAccountId;
}
