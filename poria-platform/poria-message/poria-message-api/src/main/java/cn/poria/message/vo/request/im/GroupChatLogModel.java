package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "群组id不能为空")
    @Schema(description = "群组id")
    private String groupId;

    @Schema(description = "开始时间")
    private Integer beginTime;

    @Schema(description = "结束时间")
    private Integer endTime;

    @Min(value = 1, message = "消息序号不能小于1")
    @Schema(description = "拉取消息的最大序号，首次不传，继续查询时传入响应的nextMsgSeq")
    private Long msgSeq;

    @Builder.Default
    @NotNull(message = "查询条数不能为空")
    @Min(value = 1, message = "查询条数不能小于1")
    @Max(value = 20, message = "查询条数不能超过20")
    @Schema(description = "本次拉取条数，范围1-20，默认20")
    private Integer limit = 20;

}
