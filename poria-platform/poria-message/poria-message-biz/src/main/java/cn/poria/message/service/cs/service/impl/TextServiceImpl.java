package cn.poria.message.service.cs.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.R;
import cn.poria.message.service.cs.service.ContentSecurityService;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import cn.poria.message.vo.response.cs.ContentSecurityVo;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author qiaodi
 * date 2025/9/9 10:07
 * version 6.7.3
 * description
 */
@Service
@Slf4j
public class TextServiceImpl implements ContentSecurityService {

    private final Client aliContentSecurityClient;

    public TextServiceImpl(@Lazy Client aliContentSecurityClient) {
        this.aliContentSecurityClient = aliContentSecurityClient;
    }

    @Override
    public R moderationPlus(ContentSecurityModel model) throws Exception {
        JSONObject serviceParameters = new JSONObject();
        serviceParameters.set("content", model.getContent());

        TextModerationPlusRequest textModerationPlusRequest = new TextModerationPlusRequest();
        textModerationPlusRequest.setService(model.getAliService());// 检测类型
        textModerationPlusRequest.setServiceParameters(JSONUtil.toJsonStr(serviceParameters));
        log.info("内容安全text请求参数:{}", serviceParameters);

        TextModerationPlusResponse response = aliContentSecurityClient.textModerationPlus(textModerationPlusRequest);
        if (response.getStatusCode() == 200) {
            TextModerationPlusResponseBody result = response.getBody();
            log.info("内容安全text响应结果:{}", JSONUtil.toJsonStr(result));
            Integer code = result.getCode();
            if (200 == code) {
                TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = result.getData();
                log.info("text moderation success:{}", JSONUtil.toJsonStr(data));
                ContentSecurityVo vo = getContentSecurityVo(data);
                return R.ok(vo);
            } else {
                log.info("text moderation not success. code:{}", code);
                return R.failed(code, result.getMessage());
            }
        } else {
            log.info("response not success. status:{}", response.getStatusCode());
            return R.failed(response.getStatusCode(), response.getBody().getMessage());
        }
    }


    private ContentSecurityVo getContentSecurityVo(TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data) {
        ContentSecurityVo vo = new ContentSecurityVo();
        vo.setDataId(data.getDataId());
        vo.setRiskLevel(data.getRiskLevel());
        ArrayList<ContentSecurityVo.ContentSecurityResult> list = new ArrayList<>();
        for (TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult text : data.getResult()){
            ContentSecurityVo.ContentSecurityResult securityResult = new ContentSecurityVo.ContentSecurityResult();
            securityResult.setLabel(text.getLabel());
            securityResult.setDescription(text.getDescription());
            list.add(securityResult);
        }
        vo.setResult(list);
        return vo;
    }

    /**
     * nickname_detection_pro：用户昵称检测_专业版
     * nickname_detection：用户昵称检测
     * chat_detection_pro：私聊互动内容检测_专业版
     * chat_detection：私聊互动内容检测
     * comment_detection_pro：公聊评论内容检测_专业版
     * comment_detection：公聊评论内容检测
     * ad_compliance_detection_pro：广告法合规检测_专业版
     * ad_compliance_detection：广告法合规检测
     * ugc_moderation_byllm：UGC场景文本审核大模型服务
     * ai_art_detection：AIGC文字检测
     * pgc_detection：PGC教学物料检测
     * url_detection：URL风险链接检测
     * @param aliService
     * @return
     */
    @Override
    public boolean support(String aliService) {
        return aliService.equals("nickname_detection_pro")
                || aliService.equals("nickname_detection")
                || aliService.equals("chat_detection_pro")
                || aliService.equals("chat_detection")
                || aliService.equals("comment_detection_pro")
                || aliService.equals("comment_detection")
                || aliService.equals("ad_compliance_detection_pro")
                || aliService.equals("ad_compliance_detection")
                || aliService.equals("ugc_moderation_byllm")
                || aliService.equals("ai_art_detection")
                || aliService.equals("pgc_detection")
                || aliService.equals("url_detection");
    }
}
