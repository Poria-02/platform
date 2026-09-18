package cn.poria.common.log.filter;

import cn.hutool.core.util.IdUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnBooleanProperty(name = "sw.enabled",havingValue = false)
public class TraceIdFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader("X-Trace-ID");
        if (traceId == null) {
            traceId = IdUtil.randomUUID().toString();
            log.info("系统自生成traceId{}",traceId);
        }

        MDC.put("tid", traceId);

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("X-Trace-ID", traceId);

        chain.doFilter(request, response);
        MDC.clear();
    }
}
