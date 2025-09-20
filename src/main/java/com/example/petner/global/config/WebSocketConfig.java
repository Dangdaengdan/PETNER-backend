package com.example.petner.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket STOMP 설정
 * 실시간 채팅을 위한 WebSocket 연결 및 메시지 브로커 설정
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * STOMP 엔드포인트 등록
     * 클라이언트가 WebSocket 연결을 위해 사용할 엔드포인트를 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOrigins("*")  // CORS 설정 (개발 환경용)
                .withSockJS();  // SockJS 지원 (WebSocket을 지원하지 않는 브라우저 대응)
    }

    /**
     * 메시지 브로커 설정
     * 클라이언트 간 메시지 전달을 위한 브로커 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트로 메시지를 전달할 때 사용할 prefix
        config.enableSimpleBroker("/topic", "/queue");

        // 클라이언트에서 서버로 메시지를 전송할 때 사용할 prefix
        config.setApplicationDestinationPrefixes("/app");
    }
}