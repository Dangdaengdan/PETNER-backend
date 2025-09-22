package com.example.petner.domain.chat.dto.response;

import com.example.petner.domain.chat.entity.ChatRoom;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방 정보 응답을 위한 DTO 클래스
 *
 * 서버에서 클라이언트로 채팅방 정보를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 채팅방 생성 API의 응답이나 특정 채팅방 조회 시 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponseDto {

    /**
     * 채팅방 고유 ID
     */
    private Long chatRoomId;

    /**
     * 채팅방 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 연관된 강아지 ID (선택적)
     * 강아지와 관련된 채팅방이 아닌 경우 null
     */
    private Long dogId;

    /**
     * 연관된 강아지 이름 (선택적)
     * 강아지와 관련된 채팅방이 아닌 경우 null
     */
    private String dogName;

    /**
     * 첫 번째 멤버 ID
     */
    private Long member1Id;

    /**
     * 첫 번째 멤버 닉네임
     */
    private String member1Nickname;

    /**
     * 두 번째 멤버 ID
     */
    private Long member2Id;

    /**
     * 두 번째 멤버 닉네임
     */
    private String member2Nickname;

    /**
     * ChatRoom 엔티티로부터 DTO를 생성하는 생성자
     *
     * @param chatRoom 변환할 ChatRoom 엔티티
     */
    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getChatRoomId();
        this.createdAt = chatRoom.getCreatedAt();

        // 강아지 정보는 선택적이므로 null 체크 후 설정
        this.dogId = chatRoom.getDog() != null ? chatRoom.getDog().getDogId() : null;
        this.dogName = chatRoom.getDog() != null ? chatRoom.getDog().getName() : null;

        // 멤버 정보 설정
        this.member1Id = chatRoom.getMember1().getMemberId();
        this.member1Nickname = chatRoom.getMember1().getNickname();
        this.member2Id = chatRoom.getMember2().getMemberId();
        this.member2Nickname = chatRoom.getMember2().getNickname();
    }
}