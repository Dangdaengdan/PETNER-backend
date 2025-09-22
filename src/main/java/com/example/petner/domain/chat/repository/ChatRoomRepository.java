package com.example.petner.domain.chat.repository;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.dog.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 Repository
 *
 * 변경사항:
 * - member_id1, member_id2 관련 쿼리 제거
 * - ChatRoomMember를 통한 관계 관리로 변경
 * - N+1 문제 방지를 위한 최적화된 쿼리 제공
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 강아지와 관련된 채팅방 목록 조회
     * @param dog 강아지 엔티티
     * @return 해당 강아지와 관련된 채팅방 목록
     */
    List<ChatRoom> findByDog(Dog dog);

    /**
     * 특정 강아지와 관련된 채팅방 중에서 두 멤버가 참여한 채팅방 조회
     * ChatRoomMember를 통해 멤버 관계를 확인
     * @param dogId 강아지 ID
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 조건에 맞는 채팅방
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "JOIN cr.chatRoomMembers crm1 " +
           "JOIN cr.chatRoomMembers crm2 " +
           "WHERE cr.dog.dogId = :dogId " +
           "AND crm1.member.memberId = :member1Id AND crm1.isActive = true " +
           "AND crm2.member.memberId = :member2Id AND crm2.isActive = true " +
           "AND crm1.id != crm2.id")
    Optional<ChatRoom> findByDogAndTwoActiveMembers(@Param("dogId") Long dogId,
                                                    @Param("member1Id") Long member1Id,
                                                    @Param("member2Id") Long member2Id);

    /**
     * 강아지 정보가 없는 채팅방 중에서 두 멤버가 참여한 채팅방 조회
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 조건에 맞는 채팅방
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "JOIN cr.chatRoomMembers crm1 " +
           "JOIN cr.chatRoomMembers crm2 " +
           "WHERE cr.dog IS NULL " +
           "AND crm1.member.memberId = :member1Id AND crm1.isActive = true " +
           "AND crm2.member.memberId = :member2Id AND crm2.isActive = true " +
           "AND crm1.id != crm2.id")
    Optional<ChatRoom> findByTwoActiveMembersAndNullDog(@Param("member1Id") Long member1Id,
                                                        @Param("member2Id") Long member2Id);

    /**
     * 두 멤버가 공통으로 참여한 모든 활성 채팅방 조회 (강아지 정보 포함)
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 공통 참여 채팅방 목록
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "LEFT JOIN FETCH cr.dog " +
           "JOIN cr.chatRoomMembers crm1 " +
           "JOIN cr.chatRoomMembers crm2 " +
           "WHERE crm1.member.memberId = :member1Id AND crm1.isActive = true " +
           "AND crm2.member.memberId = :member2Id AND crm2.isActive = true " +
           "AND crm1.id != crm2.id " +
           "ORDER BY cr.updatedAt DESC")
    List<ChatRoom> findCommonActiveChatRooms(@Param("member1Id") Long member1Id,
                                             @Param("member2Id") Long member2Id);

    /**
     * 채팅방 상세 정보 조회 (강아지 정보와 활성 멤버 정보 포함)
     * N+1 문제 방지를 위한 fetch join 사용
     * @param chatRoomId 채팅방 ID
     * @return 상세 정보가 포함된 채팅방
     */
    @Query("SELECT cr FROM ChatRoom cr " +
           "LEFT JOIN FETCH cr.dog " +
           "LEFT JOIN FETCH cr.chatRoomMembers crm " +
           "LEFT JOIN FETCH crm.member " +
           "WHERE cr.chatRoomId = :chatRoomId")
    Optional<ChatRoom> findByIdWithDetails(@Param("chatRoomId") Long chatRoomId);

    /**
     * 특정 멤버가 참여한 채팅방 목록 조회 (강아지 정보 포함, N+1 방지)
     * @param memberId 멤버 ID
     * @return 해당 멤버가 참여한 채팅방 목록
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "LEFT JOIN FETCH cr.dog " +
           "JOIN cr.chatRoomMembers crm " +
           "WHERE crm.member.memberId = :memberId AND crm.isActive = true " +
           "ORDER BY cr.updatedAt DESC")
    List<ChatRoom> findMemberActiveChatRoomsWithDog(@Param("memberId") Long memberId);
}