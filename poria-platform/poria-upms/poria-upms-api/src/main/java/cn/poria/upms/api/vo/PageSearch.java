package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageSearch {

    @Schema(description = "第N页")
    @NotNull(message = "第N页不能为空")
    private Integer current = 1;

    @Schema(description = "每页N条")
    @NotNull(message = "每页N条不能为空")
    private Integer size = 10;
}
