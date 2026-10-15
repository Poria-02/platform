package cn.poria.common.excel.kit;

import java.io.Serial;

/**
 * @date 2020/3/31
 */
public class ExcelException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public ExcelException(String message) {
		super(message);
	}

}
