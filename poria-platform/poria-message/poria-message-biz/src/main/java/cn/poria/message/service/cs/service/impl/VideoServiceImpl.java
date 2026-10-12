package cn.poria.message.service.cs.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.message.service.cs.service.ContentSecurityService;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import cn.poria.message.vo.response.cs.ContentSecurityVo;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.VideoModerationRequest;
import com.aliyun.green20220302.models.VideoModerationResponse;
import com.aliyun.green20220302.models.VideoModerationResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

/**
 * author qiaodi
 * date 2025/9/9 10:09
 * version 6.7.3
 * description
 */
@Service
@Slf4j
public class VideoServiceImpl implements ContentSecurityService {

    private final Client aliContentSecurityClient;

    public VideoServiceImpl(@Lazy Client aliContentSecurityClient) {
        this.aliContentSecurityClient = aliContentSecurityClient;
    }

    @Override
    public R moderationPlus(ContentSecurityModel model) throws Exception {

        JSONObject serviceParameters = new JSONObject();
        serviceParameters.set("url", model.getContent());
        if(StringUtils.isNotBlank(model.getAuditId())) serviceParameters.set("dataId", model.getAuditId());
        serviceParameters.set("callback", model.getCallback()); //回调地址,支持使用HTTP和HTTPS协议的地址。该字段为空时，您必须定时轮询检测结果
        serviceParameters.set("seed", model.getSeed()); //随机字符串，该值用于回调通知请求中的签名。由英文字母、数字、下划线（_）组成，不超过64个字符。由您自定义，用于在接收到内容安全的回调通知时校验请求由阿里云内容安全服务发起。

        VideoModerationRequest videoModerationRequest = new VideoModerationRequest();
        // 检测类型：videoDetection
        videoModerationRequest.setService("videoDetection");
        videoModerationRequest.setServiceParameters(JSONUtil.toJsonStr(serviceParameters));
        log.info("内容安全video请求参数:{}", videoModerationRequest);

        VideoModerationResponse response = aliContentSecurityClient.videoModeration(videoModerationRequest);
        if (response.getStatusCode() == 200) {
            VideoModerationResponseBody result = response.getBody();
            log.info("内容安全video响应结果:{}", JSONUtil.toJsonStr(result));
            Integer code = result.getCode();
            if (200 == code) {
                VideoModerationResponseBody.VideoModerationResponseBodyData data = result.getData();
                log.info("video moderation success:{}", JSONUtil.toJsonStr(data));
                ContentSecurityVo vo = new ContentSecurityVo();
                vo.setDataId(data.getDataId());
                vo.setTaskId(data.getTaskId());
                return R.ok(vo);
            } else {
                log.info("video moderation not success. code:{}", code);
                return R.failed(code, result.getMessage());
            }
        } else {
            log.info("response not success. status:{}", response.getStatusCode());
            return R.failed(response.getStatusCode(), response.getBody().getMessage());
        }
    }

    /**
     * videoDetection:视频文件检测
     * @param aliService
     * @return
     */
    @Override
    public boolean support(String aliService) {
        return aliService.equals("videoDetection");
    }
}
