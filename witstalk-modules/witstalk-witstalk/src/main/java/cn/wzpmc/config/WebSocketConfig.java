package cn.wzpmc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import cn.wzpmc.handler.SignalingHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册信令处理器，允许跨域
        registry.addHandler(signalingHandler(), "/signaling")
                .setAllowedOrigins("*");
    }

    @Bean
    public SignalingHandler signalingHandler() {
        return new SignalingHandler();
    }
}
