package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchunlei
 * @date 2021年12月27日 3:30 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMModifyAccountModel {

    @Schema(description = "头像地址")
    private String faceUrl;

    @Schema(description = "账号名称")
    private String name;

    @Schema(description = "账户id")
    private String accountId;

}
