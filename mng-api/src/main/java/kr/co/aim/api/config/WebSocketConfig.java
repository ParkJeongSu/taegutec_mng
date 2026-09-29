package kr.co.aim.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile({"web"})
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 브라우저 표준 Native WebSocket 전용 (ws:// 또는 wss:// 직결용)
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*");

        // 2. SockJS 레거시 호환용 (기존 SockJS 클라이언트도 지원해야 할 경우 대비)
//        registry.addEndpoint("/ws-stomp")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
