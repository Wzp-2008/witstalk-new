package top.xinsin.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.xinsin.config.IgnoreUrlsConfig;
import top.xinsin.constants.CacheConstants;
import top.xinsin.constants.TokenConstants;
import top.xinsin.util.JwtUtil;
import top.xinsin.util.RSAComponent;
import top.xinsin.util.Result;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    private final IgnoreUrlsConfig ignoreWhite;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;
    private final RSAComponent rsaComponent;

    public AuthFilter(IgnoreUrlsConfig ignoreWhite, RedisTemplate<String, Object> redisTemplate, JwtUtil jwtUtil, RSAComponent rsaComponent) {
        this.ignoreWhite = ignoreWhite;
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
        this.rsaComponent = rsaComponent;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (log.isDebugEnabled()) {
            log.debug("authFilter.request => {}", JSONObject.toJSONString(exchange.getRequest()));
        }
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        String url = request.getURI().getPath();

        // 跳过不需要验证的路径
        if (ignoreWhite.getUrls().contains(url)) {
            if ("/auth/logout".equals(url)) {
                // 登出操作处理token
                String token = getToken(request);
                if (StringUtils.isNotEmpty(token)) {
                    redisTemplate.delete(getTokenKey(token));
                    log.debug("登出成功，移除token缓存: {}", token);
                }
            }
            return chain.filter(exchange);
        }
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }

//        boolean isVerify = jwtUtil.validateToken(token);
//        if (!isVerify) {
//            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
//        }
//        String username = jwtUtil.getFromJWT(token).getSubject();
//
//        boolean islogin = redisTemplate.hasKey(getTokenKey(username));
//        if (!islogin) {
//            return unauthorizedResponse(exchange, "登录状态已过期");
//        }

        HttpMethod method = request.getMethod();
        if (!HttpMethod.POST.equals(method) && !HttpMethod.PUT.equals(method)) {
            return chain.filter(exchange.mutate().request(mutate.build()).build());
        }

        // 处理POST/PUT请求的请求体
        return DataBufferUtils.join(request.getBody())
                .flatMap(dataBuffer -> {
                    // 保留原始数据的副本，因为DataBuffer只能被消费一次
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer); // 释放缓冲区

                    String requestBody = new String(bytes, StandardCharsets.UTF_8);

                    // 处理请求体中的data数据
                    String processedBody = processRequestBody(requestBody, request);

                    // 创建新的DataBuffer包含处理后的数据
                    DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                    DataBuffer processedBuffer = bufferFactory.wrap(processedBody.getBytes(StandardCharsets.UTF_8));

                    // 创建装饰器，返回处理后的请求体
                    ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(processedBuffer);
                        }
                    };

                    // 继续过滤器链
                    return chain.filter(exchange.mutate().request(decoratedRequest).build());
                });
    }

    @Override
    public int getOrder() {
        return -200;
    }

    @SneakyThrows
    private String processRequestBody(String requestBody, ServerHttpRequest request) {
        requestBody = requestBody.replaceAll("\"", "");
        String base64Content = new String(Base64.getDecoder().decode(requestBody), StandardCharsets.UTF_8);
        String encodeKeyIV = base64Content.substring(base64Content.length() - 344);
        try {
            String decrypt = rsaComponent.decrypt(encodeKeyIV);
        }catch (Exception e) {
            throw new RuntimeException("请求数据解密失败");
        }
        return requestBody;
    }

    /**
     * 获取缓存key
     */
    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{}, 异常信息:{}", exchange.getRequest().getPath(), msg);

        byte[] responseBytes = JSON.toJSONString(Result.fail(HttpStatus.UNAUTHORIZED.value(), msg)).getBytes(StandardCharsets.UTF_8);
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(responseBytes);

        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    /**
     * 获取请求token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        boolean hasPrefix = false;
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, StringUtils.EMPTY);
            hasPrefix = true;
        }
        return token;
    }
}
