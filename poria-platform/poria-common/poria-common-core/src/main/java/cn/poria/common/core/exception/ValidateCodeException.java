package cn.poria.common.core.exception;

import java.io.Serial;

public class ValidateCodeException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -7285211528095468156L;

	public ValidateCodeException() {}

	public ValidateCodeException(String msg) {
		super(msg);
	}
}
