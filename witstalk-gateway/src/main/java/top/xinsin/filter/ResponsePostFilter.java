package top.xinsin.filter;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.xinsin.util.AESComponent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class ResponsePostFilter implements GlobalFilter, Ordered {

    private final AESComponent aesComponent;

    public ResponsePostFilter(AESComponent aesComponent) {
        this.aesComponent = aesComponent;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取原始响应
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpRequest.Builder mutate = exchange.getRequest().mutate();
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
                        String modifiedResponse = modifyResponse(responseStr, mutate);

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
    @SneakyThrows
    private String modifyResponse(String originalResponse, ServerHttpRequest.Builder mutate) {
        try {
            JSONObject.parseObject(originalResponse, JSONObject.class);
            AtomicReference<String> key = new AtomicReference<>("");
            AtomicReference<String> iv = new AtomicReference<>("");
            mutate.headers(headers -> {
                if (headers.containsKey("aes-key")) {
                    key.set(URLDecoder.decode(Objects.requireNonNull(headers.get("aes-key")).get(0), StandardCharsets.UTF_8));
                }
                if (headers.get("aes-iv") != null) {
                    iv.set(URLDecoder.decode(Objects.requireNonNull(headers.get("aes-iv")).get(0), StandardCharsets.UTF_8));
                }
            });
            return Base64.getEncoder().encodeToString(aesComponent.encrypt(originalResponse, key.get(), iv.get()).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return originalResponse;
        }
    }

    @Override
    public int getOrder() {
        // 设置过滤器顺序，-1是NettyWriteResponseFilter之前，确保能拦截响应
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
