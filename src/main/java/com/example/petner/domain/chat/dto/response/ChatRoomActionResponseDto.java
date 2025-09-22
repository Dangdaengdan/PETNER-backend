package com.example.petner.domain.chat.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 액션 성공 응답을 위한 DTO 클래스
 *
 * 서버에서 클라이언트로 채팅방 관련 액션(나가기, 재입장 등)의 성공 메시지를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 채팅방 나가기, 재입장 API의 응답으로 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomActionResponseDto {

    /**
     * 채팅방 고유 ID
     */
    private Long chatRoomId;

    /**
     * 멤버 고유 ID
     */
    private Long memberId;

    /**
     * 성공 메시지
     */
    private String message;

    /**
     * 액션 성공 상태
     */
    private boolean success;

    /**
     * 채팅방 액션 성공 응답 DTO 생성자
     *
     * @param chatRoomId 채팅방 고유 ID
     * @param memberId 멤버 고유 ID
     * @param message 성공 메시지
     */
    public ChatRoomActionResponseDto(Long chatRoomId, Long memberId, String message) {
        this.chatRoomId = chatRoomId;
        this.memberId = memberId;
        this.message = message;
        this.success = true;
    }
}