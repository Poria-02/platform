//package cn.poria.gateway.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Component
//@Slf4j
//public class XSSProtectionFilter implements GlobalFilter, Ordered {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        // 检查 URL 查询参数
//        String rawQuery = request.getURI().getRawQuery();
//        if (rawQuery != null && containsXSS(rawQuery)) {
//            return blockRequest(exchange);
//        }
//
//        // 检查请求头
//        for (var entry : request.getHeaders().entrySet()) {
//            for (String value : entry.getValue()) {
//                if (containsXSS(value)) {
//                    return blockRequest(exchange);
//                }
//            }
//        }
//
//        // 检查 Content-Type 为 application/json 的 POST / PUT 请求体
//        List<String> jsonMethods = List.of("POST", "PUT");
//        if (jsonMethods.contains(request.getMethod().name()) &&
//            MediaType.APPLICATION_JSON.includes(request.getHeaders().getContentType())) {
//
//            return exchange.getRequest().getBody()
//                .map(dataBuffer -> {
//                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                    dataBuffer.read(bytes);
//                    return new String(bytes, StandardCharsets.UTF_8);
//                })
//                .collectList()
//                .flatMap(bodyList -> {
//                    String body = String.join("", bodyList);
//                    log.info("请求体body:{} 请求长度:{}:", body,exchange.getRequest().getHeaders().getContentLength());
//                    if (containsXSS(body)) {
//                        return blockRequest(exchange);
//                    }
//                    //  重新包装请求体，保证后续组件可以读取
//                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
//                        @Override
//                        public Flux<DataBuffer> getBody() {
//                            return Flux.just(exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)));
//                        }
//                    };
//
//                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
//                });
//        }
//
//        return chain.filter(exchange);
//    }
//
//    /**
//     * 判断是否包含 XSS 相关字符
//     */
//    private boolean containsXSS(String value) {
//        String lowerValue = value.toLowerCase();
//        return lowerValue.contains("<script>") ||
//               lowerValue.contains("</script>") ||
//               lowerValue.contains("javascript:") ||
//               lowerValue.contains("onerror=") ||
//               lowerValue.contains("onload=") ||
//               lowerValue.contains("alert(") ||
//               lowerValue.contains("eval(");
//    }
//
//    /**
//     * 阻止请求并返回 400 BAD REQUEST
//     */
//    private Mono<Void> blockRequest(ServerWebExchange exchange) {
//        log.error("Blocked request due to XSS attack");
//        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
//        return exchange.getResponse().setComplete();
//    }
//
//    @Override
//    public int getOrder() {
//        return -1; // 高优先级
//    }
//}
