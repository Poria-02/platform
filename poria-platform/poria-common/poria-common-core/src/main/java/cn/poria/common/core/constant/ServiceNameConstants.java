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


	/**
	 * 推荐
	 */
	String RECOMMEND_SERVICE = "${PORIA_RECOMMEND:http://poria-recommend:8015}";

	String HEALTH_SERVICE = "${PORIA_HEALTH:http://poria-health:8016}";
	/**
	 * 处方
	 */
	String PRESCRIPTION_SERVICE = "${PORIA_PRESCRIPTION:http://poria-prescription:8020}";


	String RPQ_SERVICE = "${PORIA_RPQ:http://poria-rpq:8021}";

	/**
	 * 知识库
	 */
	String KNOWLEDGE_SERVICE = "${PORIA_INDICATOR:http://poria-knowledge:8035}";

	String IHOSPITALTOJK_SERVICE = "${PORIA_IHOSPITALTOJK:http://poria-ihospitaltojk:8030}";


	String MANAGEMENT_SERVICE = "${PORIA_MANAGEMENT:http://poria-management:8038}";

	String CHRONIC_SERVICE = "${PORIA_CHRONIC:http://poria-chronic:8029}";



	/**
	 * 微信小程序
	 */
	String WXMA_SERVICE = "${PORIA_WXMA:http://poria-wxma:8023}";

	/**
	 * 物流
	 */
	String TRANSPORT_SERVICE = "${PORIA_TRANSPORT:http://poria-transport:8025}";


	String THIRD_SERVICE = "${PORIA_THIRD:http://poria-third:8026}";

	String ARCHIVE_SERVICE = "${PORIA_ARCHIVE:http://poria-archive:8028}";

	String INQUIRY_SERVICE = "${PORIA_ARCHIVE:http://poria-inquiry:8037}";

	String ALIMA_SERVICE = "${PORIA_ALIMA:http://poria-alima:8034}";

	String RIGHT_SERVICE = "${PORIA_RIGHT:http://poria-right:8039}";

	/**
	 * 问卷
	 */
	String TDUCK_SERVICE = "${PORIA_TDUCK:http://poria-tduck:8998}";

	String HOMECARE_SERVICE = "${PORIA_HOMECARE:http://poria-homecare:8018}";

	/**
	 * 家庭病床
	 */
	String HOMEBED_SERVICE = "${PORIA_HOMEBED:http://poria-homebed:8048}";

	String SICKBED_SERVICE = "${PORIA_SICKBED:http://poria-sickbed:8040}";


	String SYNC_SERVICE = "${PORIA_SICKBED:http://poria-sync:8031}";

	String SCREENING_SERVICE = "${PORIA_SICKBED:http://poria-screening:8041}";

	String LOG_SERVICE = "${PORIA_LOG:http://poria-log:8046}";

	/**
	 * 处方流转平台
	 */
	String PTP_SERVICE = "${PORIA_PTP:http://poria-ptp:8047}";

	/**
	 * 专家会诊
	 */
	String MDT_SERVICE = "${PORIA_MDT:http://poria-mdt:8052}";

	/**
	 * 设备租赁
	 */
	String EL_SERVICE = "${PORIA_EL:http://poria-equipmentlease:8054}";

	/**
	 * 审核
	 */
	String REVIEW_SERVICE = "${PORIA_REVIEW:http://poria-review:8055}";
}
