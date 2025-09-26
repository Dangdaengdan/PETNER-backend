package com.example.petner.domain.favorite.service;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.repository.DogRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import com.example.petner.global.exception.customException.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 즐겨찾기 검증 서비스
 *
 * Single Responsibility Principle을 적용하여 검증 책임만 담당
 * 멤버와 강아지의 존재 여부 및 유효성을 검증합니다.
 */
@Service
@RequiredArgsConstructor
public class FavoriteValidator {

    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;

    /**
     * 멤버 존재 여부 검증 및 반환
     *
     * @param memberId 검증할 멤버 ID
     * @return 검증된 Member 엔티티
     * @throws MemberException 멤버가 존재하지 않을 경우
     */
    public Member validateAndGetMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 강아지 존재 여부 검증 및 반환
     *
     * @param dogId 검증할 강아지 ID
     * @return 검증된 Dog 엔티티
     * @throws DogException 강아지가 존재하지 않을 경우
     */
    public Dog validateAndGetDog(Long dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_NOT_FOUND));
    }
}