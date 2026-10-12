package cn.poria.message.vo.request.yxxh;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;

@Data
public class YxxhAXBModel {

    @Schema(description = "小号号码")
    private String middleNumber;

    @Schema(description = "绑定小号的号码 A ")
    @NotBlank
//    @Pattern(regexp = "^[1][0-9]{10}$", message = "请输入正确的手机号码")
    private String bindNumberA;

    @Schema(description = "绑定小号的号码 B")
    @NotBlank
//    @Pattern(regexp = "^[1][0-9]{10}$", message = "请输入正确的手机号码")
    private String bindNumberB;

    @Schema(description = "绑定关系有效时长")
    private Integer maxBindingTime;

    @Schema(description = "允许的最大通话时单位秒，默认不限制时长")
    private Integer maxCallDuration;

    @Schema(description = "呼叫结果推送地")
    private String callbackUrl;

    @Schema(description = "号码 A 拨打小号时候的放音编号")
    private String announceCodeToA;

    @Schema(description = "号码 B 拨打小号时候的放音编号")
    private String announceCodeToB;

    @Schema(description = "其他号码拨打小号时候的放音编号")
    private String announceCodeToOther;

    @Schema(description = "区间号")
    private String areaCode;

    @Schema(description = "自定义字段--业务id")
    @NotBlank(message = "业务id不能为空")
    private String customerData;
}
