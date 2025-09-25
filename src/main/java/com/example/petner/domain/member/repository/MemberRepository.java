package com.example.petner.domain.member.repository;

import com.example.petner.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoId(String kakaoId);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    boolean existsByKakaoId(String kakaoId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    /**
     * N+1 문제 해결을 위한 fetch join 조회
     */
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.location WHERE m.memberId = :memberId")
    Optional<Member> findByIdWithLocation(@Param("memberId") Long memberId);

    /**
     * 중복 확인 최적화 - 자신 제외한 이메일 중복 확인
     */
    boolean existsByEmailAndMemberIdNot(String email, Long memberId);

    /**
     * 중복 확인 최적화 - 자신 제외한 닉네임 중복 확인
     */
    boolean existsByNicknameAndMemberIdNot(String nickname, Long memberId);
}