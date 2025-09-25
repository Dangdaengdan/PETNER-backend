package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.breed.repository.BreedRepository;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.shelter.repository.ShelterRepository;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import com.example.petner.global.exception.customException.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 유기견 관련 검증을 담당하는 컴포넌트
 * Single Responsibility Principle(SRP)을 준수하여 검증 로직만 담당
 */
@Component
@RequiredArgsConstructor
public class DogValidator {

    private final MemberRepository memberRepository;
    private final BreedRepository breedRepository;
    private final ShelterRepository shelterRepository;

    /**
     * 사용자 존재 여부 검증
     */
    public Member validateAndGetMember(SessionUser user) {
        return memberRepository.findById(user.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 견종 존재 여부 검증
     */
    public Breed validateAndGetBreed(Long breedId) {
        return breedRepository.findById(breedId)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_BREED_NOT_FOUND));
    }

    /**
     * 보호소 존재 여부 검증 (선택적)
     */
    public Shelter validateAndGetShelter(Long shelterId) {
        if (shelterId == null) {
            return null;
        }
        return shelterRepository.findById(shelterId)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_SHELTER_NOT_FOUND));
    }

    /**
     * 유기견 수정/삭제 권한 검증
     */
    public void validateDogAccess(Dog dog, SessionUser user) {
        if (!dog.getMember().getMemberId().equals(user.getMemberId())) {
            throw new DogException(ErrorCode.DOG_ACCESS_DENIED);
        }
    }
}