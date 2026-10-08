package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "日志")
public class SysLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "日志编号")
    private Long id;

    @Schema(description = "日志类型")
    @NotBlank(message = "日志类型不能为空")
    private String type;

    @Schema(description = "日志标题")
    @NotBlank(message = "日志标题不能为空")
    private String title;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "操作ip地址")
    private String remoteAddr;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "请求uri")
    private String requestUri;

    @Schema(description = "操作方式")
    private String method;

    @Schema(description = "提交数据")
    private String params;

    @Schema(description = "方法执行时间")
    private Long time;

    @Schema(description = "异常信息")
    private String exception;

    @Schema(description = "应用标识")
    private String serviceId;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;
}
