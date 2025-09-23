package com.example.petner.domain.chat.dto.response;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 정보 응답을 위한 DTO 클래스
 *
 * 서버에서 클라이언트로 채팅방 정보를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 채팅방 생성 API의 응답이나 특정 채팅방 조회 시 사용됩니다.
 *
 * 변경사항:
 * - member1, member2 대신 ChatRoomMember를 통한 멤버 정보 관리
 * - 활성 멤버만 포함하여 응답
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
     * 활성 멤버 정보 목록
     * 채팅방에 참여 중인 멤버들의 기본 정보
     */
    private List<MemberInfo> activeMembers;

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

        // 활성 멤버 정보 설정
        this.activeMembers = chatRoom.getActiveMembers().stream()
                .map(chatRoomMember -> new MemberInfo(chatRoomMember.getMember()))
                .toList();
    }

    /**
     * 멤버 기본 정보를 담는 내부 클래스
     */
    @Getter
    public static class MemberInfo {
        private final Long memberId;
        private final String nickname;

        public MemberInfo(Member member) {
            this.memberId = member.getMemberId();
            this.nickname = member.getNickname();
        }
    }
}