package com.example.petner.domain.chat.dto.response;

import com.example.petner.domain.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 실시간 채팅 메시지 응답 DTO
 * WebSocket을 통해 클라이언트에게 전송되는 메시지 형식
 */
@Getter
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String content;
    private LocalDateTime sentAt;

    /**
     * Message 엔티티로부터 DTO 생성
     */
    public ChatMessageResponseDto(Message message) {
        this.messageId = message.getMessageId();
        this.chatRoomId = message.getChatRoom().getChatRoomId();
        this.senderId = message.getSender().getMemberId();
        this.senderNickname = message.getSender().getNickname();
        this.content = message.getContent();
        this.sentAt = message.getSentAt();
    }
}