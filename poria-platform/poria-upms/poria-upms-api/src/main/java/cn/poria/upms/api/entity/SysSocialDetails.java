package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "第三方账号信息")
public class SysSocialDetails extends Model<SysSocialDetails> {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "账号类型")
    @NotBlank(message = "类型不能为空")
    private String type;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "appId")
    @NotBlank(message = "账号不能为空")
    private String appId;

    @Schema(description = "app secret")
    @NotBlank(message = "密钥不能为空")
    private String appSecret;

    @Schema(description = "回调地址")
    private String redirectUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;
}
