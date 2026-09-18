package cn.poria.gateway.filter;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Component
public class SyncRequestFilter implements GlobalFilter, Ordered {
//	private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Value("${sync-encrypt-key}")
    private String key;

    @Autowired
    ServerCodecConfigurer codecConfigurer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("请求开始---》{}", LocalDateTime.now());
        ServerHttpRequest request = exchange.getRequest();
        String isEncrypt = request.getHeaders().getFirst("isEncrypt");
        if ("true".equals(isEncrypt)) {
            Class inClass = String.class;
            Class outClass = String.class;
//			ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
            ServerRequest serverRequest = ServerRequest.create(exchange, codecConfigurer.getReaders());

            //解密生成新的报文
            Mono<?> modifiedBody = serverRequest.bodyToMono(inClass).flatMap(decryptAES());

            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            headers.remove(HttpHeaders.CONTENT_LENGTH);
//			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
                return chain.filter(exchange.mutate().request(decorator).build());
            }));
        }
        log.debug("请求结束---》{}", LocalDateTime.now());
        return chain.filter(exchange);
    }

    private ServerHttpRequest decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }

            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }

                return httpHeaders;
            }
        };
    }

    private Function decryptAES() {
        return s -> {
            //构建前端对应解密AES 因子
            AES aes = new AES(key.getBytes());

            //获取请求密码并解密
            Map inParamsMap;
            String param = (String) s;
            log.info("param==={}", param);
            //兼容JSON

            inParamsMap = HttpUtil.decodeParamMap(param, CharsetUtil.CHARSET_UTF_8);

            log.info("map===>{}", inParamsMap);
            //param = aes.decryptStr(inParamsMap.toString());
            String req = decryptAes(param, aes);
            //返回修改后报文字符
            log.info("req===>{}", req);
            return Mono.just(req);
        };
    }

    //解密
    public String decryptAes(String content, AES aes) {
        //解密
        byte[] decrypt = aes.decrypt(content);
        //解密字符串
        return new String(decrypt);
    }


    @Override
    public int getOrder() {
        return -3;
    }
}

