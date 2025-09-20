package com.example.petner.domain.chat.repository;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.member1 = :member1 AND cr.member2 = :member2) OR (cr.member1 = :member2 AND cr.member2 = :member1)")
    Optional<ChatRoom> findByTwoMembers(@Param("member1") Member member1, @Param("member2") Member member2);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.member1 = :member OR cr.member2 = :member ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByMemberOrderByCreatedAtDesc(@Param("member") Member member);

    List<ChatRoom> findByDog(Dog dog);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.dog = :dog AND ((cr.member1 = :member1 AND cr.member2 = :member2) OR (cr.member1 = :member2 AND cr.member2 = :member1))")
    Optional<ChatRoom> findByDogAndTwoMembers(@Param("dog") Dog dog, @Param("member1") Member member1, @Param("member2") Member member2);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.dog IS NULL AND ((cr.member1 = :member1 AND cr.member2 = :member2) OR (cr.member1 = :member2 AND cr.member2 = :member1))")
    Optional<ChatRoom> findByTwoMembersAndNullDog(@Param("member1") Member member1, @Param("member2") Member member2);
}