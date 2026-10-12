package cn.poria.message.service.cs.service;

import cn.poria.common.core.util.R;
import cn.poria.message.vo.request.cs.ContentSecurityModel;

/**
 * author qiaodi
 * date 2025/9/8 15:49
 * version 6.7.3
 * description
 */
public interface ContentSecurityService {


    R moderationPlus(ContentSecurityModel model) throws Exception;

    boolean support(String aliService) ;
}
