package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author zhangchunlei
 * @date 2022年02月14日 10:04 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberNickModel {

    @Schema(description = "群组id")
    private String groupId;

    @Schema(description = "群成员账号id")
    private String member;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "扩展信息")
    private Map<String, String> ext;
}
