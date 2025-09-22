package com.example.petner.domain.chat.entity;

import com.example.petner.domain.dog.entity.Dog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 엔티티
 *
 * 변경사항:
 * - member_id1, member_id2 컬럼 제거
 * - ChatRoomMember 엔티티와 OneToMany 관계 설정
 * - 채팅방 참여자 관리는 ChatRoomMember를 통해 처리
 */
@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    @Builder
    public ChatRoom(Dog dog) {
        this.dog = dog;
        this.chatRoomMembers = new ArrayList<>();
    }

    /**
     * 채팅방에 멤버 추가
     * @param chatRoomMember 추가할 채팅방 멤버
     */
    public void addMember(ChatRoomMember chatRoomMember) {
        chatRoomMembers.add(chatRoomMember);
        chatRoomMember.setChatRoom(this);
    }

    /**
     * 채팅방에서 멤버 제거 (실제 삭제가 아닌 비활성화)
     * @param memberId 제거할 멤버 ID
     */
    public void removeMember(Long memberId) {
        chatRoomMembers.stream()
                .filter(member -> member.getMember().getMemberId().equals(memberId))
                .findFirst()
                .ifPresent(ChatRoomMember::deactivate);
    }

    /**
     * 활성화된 채팅방 멤버 목록 조회
     * @return 활성화된 멤버 목록
     */
    public List<ChatRoomMember> getActiveMembers() {
        return chatRoomMembers.stream()
                .filter(ChatRoomMember::isActive)
                .toList();
    }

    /**
     * 마지막 메시지 시간 업데이트
     */
    public void updateLastMessageTime() {
        this.updatedAt = LocalDateTime.now();
    }
}