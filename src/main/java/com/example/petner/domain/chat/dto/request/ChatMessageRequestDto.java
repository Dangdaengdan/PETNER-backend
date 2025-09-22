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
 * - 클라이언트 요청 데이터의 유효성 검증 (Single Responsibility Principle)
 * - 불변성 보장을 통한 데이터 무결성 유지
 *
 * ERD Messages 테이블 매핑:
 * - senderId → Messages.senderId (FK from Members)
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
     * ERD Members 테이블의 memberId 참조
     */
    @NotNull(message = "발신자 ID는 필수입니다")
    private Long senderId;

    /**
     * 메시지 내용
     * ERD Messages 테이블의 content 컬럼 매핑
     */
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}