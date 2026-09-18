package cn.poria.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

	private Integer code;

	public ServiceException(String message) {
		super(message);
		this.code = 1;
	}

	public ServiceException(Integer code,String message) {
		super(message);
		this.code = code;
	}

}
