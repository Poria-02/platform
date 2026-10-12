package cn.poria.message.vo.response.im;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GroupMemberVo {

    @Schema(description = "账号id")
    private String accountId;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "入群时间")
    private long joinTime;

}
