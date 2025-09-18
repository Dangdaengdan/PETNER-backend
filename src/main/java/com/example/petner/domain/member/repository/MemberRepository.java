package com.example.petner.domain.member.repository;

import com.example.petner.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
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
}