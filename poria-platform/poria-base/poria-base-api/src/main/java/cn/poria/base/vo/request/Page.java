package cn.poria.base.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Page {

     @Schema(description = "第N页")
    private Integer current = 1;

     @Schema(description = "每页N条")
    private Integer size = 10;

}

