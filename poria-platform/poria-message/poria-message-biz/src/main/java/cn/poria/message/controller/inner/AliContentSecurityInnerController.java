package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.cs.service.ContentSecurityContext;
import cn.poria.message.vo.request.cs.ContentSecurityModel;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author qiaodi
 * date 2025/9/9 10:55
 * version 6.7.3
 * description
 */
@Slf4j
@RestController
@RequestMapping("/inner/content/security")
@Hidden
public class AliContentSecurityInnerController {

    @Resource
    private ContentSecurityContext contentSecurityContext;

    /**
     * 内容安全阿里审核
     * @return
     */
    @Inner
    @PostMapping
    public R audit(@RequestBody @Validated ContentSecurityModel model){
        return contentSecurityContext.audit(model);
    }

}
