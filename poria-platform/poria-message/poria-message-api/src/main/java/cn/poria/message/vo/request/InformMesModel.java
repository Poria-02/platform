package cn.poria.message.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class InformMesModel {

    /**
     * 账号ID
     */
    @Schema(description = "账号ID")
    @NotBlank(message = "患者账户id不能为空！")
    private String accountId;

    /**
     * 机构id
     */
    @Schema(description = "机构id")
    @NotNull(message = "机构id不能为空")
    private Long orgId;

    /**
     * 状态：0已发送 1发送失败
     */
    @Schema(description = "状态：0已发送 1发送失败")
    private Integer status;

    /**
     * 服务回调地址
     */
    @Schema(description = "服务回调地址")
    private String notifyUrl;

    /**
     * 业务id
     */
    @NotBlank(message = "业务id不能为空！")
    @Schema(description = "业务id")
    private String businessId;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    @NotBlank(message = "用户id不能为空！")
    private String patientId;

    /**
     * 用户姓名
     */
    @Schema(description = "患者姓名")
    private String patientName;

    /**
     * 患者手机号
     */
    @Schema(description = "患者手机号")
    private String patientPhone;

    /**
     * 通知方式
     */
    @Schema(description = "通知方式")
    @NotNull(message = "通知方式不能为空！")
    private Integer interveneModus;

    /**
     * 通知模板key
     */
    @Schema(description = "通知模板key")
    @NotBlank(message = "通知模板key不能为空！")
    private String msgTemplateKey;

    /**
     * 发送内容
     */
    @Schema(description = "发送内容")
    private Map<String,String> params;

}
