package top.xinsin.handler;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import top.xinsin.util.Result;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class GatewayExceptionHandler {

    @Bean
    @Order(-1) // 确保优先级高于默认的错误处理器
    public ErrorWebExceptionHandler errorWebExceptionHandler() {
        return (exchange, ex) -> {
            ServerHttpResponse response = exchange.getResponse();

            // 获取请求路径
            String path = exchange.getRequest().getPath().toString();
            // 构建错误响应
            Result<?> errorResult;
            HttpStatus httpStatus;
            if (ex instanceof NotFoundException) {
                httpStatus = HttpStatus.NOT_FOUND;
                errorResult = Result.fail(httpStatus.value(), "服务未找到或路径不存在", ex.getMessage());
            } else if (ex instanceof ResponseStatusException responseStatusException) {
                httpStatus = (HttpStatus) responseStatusException.getStatusCode();
                errorResult = Result.fail(httpStatus.value(), responseStatusException.getReason(), ex.getMessage());
            } else {
                if (ex.getMessage() != null && ex.getMessage().contains("401")) {
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    errorResult = Result.fail(httpStatus.value(), "认证失败，请先登录", ex.getMessage());
                } else if (ex.getMessage() != null && ex.getMessage().contains("403")) {
                    httpStatus = HttpStatus.FORBIDDEN;
                    errorResult = Result.fail(httpStatus.value(), "权限不足，无法访问", ex.getMessage());
                } else if (ex instanceof java.net.ConnectException) {
                    httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
                    errorResult = Result.fail(httpStatus.value(), "服务连接失败，请稍后重试", ex.getMessage());
                } else {
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    errorResult = Result.fail(httpStatus.value(), "网关内部处理异常", ex.getMessage());
                }
            }

            // 详细日志记录
            log.error("[网关异常处理]请求路径: {}, 异常类型: {}, 异常信息: {}",
                    path, ex.getClass().getSimpleName(), ex.getMessage(), ex);

            // 设置响应信息
            response.setStatusCode(httpStatus);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.getHeaders().set("Access-Control-Allow-Origin", "*");

            // 转换响应内容
            byte[] responseBytes = JSON.toJSONString(errorResult).getBytes(StandardCharsets.UTF_8);
            DataBuffer dataBuffer = response.bufferFactory().wrap(responseBytes);

            return response.writeWith(Mono.just(dataBuffer));
        };
    }
}
