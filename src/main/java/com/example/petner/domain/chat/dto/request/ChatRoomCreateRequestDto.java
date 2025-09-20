package com.example.petner.domain.chat.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 생성 요청을 위한 DTO 클래스
 *
 * 클라이언트에서 서버로 채팅방 생성 요청을 보낼 때 사용하는 데이터 전송 객체입니다.
 * 두 멤버 간의 채팅방을 생성하며, 선택적으로 특정 강아지와 관련된 채팅방을 만들 수 있습니다.
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
     * 첫 번째 멤버 ID (필수)
     * 채팅방에 참여할 첫 번째 사용자의 ID
     */
    private Long member1Id;

    /**
     * 두 번째 멤버 ID (필수)
     * 채팅방에 참여할 두 번째 사용자의 ID
     */
    private Long member2Id;

    /**
     * 테스트 또는 직접 객체 생성을 위한 생성자
     *
     * @param dogId 강아지 ID (선택적)
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     */
    public ChatRoomCreateRequestDto(Long dogId, Long member1Id, Long member2Id) {
        this.dogId = dogId;
        this.member1Id = member1Id;
        this.member2Id = member2Id;
    }
}