package com.example.petner.domain.chat.entity;

import com.example.petner.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방 멤버 엔티티
 *
 * 채팅방과 멤버 간의 다대다 관계를 관리하는 중간 엔티티
 * 멤버별 채팅방 참여 상태 및 나간 시간을 추적
 *
 * DB 테이블: chat_room_members
 * 복합 유니크 키: (chat_room_id, member_id)
 */
@Entity
@Table(name = "chat_room_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * 채팅방 목록 노출 여부
     * true: 채팅방 목록에 표시
     * false: 채팅방을 나간 상태로 목록에서 숨김
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 채팅방을 나간 시간
     * 채팅방을 나갔을 때 설정되며, 이 시간 이후의 메시지는 보이지 않음
     */
    @Column(name = "exited_at")
    private LocalDateTime exitedAt;

    @Builder
    public ChatRoomMember(ChatRoom chatRoom, Member member, Boolean isActive) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.isActive = isActive != null ? isActive : true;
    }

    /**
     * 채팅방 설정 (양방향 관계 설정용)
     * @param chatRoom 설정할 채팅방
     */
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    /**
     * 채팅방 비활성화 (나가기)
     * 실제 데이터 삭제 대신 비활성화 처리하여 데이터 무결성 유지
     */
    public void deactivate() {
        this.isActive = false;
        this.exitedAt = LocalDateTime.now();
    }

    /**
     * 채팅방 재활성화 (재입장)
     * 기존 멤버가 채팅방에 다시 참여할 때 사용
     */
    public void reactivate() {
        this.isActive = true;
        this.exitedAt = null;
    }

    /**
     * 활성 상태 확인
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * 특정 시간 이후에 나간 상태인지 확인
     * @param messageTime 확인할 메시지 시간
     * @return 메시지 시간 이후에 나간 상태인지 여부
     */
    public boolean hasExitedAfter(LocalDateTime messageTime) {
        return exitedAt != null && exitedAt.isAfter(messageTime);
    }
}