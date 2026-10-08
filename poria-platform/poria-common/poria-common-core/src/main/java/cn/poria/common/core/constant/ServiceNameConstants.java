package cn.poria.common.core.constant;

/**
 * 服务名称
 */
public interface ServiceNameConstants {
	/** 认证中心 */
	String AUTH_SERVICE = "${PORIA_auth:http://poria-auth:3000}";

	/** UMPS模块 */
	String UPMS_SERVICE = "${PORIA_UPMS:http://poria-upms:4000}";


	String MESSAGE_SERVICE = "${PORIA_MESSAGE:http://poria-message:8007}";

	String BASE_SERVICE = "${PORIA_BASE:http://poria-base:8009}";

	String LOG_SERVICE = "${PORIA_LOG:http://poria-log:8046}";

}
