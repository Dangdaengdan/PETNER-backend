package com.example.petner.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

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
        // SockJS 지원 엔드포인트 (개발 편의상 모든 Origin 허용)
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")  // allowedOrigins 대신 allowedOriginPatterns 사용
                .addInterceptors(new HttpSessionHandshakeInterceptor()) // HTTP 세션을 WebSocket으로 전달
                .withSockJS();  // SockJS 지원 (WebSocket을 지원하지 않는 브라우저 대응)

        // WebSocket 네이티브 연결을 위한 엔드포인트
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")  // 개발 편의상 모든 Origin 허용
                .addInterceptors(new HttpSessionHandshakeInterceptor()); // HTTP 세션을 WebSocket으로 전달
    }

    /**
     * HTTP 세션을 WebSocket 세션으로 전달하는 핸드셰이크 인터셉터
     */
    private static class HttpSessionHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                HttpSession session = servletRequest.getServletRequest().getSession(false);
                if (session != null) {
                    // HTTP 세션의 sessionUser를 WebSocket 세션으로 복사
                    Object sessionUser = session.getAttribute("sessionUser");
                    if (sessionUser != null) {
                        attributes.put("sessionUser", sessionUser);
                    }
                }
            }
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
        }
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