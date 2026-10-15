package cn.poria.message.vo.request.center;

import cn.poria.message.vo.request.im.IMPush;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 站内信模板发送请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationLetterModel {

    @NotEmpty(message = "接收人不能为空")
    @Schema(description = "接收人用户ID列表")
    private List<@NotBlank(message = "接收人用户ID不能为空") String> userIds;

    @Schema(description = "业务ID")
    private String businessId;

    @Schema(description = "参数列表")
    private Map<String, String> param;

    @NotBlank(message = "模版key不能为空")
    @Schema(description = "模版key")
    private String templateKey;

    @Schema(description = "im系统消息参数")
    private IMPush imPush;
}
