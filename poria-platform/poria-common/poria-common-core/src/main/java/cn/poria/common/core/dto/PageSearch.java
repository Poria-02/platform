package cn.poria.common.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 版权所有
 *
 * @Author: yuhaitao
 * @Date 2020/5/10 6:01 下午
 */
@Data
public class PageSearch {

	@NotNull(message = "第N页不能为空")
	@Schema(description = "第N页")
	private Integer current = 1;
	@NotNull(message = "每页N条不能为空")
	@Schema(description = "每页N条")
	private Integer size = 10;
}
