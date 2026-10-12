package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchunlei
 * @date 2022年01月06日 2:35 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelGroupMemberModel {

    @Schema(description = "群组id")
    private String groupId;

    @Schema(description = "群成员账号id")
    private String member;

}
