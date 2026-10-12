package cn.poria.message.service.cs.service;

import cn.poria.common.core.util.R;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author qiaodi
 * date 2025/9/9 11:03
 * version 6.7.0
 * description
 */
@Component
public class ContentSecurityContext {

    private final List<ContentSecurityService> securityServices;


    public ContentSecurityContext(List<ContentSecurityService> securityServices){
        this.securityServices = securityServices;
    }

    @SneakyThrows
    public R audit(ContentSecurityModel model) {
        R result = R.failed("暂不支持的审核场景");
        for(ContentSecurityService css : securityServices){
            if (css.support(model.getAliService())){
                result = css.moderationPlus(model);
                break;
            }
        }
        return result;
    }
}
