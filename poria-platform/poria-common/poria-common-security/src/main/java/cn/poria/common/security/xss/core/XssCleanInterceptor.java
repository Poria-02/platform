package cn.poria.common.security.xss.core;

import cn.poria.common.security.xss.config.MicaXssProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.List;

/**
 * xss 处理拦截器
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class XssCleanInterceptor implements AsyncHandlerInterceptor {
	private final PathMatcher matcher = new AntPathMatcher();
	private final MicaXssProperties xssProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 1. 非控制器请求直接跳出
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		// 2. 没有开启
		if (!xssProperties.isEnabled()) {
			return true;
		}
		// 判断是否需要跳过
		List<String> pathExcludePatterns = xssProperties.getPathExcludePatterns();
		String requestURL = request.getRequestURI();
		boolean needExclude = pathExcludePatterns.stream()
			.anyMatch(pattern -> matcher.match(pattern, requestURL));
		if (needExclude) {
			XssHolder.setIgnore(new XssIgnoreVo());
			return true;
		}
		// 3. 处理 XssIgnore 注解
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		// 先找方法，再找方法上的类
		XssCleanIgnore xssCleanIgnore = handlerMethod.getMethodAnnotation(XssCleanIgnore.class);
		if (null == xssCleanIgnore) {
			Class<?> beanType = handlerMethod.getBeanType();
			xssCleanIgnore = AnnotatedElementUtils.findMergedAnnotation(beanType, XssCleanIgnore.class);
		}
		if (xssCleanIgnore != null) {
			XssHolder.setIgnore(new XssIgnoreVo(xssCleanIgnore.value()));
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		XssHolder.remove();
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		XssHolder.remove();
	}
}
