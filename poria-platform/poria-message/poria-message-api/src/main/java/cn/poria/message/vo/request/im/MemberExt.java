package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberExt {

    @Schema(description = "账号id")
    private String accountId;

    @Schema(description = "扩展信息")
    private Map<String, String> ext;

    @Schema(description = "角色")
    private String role = "Member";
}
