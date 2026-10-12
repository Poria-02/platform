package cn.poria.message.service.cs.service.impl;

import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.message.service.cs.service.ContentSecurityService;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import cn.poria.message.vo.response.cs.ContentSecurityVo;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.ImageModerationRequest;
import com.aliyun.green20220302.models.ImageModerationResponse;
import com.aliyun.green20220302.models.ImageModerationResponseBody;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * author qiaodi
 * date 2025/9/9 10:08
 * version 6.7.3
 * description
 */
@Service
@Slf4j
public class ImageServiceImpl implements ContentSecurityService {

    private final Client aliContentSecurityClient;

    public ImageServiceImpl(@Lazy Client aliContentSecurityClient) {
        this.aliContentSecurityClient = aliContentSecurityClient;
    }

    @Override
    public R moderationPlus(ContentSecurityModel model) throws Exception {
        //获取数据
        ImageModerationResponse response = invokeFunction(model);

        // 打印检测结果。
        if (response != null) {
            if (response.getStatusCode() == 200) {
                ImageModerationResponseBody body = response.getBody();
                log.info("内容安全image响应结果:{}", JSONUtil.toJsonStr(body));
                if (body.getCode() == 200) {
                    ImageModerationResponseBody.ImageModerationResponseBodyData data = body.getData();
                    log.info("image moderation success:{}", JSONUtil.toJsonStr(data));
                    ContentSecurityVo vo = getContentSecurityVo(data);
                    return R.ok(vo);
                } else {
                    log.info("image moderation not success. code:{}", body.getCode());
                    return R.failed(body.getCode(), body.getMsg());
                }
            } else {
                return R.failed(response.getStatusCode(), response.getBody().getMsg());
            }
        }
        return R.failed();
    }

    private ContentSecurityVo getContentSecurityVo(ImageModerationResponseBody.ImageModerationResponseBodyData data) {
        ContentSecurityVo vo = new ContentSecurityVo();
        vo.setDataId(data.getDataId());
        vo.setRiskLevel(data.getRiskLevel());
        ArrayList<ContentSecurityVo.ContentSecurityResult> list = new ArrayList<>();
        for (ImageModerationResponseBody.ImageModerationResponseBodyDataResult text : data.getResult()){
            ContentSecurityVo.ContentSecurityResult securityResult = new ContentSecurityVo.ContentSecurityResult();
            securityResult.setLabel(text.getLabel());
            securityResult.setDescription(text.getDescription());
            list.add(securityResult);
        }
        vo.setResult(list);
        return vo;
    }

    /**
     * baselineCheck：通用基线检测
     * baselineCheck_pro：通用基线检测_专业版
     * tonalityImprove：内容治理检测
     * aigcCheck：AIGC图片风险
     * profilePhotoCheck：头像图片检测
     * postImageCheck：帖子评论图片检测
     * advertisingCheck：营销素材检测
     * liveStreamCheck：视频\直播截图检测
     * riskDetection：恶意图片检测
     * @param aliService
     * @return
     */
    @Override
    public boolean support(String aliService) {
        return aliService.equals("baselineCheck")
                || aliService.equals("baselineCheck_pro")
                || aliService.equals("tonalityImprove")
                || aliService.equals("aigcCheck")
                || aliService.equals("profilePhotoCheck")
                || aliService.equals("postImageCheck")
                || aliService.equals("advertisingCheck")
                || aliService.equals("liveStreamCheck")
                || aliService.equals("riskDetection");
    }


    private ImageModerationResponse invokeFunction(ContentSecurityModel model) throws Exception {

        // 创建RuntimeObject实例并设置运行参数
        RuntimeOptions runtime = new RuntimeOptions();

        // 检测参数构造。
        Map<String, String> serviceParameters = new HashMap<>();
        //公网可访问的URL。
        serviceParameters.put("imageUrl", model.getContent());
        //待检测数据唯一标识
        if(StringUtils.isNotBlank(model.getAuditId())) serviceParameters.put("dataId", model.getAuditId());

        ImageModerationRequest request = new ImageModerationRequest();
        // 图片检测service：内容安全控制台图片增强版规则配置的serviceCode，示例：baselineCheck
        // 支持service请参考：https://help.aliyun.com/document_detail/467826.html?0#p-23b-o19-gff
        request.setService(model.getAliService());
        request.setServiceParameters(JSONUtil.toJsonStr(serviceParameters));

        ImageModerationResponse response = null;
        try {
            response = aliContentSecurityClient.imageModerationWithOptions(request, runtime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
