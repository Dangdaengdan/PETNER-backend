package com.example.petner.domain.chat.service;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.repository.DogRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 채팅방 생성 시 필요한 검증 로직을 담당하는 컴포넌트
 * Single Responsibility Principle을 적용하여 검증 책임만 담당
 */
@Component
@RequiredArgsConstructor
public class  ChatRoomValidator {

    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;

    /**
     * 멤버 존재 여부 검증
     *
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 검증된 멤버 배열 [member1, member2]
     */
    public Member[] validateAndGetMembers(Long member1Id, Long member2Id) {
        Member member1 = memberRepository.findById(member1Id)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MEMBER_NOT_FOUND));
        Member member2 = memberRepository.findById(member2Id)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MEMBER_NOT_FOUND));

        return new Member[]{member1, member2};
    }

    /**
     * 강아지 존재 여부 검증 및 소유자 검증
     *
     * @param dogId 강아지 ID (nullable)
     * @param member1 첫 번째 멤버
     * @param member2 두 번째 멤버
     * @return 검증된 강아지 (dogId가 null이면 null 반환)
     */
    public Dog validateAndGetDog(Long dogId, Member member1, Member member2) {
        if (dogId == null) {
            return null;
        }

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_DOG_OWNER_MISMATCH));

        validateDogAdoptionStatus(dog);
        validateDogOwnership(dog, member1, member2);

        return dog;
    }

    /**
     * 강아지 입양 상태 검증
     * 입양 완료된 강아지는 채팅방 생성 불가
     *
     * @param dog 강아지
     */
    private void validateDogAdoptionStatus(Dog dog) {
        if (dog.getAdoptionStatus() == AdoptionStatus.입양_완료) {
            throw new ChatException(ErrorCode.CHAT_ALREADY_ADOPTED);
        }
    }

    /**
     * 강아지 소유자가 채팅방 참여자 중 한 명인지 검증
     *
     * @param dog 강아지
     * @param member1 첫 번째 멤버
     * @param member2 두 번째 멤버
     */
    private void validateDogOwnership(Dog dog, Member member1, Member member2) {
        if (!dog.getMember().getMemberId().equals(member1.getMemberId()) &&
            !dog.getMember().getMemberId().equals(member2.getMemberId())) {
            throw new ChatException(ErrorCode.CHAT_DOG_OWNER_MISMATCH);
        }
    }
}