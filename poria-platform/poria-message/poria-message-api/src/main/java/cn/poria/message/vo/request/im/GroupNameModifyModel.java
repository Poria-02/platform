package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchunlei
 * @date 2022年02月14日 10:04 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupNameModifyModel {

    @Schema(description = "群组id")
    private String groupId;

    @Schema(description = "群名称")
    private String name;

    @Schema(description = "群简介")
    private String introduction;
}
