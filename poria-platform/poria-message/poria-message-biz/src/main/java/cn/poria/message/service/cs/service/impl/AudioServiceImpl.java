package cn.poria.message.service.cs.service.impl;

import cn.poria.common.core.util.R;
import cn.poria.message.service.cs.service.ContentSecurityService;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * author qiaodi
 * date 2025/9/9 14:41
 * version 6.7.3
 * description
 */
@Service
@Slf4j
public class AudioServiceImpl implements ContentSecurityService {


    @Override
    public R moderationPlus(ContentSecurityModel model) throws Exception {
        return null;
    }

    /**
     * audio_media_detection:音视频媒体检测
     * audio_multilingual_cb:音视频媒体多语言检测
     * @param aliService
     * @return
     */
    @Override
    public boolean support(String aliService) {
        return aliService.equals("audio_media_detection")
                || aliService.equals("audio_multilingual_cb");
    }
}
