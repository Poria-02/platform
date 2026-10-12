package cn.poria.message.vo.request.cs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * author qiaodi
 * date 2025/9/8 15:57
 * version 6.7.3
 * description
 */
@Data
public class ContentSecurityModel{
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
    @NotBlank(message = "业务场景不能为空")
    @Schema(description = "aliyun业务场景")
    private String aliService;

    //审核内容
    /**
     * text:为文本
     * image：为图片地址
     * video：为地址
     */
    @NotBlank(message = "审核内容不能为空")
    @Schema(description = "审核内容,text:为文本;image：为图片地址;video：为地址")
    private String content;

    //审核记录ID
    private String auditId;

    //随机码，视频审核不能为空，随机字符串，该值用于回调通知请求中的签名。由英文字母、数字、下划线（_）组成，不超过64个字符。由您自定义，用于在接收到内容安全的回调通知时校验请求由阿里云内容安全服务发起
    private String seed;

    //视频回调地址，视频审核不能为空
    private String callback;
}
