package com.example.petner.domain.chat.repository;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 멤버 Repository
 *
 * 채팅방 참여자 관리를 위한 데이터 액세스 계층
 * N+1 문제 방지를 위한 최적화된 쿼리 제공
 */
@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    /**
     * 채팅방과 멤버로 채팅방 멤버 정보 조회
     * @param chatRoom 채팅방
     * @param member 멤버
     * @return 채팅방 멤버 정보
     */
    Optional<ChatRoomMember> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    /**
     * 채팅방의 활성 멤버 목록 조회
     * @param chatRoom 채팅방
     * @return 활성 멤버 목록
     */
    @Query("SELECT crm FROM ChatRoomMember crm " +
           "JOIN FETCH crm.member " +
           "WHERE crm.chatRoom = :chatRoom AND crm.isActive = true")
    List<ChatRoomMember> findActiveMembersByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    /**
     * 멤버가 참여한 활성 채팅방 목록 조회 (N+1 문제 방지)
     * @param memberId 멤버 ID
     * @return 활성 채팅방 목록
     */
    @Query("SELECT crm FROM ChatRoomMember crm " +
           "JOIN FETCH crm.chatRoom cr " +
           "LEFT JOIN FETCH cr.dog " +
           "WHERE crm.member.memberId = :memberId AND crm.isActive = true " +
           "ORDER BY cr.updatedAt DESC")
    List<ChatRoomMember> findActiveChatRoomsByMemberId(@Param("memberId") Long memberId);

    /**
     * 채팅방에서 특정 멤버가 활성 상태인지 확인
     * @param chatRoomId 채팅방 ID
     * @param memberId 멤버 ID
     * @return 활성 상태 여부
     */
    @Query("SELECT CASE WHEN COUNT(crm) > 0 THEN true ELSE false END " +
           "FROM ChatRoomMember crm " +
           "WHERE crm.chatRoom.chatRoomId = :chatRoomId " +
           "AND crm.member.memberId = :memberId " +
           "AND crm.isActive = true")
    boolean existsActiveMemberInChatRoom(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    /**
     * 두 멤버가 함께 참여한 채팅방의 활성 멤버 정보 조회
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 공통 참여 채팅방의 멤버 정보 목록
     */
    @Query("SELECT crm1 FROM ChatRoomMember crm1 " +
           "WHERE crm1.member.memberId = :member1Id AND crm1.isActive = true " +
           "AND EXISTS (" +
           "    SELECT crm2 FROM ChatRoomMember crm2 " +
           "    WHERE crm2.member.memberId = :member2Id " +
           "    AND crm2.chatRoom = crm1.chatRoom " +
           "    AND crm2.isActive = true" +
           ")")
    List<ChatRoomMember> findCommonActiveChatRooms(@Param("member1Id") Long member1Id, @Param("member2Id") Long member2Id);

    /**
     * 특정 채팅방의 활성 멤버 수 조회
     * @param chatRoomId 채팅방 ID
     * @return 활성 멤버 수
     */
    @Query("SELECT COUNT(crm) FROM ChatRoomMember crm " +
           "WHERE crm.chatRoom.chatRoomId = :chatRoomId AND crm.isActive = true")
    long countActiveMembersByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}