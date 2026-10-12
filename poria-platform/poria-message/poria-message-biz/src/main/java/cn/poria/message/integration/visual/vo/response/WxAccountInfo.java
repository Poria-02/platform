package cn.poria.message.integration.visual.vo.response;

import lombok.Data;

@Data
public class WxAccountInfo {

	/**
	 * 公众号名称
	 */
	private String name;


	/**
	 * 公众号appid
	 */
	private String appid;

	/**
	 * 机构id
	 */
	private Long orgId;

}
