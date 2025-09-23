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
@Table(name = "chat_room_members", uniqueConstraints = {
        @UniqueConstraint(
                name = "chatroom_member_uk",
                columnNames = {"chat_room_id", "member_id"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_member_id")
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

    /**
     * 채팅방에 (재)입장한 시간
     * 채팅방 생성 시점 또는 재입장 시점으로 설정
     * 이 시간 이전의 메시지는 보이지 않음
     */
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Builder
    public ChatRoomMember(ChatRoom chatRoom, Member member, Boolean isActive) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.isActive = isActive != null ? isActive : true;
        this.joinedAt = LocalDateTime.now(); // 생성 시점을 입장 시간으로 설정
    }

    /**
     * 채팅방 설정 (양방향 관계 설정용)
     * @param chatRoom 설정할 채팅방
     */
    public void setChatRoom(ChatRoom chatRoom) {
        // 기존 관계 해제
        if (this.chatRoom != null && this.chatRoom != chatRoom) {
            this.chatRoom.getChatRoomMembers().remove(this);
        }

        this.chatRoom = chatRoom;

        // 새로운 관계 설정 (무한 순환 방지)
        if (chatRoom != null && !chatRoom.getChatRoomMembers().contains(this)) {
            chatRoom.getChatRoomMembers().add(this);
        }
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
     * 재입장 시점을 기록하여 이전 메시지 가시성 제어
     */
    public void reactivate() {
        this.isActive = true;
        this.exitedAt = null;
        this.joinedAt = LocalDateTime.now(); // 재입장 시간 업데이트
    }

    /**
     * 활성 상태 확인
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * 입장 시간 조회
     * @return 입장 시간
     */
    public LocalDateTime getJoinedAt() {
        return this.joinedAt;
    }

    /**
     * 특정 시간 이후에 나간 상태인지 확인
     * @param messageTime 확인할 메시지 시간
     * @return 메시지 시간 이후에 나간 상태인지 여부
     */
    public boolean hasExitedAfter(LocalDateTime messageTime) {
        return exitedAt != null && exitedAt.isAfter(messageTime);
    }

    /**
     * 멤버에게 특정 메시지가 보여야 하는지 확인
     * 입장 시간 이후이고 나가기 전(또는 나간 적 없음)의 메시지만 보임
     *
     * @param messageTime 확인할 메시지 시간
     * @return 메시지 가시성 여부
     */
    public boolean canSeeMessage(LocalDateTime messageTime) {
        // 입장 시간 이전의 메시지는 보이지 않음
        if (joinedAt != null && messageTime.isBefore(joinedAt)) {
            return false;
        }

        // 나간 시간 이후의 메시지는 보이지 않음
        if (exitedAt != null && messageTime.isAfter(exitedAt)) {
            return false;
        }

        return true;
    }
}