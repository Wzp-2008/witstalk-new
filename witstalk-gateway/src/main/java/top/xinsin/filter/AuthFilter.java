package top.xinsin.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.xinsin.config.IgnoreUrlsConfig;
import top.xinsin.constants.CacheConstants;
import top.xinsin.constants.TokenConstants;
import top.xinsin.util.JwtUtil;
import top.xinsin.util.Result;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    private final IgnoreUrlsConfig ignoreWhite;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    public AuthFilter(IgnoreUrlsConfig ignoreWhite, RedisTemplate<String, Object> redisTemplate, JwtUtil jwtUtil) {
        this.ignoreWhite = ignoreWhite;
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
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

        boolean isVerify = jwtUtil.validateToken(token);
        if (!isVerify) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }
        String username = jwtUtil.getFromJWT(token).getSubject();

        boolean islogin = redisTemplate.hasKey(getTokenKey(username));
        if (!islogin) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }
//        String userid = JwtUtils.getUserId(claims);
//        String username = JwtUtils.getUserName(claims);
//        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(username)) {
//            return unauthorizedResponse(exchange, "令牌验证失败");
//        }
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    @Override
    public int getOrder() {
        return -200;
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
