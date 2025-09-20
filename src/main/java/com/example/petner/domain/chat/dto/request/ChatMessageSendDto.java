package com.example.petner.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 클라이언트에서 서버로 메시지 전송 시 사용하는 DTO
 * WebSocket /app/chat/{chatRoomId}로 전송되는 메시지 형식
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSendDto {

    @NotNull(message = "채팅방 ID는 필수입니다")
    private Long chatRoomId;

    @NotNull(message = "발신자 ID는 필수입니다")
    private Long senderId;

    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}