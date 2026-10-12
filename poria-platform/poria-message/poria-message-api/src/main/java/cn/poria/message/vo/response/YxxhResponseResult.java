package cn.poria.message.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class YxxhResponseResult {

    @Schema(description = "响应结果")
    private String result;

    @Schema(description = "响应代码具体原因")
    private String message;

    @Schema(description = "绑定 ID --小号绑定关系唯一标识")
    private String bindId;

    @Schema(description = "绑定的小号")
    private String middleNumber;

}
