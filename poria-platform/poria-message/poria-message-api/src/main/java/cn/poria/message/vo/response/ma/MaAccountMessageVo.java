package cn.poria.message.vo.response.ma;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class MaAccountMessageVo {

	private String id;

	@Schema(description = "名字")
	private String name;

	@Schema(description = "账号")
	private String account;

	@Schema(description = "appid")
	private String appid;

	@Schema(description = "appsecret")
	private String appsecret;

	@Schema(description = "回调地址")
	private String url;

	@Schema(description = "令牌")
	private String token;

	@Schema(description = "秘钥")
	private String aeskey;

	@Schema(description = "二维码")
	private String qrUrl;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "更新时间")
	private Date updateTime;

	@Schema(description = "机构Id")
	private Long orgId;
}
