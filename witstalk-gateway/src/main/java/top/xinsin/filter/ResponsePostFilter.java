package top.xinsin.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ResponsePostFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取原始响应
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        // 创建响应装饰器
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux<? extends DataBuffer> fluxBody) {
                    // 转换响应体
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        // 合并所有DataBuffer
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);

                        // 释放内存
                        DataBufferUtils.release(join);

                        // 原始响应内容
                        String responseStr = new String(content, StandardCharsets.UTF_8);
                        log.info("原始响应内容: {}", responseStr);

                        // 在这里可以修改响应内容
                        String modifiedResponse = modifyResponse(responseStr);

                        // 返回修改后的响应
                        return bufferFactory.wrap(modifiedResponse.getBytes(StandardCharsets.UTF_8));
                    }));
                }
                // 如果body不是Flux，直接返回
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };

        // 替换响应并继续过滤链
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 修改响应内容的方法
     */
    private String modifyResponse(String originalResponse) {
        return originalResponse;
    }

    @Override
    public int getOrder() {
        // 设置过滤器顺序，-1是NettyWriteResponseFilter之前，确保能拦截响应
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
