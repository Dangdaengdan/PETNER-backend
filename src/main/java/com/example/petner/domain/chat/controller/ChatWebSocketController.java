package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 실시간 채팅을 위한 WebSocket 컨트롤러
 * 채팅방별 메시지 구독 및 전송 처리
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 특정 채팅방에 메시지 브로드캐스트
     *
     * @param chatRoomId 채팅방 ID
     * @param message 전송할 메시지
     */
    public void sendMessageToChatRoom(Long chatRoomId, ChatMessageResponseDto message) {
        String destination = "/topic/chat/" + chatRoomId;
        log.info("Broadcasting message to {}: {}", destination, message.getContent());
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * 채팅방 입장 알림 (선택적 구현)
     *
     * @param chatRoomId 채팅방 ID
     */
    @MessageMapping("/chat/{chatRoomId}/join")
    public void handleUserJoin(@DestinationVariable Long chatRoomId) {
        log.info("User joined chat room: {}", chatRoomId);
        // 필요시 입장 알림 로직 추가
    }

    /**
     * 채팅방 퇴장 알림 (선택적 구현)
     *
     * @param chatRoomId 채팅방 ID
     */
    @MessageMapping("/chat/{chatRoomId}/leave")
    public void handleUserLeave(@DestinationVariable Long chatRoomId) {
        log.info("User left chat room: {}", chatRoomId);
        // 필요시 퇴장 알림 로직 추가
    }
}