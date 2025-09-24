package com.example.petner.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * REST API 전용 채팅 메시지 요청 DTO
 *
 * 책임:
 * - REST API /api/v1/chat/rooms/{chatRoomId}/messages 요청 데이터 캡슐화
 * - 세션 인증을 통한 발신자 정보 자동 주입 지원
 * - 클라이언트 요청 데이터의 유효성 검증
 *
 * ERD Messages 테이블 매핑:
 * - content → Messages.content
 * - senderId는 세션에서 자동으로 주입됨
 *
 * @author VIBE CODING Team
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRestRequestDto {

    /**
     * 메시지 내용
     * ERD Messages 테이블의 content 컬럼 매핑
     */
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}