package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchunlei
 * @date 2021年12月29日 5:35 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatLogModel {

    @Schema(description = "账号id")
    private String groupId;

    @Schema(description = "开始时间")
    private Integer beginTime;

    @Schema(description = "结束时间")
    private Integer endTime;

}
