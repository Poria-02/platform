package cn.poria.message.vo.request.center;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公示公告发布状态修改请求
 */
@Data
@Schema(description = "公示公告发布状态修改请求")
public class CenterStatusModel {

    /**
     * 目标状态
     */
    @Schema(description = "公告状态：0-恢复草稿，2-撤回；发布发送使用独立发送接口", allowableValues = {"0", "2"})
    @NotNull
    @Min(0)
    @Max(2)
    private Integer status;
}
