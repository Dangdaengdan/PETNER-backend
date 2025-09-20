package com.example.petner.domain.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 연결 테스트를 위한 컨트롤러
 * 실제 채팅 기능 구현 전 WebSocket 연결을 테스트하는 용도
 */
@Controller
@Slf4j
public class WebSocketTestController {

    /**
     * WebSocket 연결 테스트용 엔드포인트
     * 클라이언트에서 /app/test로 메시지를 보내면 /topic/test로 브로드캐스트
     */
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public String handleTestMessage(String message) {
        log.info("Received WebSocket test message: {}", message);
        return "Echo: " + message;
    }
}