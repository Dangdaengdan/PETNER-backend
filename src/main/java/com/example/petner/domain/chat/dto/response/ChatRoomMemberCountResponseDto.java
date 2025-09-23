package com.example.petner.domain.chat.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 활성 멤버 수 응답을 위한 DTO 클래스
 *
 * 서버에서 클라이언트로 채팅방의 활성 멤버 수 정보를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 채팅방 통계 정보 조회 API의 응답으로 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMemberCountResponseDto {

    /**
     * 채팅방 고유 ID
     */
    private Long chatRoomId;

    /**
     * 활성 멤버 수
     */
    private Long activeMemberCount;

    /**
     * 활성 멤버 수 응답 DTO 생성자
     *
     * @param chatRoomId 채팅방 고유 ID
     * @param activeMemberCount 활성 멤버 수
     */
    public ChatRoomMemberCountResponseDto(Long chatRoomId, Long activeMemberCount) {
        this.chatRoomId = chatRoomId;
        this.activeMemberCount = activeMemberCount;
    }
}