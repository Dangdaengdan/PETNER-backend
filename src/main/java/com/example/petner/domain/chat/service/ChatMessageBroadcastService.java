package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.controller.ChatWebSocketController;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 채팅 메시지 브로드캐스팅 서비스
 * Single Responsibility Principle을 적용하여 실시간 메시지 전송 책임만 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageBroadcastService {

    private final ChatWebSocketController chatWebSocketController;

    /**
     * 새로운 메시지를 해당 채팅방의 모든 구독자에게 실시간 전송
     *
     * @param message 전송할 메시지 엔티티
     */
    public void broadcastNewMessage(Message message) {
        try {
            ChatMessageResponseDto responseDto = new ChatMessageResponseDto(message);
            Long chatRoomId = message.getChatRoom().getChatRoomId();

            log.info("Broadcasting message {} to chat room {}", message.getMessageId(), chatRoomId);
            chatWebSocketController.sendMessageToChatRoom(chatRoomId, responseDto);

        } catch (Exception e) {
            log.error("Failed to broadcast message {}: {}", message.getMessageId(), e.getMessage());
        }
    }

    /**
     * 메시지 DTO를 직접 브로드캐스트 (테스트 또는 특수 용도)
     *
     * @param chatRoomId 채팅방 ID
     * @param messageDto 전송할 메시지 DTO
     */
    public void broadcastMessage(Long chatRoomId, ChatMessageResponseDto messageDto) {
        try {
            log.info("Broadcasting custom message to chat room {}", chatRoomId);
            chatWebSocketController.sendMessageToChatRoom(chatRoomId, messageDto);

        } catch (Exception e) {
            log.error("Failed to broadcast custom message to chat room {}: {}", chatRoomId, e.getMessage());
        }
    }
}