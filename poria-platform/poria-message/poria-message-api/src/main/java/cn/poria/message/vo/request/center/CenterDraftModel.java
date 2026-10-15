package cn.poria.message.vo.request.center;

import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 站内信及公示公告内容请求
 */
@Data
@Schema(description = "站内信及公示公告新增、编辑请求")
public class CenterDraftModel {

    /**
     * 公告ID
     */
    @Schema(description = "公告ID，修改时必传，新增时不传")
    @NotBlank(message = "公告ID不能为空", groups = Update.class)
    @Null(message = "新增公告时无需传入ID", groups = Create.class)
    private String id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题，最多200字", maxLength = 200)
    @NotBlank(message = "标题不能为空")
    @Size(max = 200)
    private String title;

    /**
     * 消息正文
     */
    @Schema(description = "消息正文，站内信最多5000字，公示公告最多20000字", maxLength = 20000)
    @NotBlank(message = "正文不能为空")
    @Size(max = 20000)
    private String content;

    /**
     * 站内信关联业务ID
     */
    @Schema(description = "站内信关联业务ID，最多19位，公告不使用此字段", maxLength = 19)
    @Size(max = 19)
    private String businessId;
}
