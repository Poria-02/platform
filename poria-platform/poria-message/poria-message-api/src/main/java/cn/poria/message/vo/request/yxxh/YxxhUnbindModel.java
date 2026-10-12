package cn.poria.message.vo.request.yxxh;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;
@Data
public class YxxhUnbindModel {

    @Schema(description = "小号号码")
    private String middleNumber;

    @Schema(description = "绑定小号的号码 A ")
    private String bindNumberA;

    @Schema(description = "绑定小号的号码 B")
    private String bindNumberB;

    @Schema(description = "解绑对应的模式 0: AXB; 1: AXN; 2: AX; 3: AXx;4: AXyb 模式 5: XYB 模式手工绑定")
    private String mode;

    @Schema(description = "自定义字段--业务id")
    @NotBlank(message = "业务id不能为空")
    private String customerData;

}
