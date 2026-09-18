package cn.poria.common.log.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.poria.commom.kafka.KafkaUtil;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.log.constant.SysLogConstant;
import cn.poria.common.log.util.SysLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 操作日志使用spring event异步入库
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {

	@Autowired
	private ApplicationEventPublisher publisher;


	/**
	 * 正确返回时记录日志
	 * @param point
	 * @param sysLog
	 * @param result
	 */
	@AfterReturning(pointcut = "@annotation(sysLog)", returning = "result")
	public void afterReturning(JoinPoint point, SysLog sysLog,  Object result) {
		triggerSysLogEvent(point, sysLog, null);
	}

	/**
	 * 抛出异常时记录日志
	 * @param point
	 * @param sysLog
	 * @param e
	 */
	@AfterThrowing(pointcut = "@annotation(sysLog)", throwing = "e")
	public void afterThrowing(JoinPoint point, SysLog sysLog, Exception e){
		triggerSysLogEvent(point, sysLog, e);
	}

	private void triggerSysLogEvent(JoinPoint point, SysLog sysLog, Exception e){
		String strClassName  = point.getTarget().getClass().getName();
		String strMethodName = point.getSignature().getName();
		log.debug("[类名]:{},[方法]:{}", strClassName, strMethodName);

		cn.poria.common.log.service.SysLog logVo = SysLogUtils.getSysLog();
		logVo.setTitle(sysLog.value());

		//获取请求参数
		String params = JSONUtil.toJsonStr(point.getArgs());

		if(StrUtil.isBlank(params)){
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			params = JSONUtil.toJsonStr(request.getParameterMap());
		}
		logVo.setParams(params);
		//发送异步日志事件
		Long startTime 	= System.currentTimeMillis();
		Long endTime 	= System.currentTimeMillis();
		logVo.setTime(endTime - startTime);
		if (null != e)logVo.setException(e.getMessage());//如果有异常则记录异常信息
//        KafkaUtil.send(SysLogConstant.SYS_LOG_KAFKA_TOPIC,JSONUtil.toJsonStr(logVo));
	}
}
