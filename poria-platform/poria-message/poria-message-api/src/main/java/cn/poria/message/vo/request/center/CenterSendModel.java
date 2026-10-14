package cn.poria.message.vo.request.center;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 公示公告发送请求
 */
@Data
public class CenterSendModel {

    @NotEmpty(message = "接收人不能为空")
    @Schema(description = "接收人用户ID列表")
    private List<@NotBlank(message = "接收人用户ID不能为空") String> userIds;
}
