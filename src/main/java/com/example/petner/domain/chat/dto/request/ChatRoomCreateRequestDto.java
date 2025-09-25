package com.example.petner.domain.chat.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 생성 요청을 위한 DTO 클래스
 *
 * 세션 인증을 통해 현재 로그인한 사용자와 상대방 사용자 간의 채팅방을 생성합니다.
 * 선택적으로 특정 강아지와 관련된 채팅방을 만들 수 있습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateRequestDto {

    /**
     * 강아지 ID (선택적)
     * 입양 상담 등 특정 강아지와 관련된 채팅방인 경우 해당 강아지의 ID
     * null일 경우 일반 채팅방으로 생성됨
     */
    private Long dogId;

    /**
     * 상대방 멤버 ID (필수)
     * 채팅방에 참여할 상대방 사용자의 ID
     * 현재 로그인한 사용자는 세션에서 자동으로 추출됨
     */
    private Long otherMemberId;

    /**
     * 테스트 또는 직접 객체 생성을 위한 생성자
     *
     * @param dogId 강아지 ID (선택적)
     * @param otherMemberId 상대방 멤버 ID
     */
    public ChatRoomCreateRequestDto(Long dogId, Long otherMemberId) {
        this.dogId = dogId;
        this.otherMemberId = otherMemberId;
    }
}