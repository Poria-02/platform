package cn.poria.message.service.cs.service.impl;

import cn.poria.common.core.util.R;
import cn.poria.message.service.cs.service.ContentSecurityService;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import com.aliyun.green20220302.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * author qiaodi
 * date 2025/9/9 10:09
 * version 6.7.3
 * description
 */
@Service
@Slf4j
public class DocServiceImpl implements ContentSecurityService {

    private final Client aliContentSecurityClient;

    public DocServiceImpl(Client aliContentSecurityClient) {
        this.aliContentSecurityClient = aliContentSecurityClient;
    }

    @Override
    public R moderationPlus(ContentSecurityModel model) throws Exception {
        return null;
    }

    /**
     * document_detection：通用文档检测服务
     * @param aliService
     * @return
     */
    @Override
    public boolean support(String aliService) {
        return aliService.equals("document_detection");
    }
}
