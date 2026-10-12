package cn.poria.message.integration.visual.vo.request.alima;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author zhangchunlei
 * @date 2023年01月04日 2:29 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateMsgModel {

	@NotBlank(message = "用户id不能为空")
	@Schema(description = "用户id")
	private String userId;

	@NotBlank(message = "appId不能为空")
	private String appId;

	@NotBlank(message = "模版id不能为空")
	@Schema(description = "微信模版id")
	private String templateId;

	@Schema(description = "跳转url")
	private String url;

	@Schema(description = "参数列表")
	private Map<String, String> param;

}
