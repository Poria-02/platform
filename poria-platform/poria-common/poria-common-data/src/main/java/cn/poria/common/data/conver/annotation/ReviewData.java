package cn.poria.common.data.conver.annotation;

import java.lang.annotation.*;

/**
 * author qiaodi
 * date 2025/9/18 09:03
 * version 6.7.3
 * description
 *
 * 1.先发后审必填
 * serviceId = ServiceNameConstants.STAFF_SERVICE,
 * TABLE = "staff_raise",
 * id = "#model.id",
 * value = "#content",要审核的内容值，
 * dbKey = "id",搜索条件数据库字段
 * dbValue = "content",搜索结果数据库字段
 * aliYunService = "comment_detection_pro",
 * reviewMethod = "#model.reviewMethod"先发后审
 * orgId = "#model.orgId",
 * machineReview = "#model.machineReview"绝对值参考值
 *
 * 2.先审后发必填
 * serviceId = ServiceNameConstants.STAFF_SERVICE,
 * TABLE = "staff_raise",
 * id = "#model.id",
 * value = "#content",要审核的内容值，
 * dbKey = "id", 搜索条件数据库字段
 * dbValue = "content",更新条件数据库字段
 * aliYunService = "comment_detection_pro",
 * reviewMethod = "#model.reviewMethod",先审后发
 * orgId = "#model.orgId",
 * machineReview = "#model.machineReview" 绝对值参考值
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReviewData {

    /**
     * 主键ID
     * @return
     */
    String id();

    /**
     * 内容
     * @return
     */
    String value() default "";

    /**
     * 数据库原始字段 如果为空将使用key转换成下划线的形式
     * @return
     */
    String dbKey() default "";

    /**
     * 数据库绑定字段,如果为空将使用value转换成下划线的形式
     * @return
     */

    String dbValue() default  "";
    /**
     * 服务名,如果为空则在本地数据库查询,
     * @return
     */
    String serviceId() default "";

    /**
     * 表名
     * @return
     */
    String table() ;

    String condition() default "";

    /**
     * ali业务场景
     * 如需多个场景验证，以逗号分隔，例:"baselineCheck,baselineCheck_pro"
     *
     * 文本
     * nickname_detection_pro：用户昵称检测_专业版
     * chat_detection_pro：私聊互动内容检测_专业版
     * comment_detection_pro：公聊评论内容检测_专业版
     * ad_compliance_detection_pro：广告法合规检测_专业版
     * ugc_moderation_byllm：UGC场景文本审核大模型服务
     * comment_multilingual_pro_cb：国际业务多语言检测_出海版
     *
     * 图片
     * baselineCheck：通用基线检测
     * baselineCheck_pro：通用基线检测_专业版
     * tonalityImprove：内容治理检测
     * aigcCheck：AIGC图片风险
     * profilePhotoCheck：头像图片检测
     * postImageCheck：帖子评论图片检测
     * advertisingCheck：营销素材检测
     * liveStreamCheck：视频\直播截图检测
     * riskDetection：恶意图片检测
     *
     * 视频
     * videoDetection：视频文件检测
     * videoDetectionByVL：视频文件检测_大模型版
     * videoAigcDetector：AIGC视频生成判定
     */
    String aliYunService() default "";

    /**
     * 审核方式：先发后审push_review、先审后发review_push
     * @return
     */
    String reviewMethod() default "";

    /**
     * 绝对值1/参考值2
     * @return
     */
    String machineReview() default "";

    /**
     * 类型
     * 评论类(新增)：comment
     * 更新类(更新某处内容)：modify
     * @return
     */
    String type() default "modify";
}