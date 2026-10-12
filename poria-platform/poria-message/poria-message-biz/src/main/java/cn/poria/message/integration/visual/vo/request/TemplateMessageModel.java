package cn.poria.message.integration.visual.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class TemplateMessageModel {

//	@Schema(description = "用户id")
//	private String accountId;

	@Schema(description = "用户openId")
	private String openId;

//	@Schema(description = "机构id")
//	private Long orgId;

	private String appId;

	@Schema(description = "微信模版id")
	private String templateId;

	@Schema(description = "跳转url")
	private String url;

	@Schema(description = "参数列表")
	private Map<String,String> param;

}
