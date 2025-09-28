package com.example.petner.domain.dogApply.service;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.repository.DogRepository;
import com.example.petner.domain.dogApply.entity.DogApply;
import com.example.petner.domain.dogApply.repository.DogApplyRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogApplyException;
import com.example.petner.global.exception.customException.DogException;
import com.example.petner.global.exception.customException.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 유기견 분양 신청 검증 담당 클래스
 * Single Responsibility Principle - 검증 로직만 담당
 */
@Component
@RequiredArgsConstructor
public class DogApplyValidator {

    private final DogRepository dogRepository;
    private final MemberRepository memberRepository;
    private final DogApplyRepository dogApplyRepository;

    /**
     * 사용자 검증 및 조회
     * @param user 세션 사용자 정보
     * @return 검증된 Member 엔티티
     */
    public Member validateAndGetMember(SessionUser user) {
        return memberRepository.findById(user.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 유기견 검증 및 조회 (활성 상태만)
     * @param dogId 유기견 ID
     * @return 검증된 Dog 엔티티
     */
    public Dog validateAndGetActiveDog(Long dogId) {
        return dogRepository.findByIdWithAssociations(dogId)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_NOT_FOUND));
    }

    /**
     * 분양 신청 검증 및 조회
     * @param dogApplyId 신청 ID
     * @return 검증된 DogApply 엔티티
     */
    public DogApply validateAndGetDogApply(Long dogApplyId) {
        return dogApplyRepository.findByIdWithDetails(dogApplyId)
                .orElseThrow(() -> new DogApplyException(ErrorCode.DOG_APPLY_NOT_FOUND));
    }

    /**
     * 분양 신청 생성 전 검증
     * @param dog 유기견
     * @param applicant 신청자
     */
    public void validateCreateApplication(Dog dog, Member applicant) {
        // 1. 자신의 강아지에게 신청할 수 없음
        if (dog.getMember().getMemberId().equals(applicant.getMemberId())) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_SELF_APPLICATION);
        }

        // 2. 이미 신청한 내역이 있는지 확인
        if (dogApplyRepository.existsByDogIdAndApplicantId(dog.getDogId(), applicant.getMemberId())) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_ALREADY_EXISTS);
        }

        // 3. 입양 가능한 상태인지 확인
        if (dog.getAdoptionStatus() != com.example.petner.domain.dog.common.AdoptionStatus.입양_가능) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_NOT_AVAILABLE);
        }
    }

    /**
     * 분양 신청 처리 권한 검증 (유기견 등록자만 가능)
     * @param dogApply 분양 신청
     * @param user 세션 사용자
     */
    public void validateProcessPermission(DogApply dogApply, SessionUser user) {
        if (!dogApply.getDog().getMember().getMemberId().equals(user.getMemberId())) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_ACCESS_DENIED);
        }
    }

    /**
     * 분양 신청 조회 권한 검증 (신청자 또는 유기견 등록자만 가능)
     * @param dogApply 분양 신청
     * @param user 세션 사용자
     */
    public void validateViewPermission(DogApply dogApply, SessionUser user) {
        Long userId = user.getMemberId();
        Long applicantId = dogApply.getApplicant().getMemberId();
        Long ownerId = dogApply.getDog().getMember().getMemberId();

        if (!userId.equals(applicantId) && !userId.equals(ownerId)) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_ACCESS_DENIED);
        }
    }

    /**
     * 분양 신청 삭제 권한 검증 (신청자만 가능, 대기 상태일 때만)
     * @param dogApply 분양 신청
     * @param user 세션 사용자
     */
    public void validateDeletePermission(DogApply dogApply, SessionUser user) {
        // 신청자 본인만 삭제 가능
        if (!dogApply.getApplicant().getMemberId().equals(user.getMemberId())) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_ACCESS_DENIED);
        }

        // 대기 상태일 때만 삭제 가능
        if (!dogApply.isPending()) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_ALREADY_PROCESSED);
        }
    }
}