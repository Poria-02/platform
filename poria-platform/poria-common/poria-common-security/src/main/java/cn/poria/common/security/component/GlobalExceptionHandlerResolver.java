package cn.poria.common.security.component;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
@Order()
public class GlobalExceptionHandlerResolver {
	/**
	 * 自定义服务器内部异常
	 */
	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(HttpStatus.OK)
	public R handleServiceException(ServiceException e) {
		log.error("服务器内部异常", e);
		return R.restResult(null,e.getCode(),e.getMessage());
	}

	/**
	 * 自定义服务器内部异常
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.OK)
	public R handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		log.error("请求方式{}不支持",e.getMethod());
		return R.failed(String.format("请求方式%s不支持",e.getMethod()));
	}

	/**
	 * 全局异常.
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R handleGlobalException(Exception e) {
		log.error("全局异常信息 ex={}", e.getMessage(), e);
        String tid = StrUtil.isEmpty(TraceContext.traceId())? "Tid:" +  MDC.get("tid"): "TID:" + TraceContext.traceId();
//        JSONObject log = JSONUtil.createObj()
//                .set("service", System.getProperty("spring.application.name", "unknown-service"))
//                .set("userId", SecurityUtils.getSId())
//                .set("clientId", SecurityUtils.getUser().getLoginType())
//                .set("errorMsg", e.getMessage())
//                .set("traceId", tid)
//                .set("createTime",new Date())
//                .set("status", 0);
//        KafkaUtil.send(PlatAuthConstant.LOG_SYS_ERROR_LOG_TOPIC, JSONUtil.toJsonStr(log));
		return R.failed(tid, "服务器内部错误");
	}

	/**
	 * validation Exception
	 */
	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleBodyValidException(MethodArgumentNotValidException exception) {
		List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
		log.warn("参数绑定异常,ex = {}", allErrors.getFirst().getDefaultMessage());
		return R.failed(allErrors.getFirst().getDefaultMessage());
	}

	/**
	 * validation Exception (以form-data形式传参)
	 */
	@ExceptionHandler({BindException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R bindExceptionHandler(BindException exception) {
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		log.warn("参数绑定异常,ex = {}", fieldErrors.getFirst().getDefaultMessage());
		return R.failed(fieldErrors.getFirst().getDefaultMessage());
	}


	@SneakyThrows
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public R handleException(HttpMessageNotReadableException ex, HttpServletRequest request) {
		String rawBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8); // 记录原始请求体
		log.error("Invalid JSON received: {}", rawBody, ex);
		return R.failed("请求信息解析错误");
	}

	@ExceptionHandler({AccessDeniedException.class})
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public R accessDeniedExceptionHandler(AccessDeniedException exception) {
		return R.failed("无效的权限");
	}

}
