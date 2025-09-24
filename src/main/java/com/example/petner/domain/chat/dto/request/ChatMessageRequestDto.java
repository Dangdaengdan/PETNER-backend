package com.example.petner.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 실시간 채팅 메시지 요청 DTO
 *
 * 책임:
 * - WebSocket /app/chat/{chatRoomId}로 전송되는 메시지 데이터 캡슐화
 * - REST API /api/v1/chat/rooms/{chatRoomId}/messages 요청 데이터 캡슐화
 * - 클라이언트 요청 데이터의 유효성 검증 (Single Responsibility Principle)
 * - 불변성 보장을 통한 데이터 무결성 유지
 *
 * ERD Messages 테이블 매핑:
 * - senderId → Messages.senderId (WebSocket에서 사용, REST API에서는 세션에서 주입)
 * - content → Messages.content
 *
 * @author VIBE CODING Team
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {

    /**
     * 메시지 발신자 ID
     * - WebSocket: 클라이언트에서 필수로 전송
     * - REST API: 사용하지 않음 (세션에서 자동 주입)
     */
    private Long senderId;

    /**
     * 메시지 내용
     * ERD Messages 테이블의 content 컬럼 매핑
     */
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;

    /**
     * REST API 전용 생성자 (senderId 없이)
     * @param content 메시지 내용
     */
    public ChatMessageRequestDto(String content) {
        this.content = content;
    }
}