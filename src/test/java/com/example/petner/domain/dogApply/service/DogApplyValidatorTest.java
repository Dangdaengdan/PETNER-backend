package com.example.petner.domain.dogApply.service;

import com.example.petner.domain.dog.common.AdoptionStatus;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogApplyValidatorTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DogApplyRepository dogApplyRepository;

    @InjectMocks
    private DogApplyValidator dogApplyValidator;

    private SessionUser sessionUser;
    private Member member;
    private Member owner;
    private Dog dog;
    private DogApply dogApply;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("user@example.com")
                .nickname("사용자")
                .build();

        member = Member.builder()
                .kakaoId("12345")
                .email("user@example.com")
                .nickname("사용자")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        owner = Member.builder()
                .kakaoId("67890")
                .email("owner@example.com")
                .nickname("등록자")
                .build();
        ReflectionTestUtils.setField(owner, "memberId", 2L);

        dog = Dog.builder()
                .name("바둑이")
                .member(owner)
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);
        ReflectionTestUtils.setField(dog, "adoptionStatus", AdoptionStatus.입양_가능);

        dogApply = DogApply.builder()
                .dog(dog)
                .applicant(member)
                .build();
        ReflectionTestUtils.setField(dogApply, "dogApplyId", 1L);
    }

    @Test
    @DisplayName("사용자 검증 및 조회 성공")
    void validateAndGetMember_Success() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // When
        Member result = dogApplyValidator.validateAndGetMember(sessionUser);

        // Then
        assertThat(result).isEqualTo(member);
        verify(memberRepository).findById(1L);
    }

    @Test
    @DisplayName("사용자 검증 실패 - 존재하지 않는 사용자")
    void validateAndGetMember_NotFound() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateAndGetMember(sessionUser))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);

        verify(memberRepository).findById(1L);
    }

    @Test
    @DisplayName("유기견 검증 및 조회 성공")
    void validateAndGetActiveDog_Success() {
        // Given
        when(dogRepository.findByIdWithAssociations(1L)).thenReturn(Optional.of(dog));

        // When
        Dog result = dogApplyValidator.validateAndGetActiveDog(1L);

        // Then
        assertThat(result).isEqualTo(dog);
        verify(dogRepository).findByIdWithAssociations(1L);
    }

    @Test
    @DisplayName("유기견 검증 실패 - 존재하지 않는 유기견")
    void validateAndGetActiveDog_NotFound() {
        // Given
        when(dogRepository.findByIdWithAssociations(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateAndGetActiveDog(1L))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_NOT_FOUND);

        verify(dogRepository).findByIdWithAssociations(1L);
    }

    @Test
    @DisplayName("분양 신청 검증 및 조회 성공")
    void validateAndGetDogApply_Success() {
        // Given
        when(dogApplyRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(dogApply));

        // When
        DogApply result = dogApplyValidator.validateAndGetDogApply(1L);

        // Then
        assertThat(result).isEqualTo(dogApply);
        verify(dogApplyRepository).findByIdWithDetails(1L);
    }

    @Test
    @DisplayName("분양 신청 검증 실패 - 존재하지 않는 신청")
    void validateAndGetDogApply_NotFound() {
        // Given
        when(dogApplyRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateAndGetDogApply(1L))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_NOT_FOUND);

        verify(dogApplyRepository).findByIdWithDetails(1L);
    }

    @Test
    @DisplayName("분양 신청 생성 검증 성공")
    void validateCreateApplication_Success() {
        // Given
        when(dogApplyRepository.existsByDogIdAndApplicantId(1L, 1L)).thenReturn(false);

        // When & Then
        dogApplyValidator.validateCreateApplication(dog, member);

        verify(dogApplyRepository).existsByDogIdAndApplicantId(1L, 1L);
    }

    @Test
    @DisplayName("분양 신청 생성 검증 실패 - 자신의 강아지에게 신청")
    void validateCreateApplication_SelfApplication() {
        // Given
        Dog ownDog = Dog.builder()
                .name("내강아지")
                .member(member) // 같은 사용자
                .build();
        ReflectionTestUtils.setField(ownDog, "dogId", 2L);

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateCreateApplication(ownDog, member))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_SELF_APPLICATION);

        verify(dogApplyRepository, never()).existsByDogIdAndApplicantId(any(), any());
    }

    @Test
    @DisplayName("분양 신청 생성 검증 실패 - 이미 신청한 내역 존재")
    void validateCreateApplication_AlreadyExists() {
        // Given
        when(dogApplyRepository.existsByDogIdAndApplicantId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateCreateApplication(dog, member))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_ALREADY_EXISTS);

        verify(dogApplyRepository).existsByDogIdAndApplicantId(1L, 1L);
    }

    @Test
    @DisplayName("분양 신청 생성 검증 실패 - 입양 불가능한 상태")
    void validateCreateApplication_NotAvailable() {
        // Given
        ReflectionTestUtils.setField(dog, "adoptionStatus", AdoptionStatus.입양_완료);
        when(dogApplyRepository.existsByDogIdAndApplicantId(1L, 1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateCreateApplication(dog, member))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_NOT_AVAILABLE);

        verify(dogApplyRepository).existsByDogIdAndApplicantId(1L, 1L);
    }

    @Test
    @DisplayName("분양 신청 처리 권한 검증 성공")
    void validateProcessPermission_Success() {
        // Given
        SessionUser ownerSession = SessionUser.builder()
                .memberId(2L) // 강아지 등록자
                .email("owner@example.com")
                .nickname("등록자")
                .build();

        // When & Then
        dogApplyValidator.validateProcessPermission(dogApply, ownerSession);
    }

    @Test
    @DisplayName("분양 신청 처리 권한 검증 실패 - 권한 없음")
    void validateProcessPermission_AccessDenied() {
        // Given
        SessionUser otherUser = SessionUser.builder()
                .memberId(3L) // 다른 사용자
                .email("other@example.com")
                .nickname("다른사용자")
                .build();

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateProcessPermission(dogApply, otherUser))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_ACCESS_DENIED);
    }

    @Test
    @DisplayName("분양 신청 조회 권한 검증 성공 - 신청자")
    void validateViewPermission_Success_Applicant() {
        // Given (신청자)
        // When & Then
        dogApplyValidator.validateViewPermission(dogApply, sessionUser);
    }

    @Test
    @DisplayName("분양 신청 조회 권한 검증 성공 - 강아지 등록자")
    void validateViewPermission_Success_Owner() {
        // Given
        SessionUser ownerSession = SessionUser.builder()
                .memberId(2L) // 강아지 등록자
                .email("owner@example.com")
                .nickname("등록자")
                .build();

        // When & Then
        dogApplyValidator.validateViewPermission(dogApply, ownerSession);
    }

    @Test
    @DisplayName("분양 신청 조회 권한 검증 실패 - 권한 없음")
    void validateViewPermission_AccessDenied() {
        // Given
        SessionUser otherUser = SessionUser.builder()
                .memberId(3L) // 관련 없는 사용자
                .email("other@example.com")
                .nickname("다른사용자")
                .build();

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateViewPermission(dogApply, otherUser))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_ACCESS_DENIED);
    }

    @Test
    @DisplayName("분양 신청 삭제 권한 검증 성공")
    void validateDeletePermission_Success() {
        // Given (신청자, PENDING 상태)
        // When & Then
        dogApplyValidator.validateDeletePermission(dogApply, sessionUser);
    }

    @Test
    @DisplayName("분양 신청 삭제 권한 검증 실패 - 권한 없음")
    void validateDeletePermission_AccessDenied() {
        // Given
        SessionUser otherUser = SessionUser.builder()
                .memberId(3L) // 다른 사용자
                .email("other@example.com")
                .nickname("다른사용자")
                .build();

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateDeletePermission(dogApply, otherUser))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_ACCESS_DENIED);
    }

    @Test
    @DisplayName("분양 신청 삭제 권한 검증 실패 - 이미 처리된 신청")
    void validateDeletePermission_AlreadyProcessed() {
        // Given
        dogApply.approve(); // 승인 처리

        // When & Then
        assertThatThrownBy(() -> dogApplyValidator.validateDeletePermission(dogApply, sessionUser))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_ALREADY_PROCESSED);
    }
}