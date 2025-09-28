package com.example.petner.domain.dogApply.service;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.dto.request.DogApplyCreateRequestDto;
import com.example.petner.domain.dogApply.dto.request.DogApplyProcessRequestDto;
import com.example.petner.domain.dogApply.dto.response.*;
import com.example.petner.domain.dogApply.entity.DogApply;
import com.example.petner.domain.dogApply.repository.DogApplyRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogApplyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 유기견 분양 신청 서비스
 * SOLID 원칙을 준수하여 설계된 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DogApplyService {

    private final DogApplyRepository dogApplyRepository;
    private final DogApplyValidator dogApplyValidator;

    /**
     * 분양 신청 생성
     * @param requestDto 신청 요청 데이터
     * @param user 세션 사용자 정보
     * @return 생성된 신청 정보
     */
    @Transactional
    public DogApplyCreateResponseDto createApplication(DogApplyCreateRequestDto requestDto, SessionUser user) {
        // 1. 사용자 검증
        Member applicant = dogApplyValidator.validateAndGetMember(user);

        // 2. 유기견 검증
        Dog dog = dogApplyValidator.validateAndGetActiveDog(requestDto.getDogId());

        // 3. 신청 생성 전 검증
        dogApplyValidator.validateCreateApplication(dog, applicant);

        // 4. 분양 신청 엔티티 생성
        DogApply dogApply = DogApply.builder()
                .dog(dog)
                .applicant(applicant)
                .applicationMessage(requestDto.getApplicationMessage())
                .build();

        // 5. 데이터베이스에 저장
        DogApply savedDogApply = dogApplyRepository.save(dogApply);

        log.info("분양 신청 생성 완료 - dogId: {}, applicantId: {}, applyId: {}",
                requestDto.getDogId(), user.getMemberId(), savedDogApply.getDogApplyId());

        return DogApplyCreateResponseDto.from(savedDogApply);
    }

    /**
     * 분양 신청 처리 (승인/거절)
     * @param dogApplyId 신청 ID
     * @param requestDto 처리 요청 데이터
     * @param user 세션 사용자 정보
     * @return 처리 결과 정보
     */
    @Transactional
    public DogApplyProcessResponseDto processApplication(Long dogApplyId, DogApplyProcessRequestDto requestDto, SessionUser user) {
        // 1. 분양 신청 조회 및 검증
        DogApply dogApply = dogApplyValidator.validateAndGetDogApply(dogApplyId);

        // 2. 처리 권한 검증 (유기견 등록자만 가능)
        dogApplyValidator.validateProcessPermission(dogApply, user);

        // 3. 처리 상태 검증
        validateProcessStatus(requestDto.getStatus());

        // 4. 신청 처리 (승인 또는 거절)
        if (requestDto.isApproval()) {
            dogApply.approve(requestDto.getResponseMessage());
        } else if (requestDto.isRejection()) {
            dogApply.reject(requestDto.getResponseMessage());
        }

        log.info("분양 신청 처리 완료 - applyId: {}, status: {}, processedBy: {}",
                dogApplyId, requestDto.getStatus(), user.getMemberId());

        return DogApplyProcessResponseDto.from(dogApply);
    }

    /**
     * 분양 신청 삭제 (신청자만 가능, 대기 상태일 때만)
     * @param dogApplyId 신청 ID
     * @param user 세션 사용자 정보
     */
    @Transactional
    public void deleteApplication(Long dogApplyId, SessionUser user) {
        // 1. 분양 신청 조회 및 검증
        DogApply dogApply = dogApplyValidator.validateAndGetDogApply(dogApplyId);

        // 2. 삭제 권한 검증
        dogApplyValidator.validateDeletePermission(dogApply, user);

        // 3. 신청 삭제
        dogApplyRepository.delete(dogApply);

        log.info("분양 신청 삭제 완료 - applyId: {}, deletedBy: {}", dogApplyId, user.getMemberId());
    }

    /**
     * 분양 신청 상세 조회
     * @param dogApplyId 신청 ID
     * @param user 세션 사용자 정보
     * @return 신청 상세 정보
     */
    public DogApplyResponseDto getApplicationDetail(Long dogApplyId, SessionUser user) {
        // 1. 분양 신청 조회
        DogApply dogApply = dogApplyValidator.validateAndGetDogApply(dogApplyId);

        // 2. 조회 권한 검증
        dogApplyValidator.validateViewPermission(dogApply, user);

        return DogApplyResponseDto.from(dogApply);
    }

    /**
     * 내가 신청한 분양 신청 목록 조회 (신청자 관점)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param status 상태 필터 (선택적)
     * @param user 세션 사용자 정보
     * @return 신청 목록
     */
    public List<DogApplyListResponseDto> getMyApplications(int page, int size, ApplyStatus status, SessionUser user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<DogApply> dogApplies;
        if (status != null) {
            dogApplies = dogApplyRepository.findByApplicantIdAndStatusWithDetails(user.getMemberId(), status, pageable);
        } else {
            dogApplies = dogApplyRepository.findByApplicantIdWithDetails(user.getMemberId(), pageable);
        }

        return dogApplies.stream()
                .map(DogApplyListResponseDto::fromApplicantView)
                .collect(Collectors.toList());
    }

    /**
     * 내 유기견에 대한 분양 신청 목록 조회 (등록자 관점)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param status 상태 필터 (선택적)
     * @param user 세션 사용자 정보
     * @return 신청 목록
     */
    public List<DogApplyListResponseDto> getReceivedApplications(int page, int size, ApplyStatus status, SessionUser user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<DogApply> dogApplies;
        if (status != null) {
            dogApplies = dogApplyRepository.findByDogOwnerIdAndStatusWithDetails(user.getMemberId(), status, pageable);
        } else {
            dogApplies = dogApplyRepository.findByDogOwnerIdWithDetails(user.getMemberId(), pageable);
        }

        return dogApplies.stream()
                .map(DogApplyListResponseDto::fromOwnerView)
                .collect(Collectors.toList());
    }

    /**
     * 특정 유기견에 대한 신청 개수 조회
     * @param dogId 유기견 ID
     * @return 신청 개수
     */
    public Long getApplicationCount(Long dogId) {
        return dogApplyRepository.countByDogId(dogId);
    }

    /**
     * 특정 유기견에 대한 대기 중인 신청 개수 조회
     * @param dogId 유기견 ID
     * @return 대기 중인 신청 개수
     */
    public Long getPendingApplicationCount(Long dogId) {
        return dogApplyRepository.countPendingByDogId(dogId);
    }

    /**
     * 처리 상태 검증
     * @param status 처리 상태
     */
    private void validateProcessStatus(ApplyStatus status) {
        if (status == ApplyStatus.PENDING) {
            throw new DogApplyException(ErrorCode.DOG_APPLY_INVALID_STATUS);
        }
    }
}