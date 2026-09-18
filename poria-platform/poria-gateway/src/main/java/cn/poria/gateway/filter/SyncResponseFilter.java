package cn.poria.gateway.filter;


import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;


@Slf4j
@Component
public class SyncResponseFilter implements GlobalFilter, Ordered{

	@Value("${sync-encrypt-key}")
	private  String key;



	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.debug("key===={}",key);

		String isEncrypt = exchange.getRequest().getHeaders().getFirst("isEncrypt");
		if("true".equals(isEncrypt)){

			return chain.filter(exchange.mutate().response(decorate(exchange)).build());
		}else {

			return chain.filter(exchange);
		}
	}

	private ServerHttpResponse decorate(ServerWebExchange exchange) {
		return new ServerHttpResponseDecorator(exchange.getResponse()){
			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				log.info("body----{}",body);
				String originalResponseContentType = exchange
						.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add(HttpHeaders.CONTENT_TYPE,
						originalResponseContentType);
				ClientResponse clientResponse = ClientResponse
						.create(exchange.getResponse().getStatusCode())
						.headers(headers -> headers.putAll(httpHeaders))
						.body(Flux.from(body)).build();
				//修改body
				Mono<String> modifiedBody = clientResponse.bodyToMono(String.class)
						.flatMap(originalBody -> {
							try {
								return modifyBody()
										.apply(exchange,Mono.just(originalBody));
							} catch (Exception e) {
								e.printStackTrace();
							}
							return null;
						});
				BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody,
						String.class);
				CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(
						exchange, exchange.getResponse().getHeaders());
				return bodyInserter.insert(outputMessage, new BodyInserterContext())
						.then(Mono.defer(() -> {
							Flux<DataBuffer> messageBody = outputMessage.getBody();
							HttpHeaders headers = getDelegate().getHeaders();
							if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
								messageBody = messageBody.doOnNext(data -> headers
										.setContentLength(data.readableByteCount()));
							}
							return getDelegate().writeWith(messageBody);
						}));
			}
			/**
			 * 修改body
			 * @return apply 返回Mono<String>，数据是修改后的body
			 */
			private BiFunction<ServerWebExchange,Mono<String>,Mono<String>> modifyBody(){
				return (exchange,json)-> {
					AtomicReference<String> result = new AtomicReference<>();
					json.subscribe(
							value -> result.set(encryptAes(value,new AES(key.getBytes()))),
							Throwable::printStackTrace
					);
					return Mono.just(result.get());
				};
			}
			@Override
			public Mono<Void> writeAndFlushWith(
					Publisher<? extends Publisher<? extends DataBuffer>> body) {
				return writeWith(Flux.from(body).flatMapSequential(p -> p));
			}

		};

	}


	@Override
	public int getOrder() {
		return -2;
	}

	//加密
	public  String encryptAes(String content, AES aes) {
		return aes.encryptBase64(content);
	}
}
