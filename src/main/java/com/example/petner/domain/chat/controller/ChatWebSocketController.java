package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatMessageSendDto;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
    private final ChatMessageService chatMessageService;

    /**
     * 클라이언트로부터 메시지 수신 및 처리
     * /app/chat/{chatRoomId}로 전송된 메시지를 받아서 저장하고 브로드캐스트
     *
     * @param chatRoomId 채팅방 ID
     * @param messageDto 클라이언트가 전송한 메시지 DTO
     */
    @MessageMapping("/chat/{chatRoomId}")
    public void handleChatMessage(@DestinationVariable Long chatRoomId, @Payload ChatMessageSendDto messageDto) {
        try {
            log.info("Received message for chat room {}: {}", chatRoomId, messageDto.getContent());

            // 메시지 저장 및 브로드캐스트
            chatMessageService.saveAndBroadcastMessage(messageDto);

        } catch (Exception e) {
            log.error("Error handling chat message for room {}: {}", chatRoomId, e.getMessage());
        }
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