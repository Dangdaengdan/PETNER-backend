package com.example.petner.domain.dogApply.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.global.config.common.Gender;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogApplyServiceTest {

    @Mock
    private DogApplyRepository dogApplyRepository;

    @Mock
    private DogApplyValidator dogApplyValidator;

    @InjectMocks
    private DogApplyService dogApplyService;

    private SessionUser sessionUser;
    private SessionUser ownerSession;
    private Member member;
    private Member owner;
    private Breed breed;
    private Dog dog;
    private DogApply dogApply;
    private DogApplyCreateRequestDto createRequestDto;
    private DogApplyProcessRequestDto processRequestDto;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("user@example.com")
                .nickname("사용자")
                .build();

        ownerSession = SessionUser.builder()
                .memberId(2L)
                .email("owner@example.com")
                .nickname("등록자")
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

        breed = Breed.builder()
                .name("믹스견")
                .build();
        ReflectionTestUtils.setField(breed, "breedId", 1L);

        dog = Dog.builder()
                .name("바둑이")
                .breed(breed)
                .gender(Gender.MALE)
                .dogSize(DogSize.중형)
                .adoptionStatus(AdoptionStatus.입양_가능)
                .member(owner)
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);

        dogApply = DogApply.builder()
                .dog(dog)
                .applicant(member)
                .build();
        ReflectionTestUtils.setField(dogApply, "dogApplyId", 1L);
        ReflectionTestUtils.setField(dogApply, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(dogApply, "updatedAt", LocalDateTime.now());

        createRequestDto = new DogApplyCreateRequestDto(1L);
        processRequestDto = new DogApplyProcessRequestDto(ApplyStatus.APPROVED);
    }

    @Test
    @DisplayName("분양 신청 생성 성공")
    void createApplication_Success() {
        // Given
        when(dogApplyValidator.validateAndGetMember(sessionUser)).thenReturn(member);
        when(dogApplyValidator.validateAndGetActiveDog(1L)).thenReturn(dog);
        when(dogApplyRepository.save(any(DogApply.class))).thenReturn(dogApply);

        // When
        DogApplyCreateResponseDto result = dogApplyService.createApplication(createRequestDto, sessionUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDogApplyId()).isEqualTo(1L);

        verify(dogApplyValidator).validateAndGetMember(sessionUser);
        verify(dogApplyValidator).validateAndGetActiveDog(1L);
        verify(dogApplyValidator).validateCreateApplication(dog, member);
        verify(dogApplyRepository).save(any(DogApply.class));
    }

    @Test
    @DisplayName("분양 신청 처리 성공 - 승인")
    void processApplication_Success_Approved() {
        // Given
        when(dogApplyValidator.validateAndGetDogApply(1L)).thenReturn(dogApply);

        // When
        DogApplyProcessResponseDto result = dogApplyService.processApplication(1L, processRequestDto, ownerSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(dogApply.isApproved()).isTrue();

        verify(dogApplyValidator).validateAndGetDogApply(1L);
        verify(dogApplyValidator).validateProcessPermission(dogApply, ownerSession);
    }

    @Test
    @DisplayName("분양 신청 처리 성공 - 거절")
    void processApplication_Success_Rejected() {
        // Given
        DogApplyProcessRequestDto rejectRequestDto = new DogApplyProcessRequestDto(ApplyStatus.REJECTED);
        when(dogApplyValidator.validateAndGetDogApply(1L)).thenReturn(dogApply);

        // When
        DogApplyProcessResponseDto result = dogApplyService.processApplication(1L, rejectRequestDto, ownerSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(dogApply.isRejected()).isTrue();

        verify(dogApplyValidator).validateAndGetDogApply(1L);
        verify(dogApplyValidator).validateProcessPermission(dogApply, ownerSession);
    }

    @Test
    @DisplayName("분양 신청 처리 실패 - 잘못된 상태")
    void processApplication_Fail_InvalidStatus() {
        // Given
        DogApplyProcessRequestDto pendingRequestDto = new DogApplyProcessRequestDto(ApplyStatus.PENDING);
        when(dogApplyValidator.validateAndGetDogApply(1L)).thenReturn(dogApply);

        // When & Then
        assertThatThrownBy(() -> dogApplyService.processApplication(1L, pendingRequestDto, ownerSession))
                .isInstanceOf(DogApplyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_APPLY_INVALID_STATUS);

        verify(dogApplyValidator).validateAndGetDogApply(1L);
        verify(dogApplyValidator).validateProcessPermission(dogApply, ownerSession);
    }

    @Test
    @DisplayName("분양 신청 삭제 성공")
    void deleteApplication_Success() {
        // Given
        when(dogApplyValidator.validateAndGetDogApply(1L)).thenReturn(dogApply);

        // When
        dogApplyService.deleteApplication(1L, sessionUser);

        // Then
        verify(dogApplyValidator).validateAndGetDogApply(1L);
        verify(dogApplyValidator).validateDeletePermission(dogApply, sessionUser);
        verify(dogApplyRepository).delete(dogApply);
    }

    @Test
    @DisplayName("분양 신청 상세 조회 성공")
    void getApplicationDetail_Success() {
        // Given
        when(dogApplyValidator.validateAndGetDogApply(1L)).thenReturn(dogApply);

        // When
        DogApplyResponseDto result = dogApplyService.getApplicationDetail(1L, sessionUser);

        // Then
        assertThat(result).isNotNull();

        verify(dogApplyValidator).validateAndGetDogApply(1L);
        verify(dogApplyValidator).validateViewPermission(dogApply, sessionUser);
    }

    @Test
    @DisplayName("내가 신청한 분양 신청 목록 조회 성공 - 상태 필터 없음")
    void getMyApplications_Success_NoStatusFilter() {
        // Given
        List<DogApply> dogApplies = Arrays.asList(dogApply);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(dogApplyRepository.findByApplicantIdWithDetails(eq(1L), any(Pageable.class)))
                .thenReturn(dogApplies);

        // When
        List<DogApplyListResponseDto> result = dogApplyService.getMyApplications(0, 10, null, sessionUser);

        // Then
        assertThat(result).hasSize(1);

        verify(dogApplyRepository).findByApplicantIdWithDetails(eq(1L), any(Pageable.class));
        verify(dogApplyRepository, never()).findByApplicantIdAndStatusWithDetails(any(), any(), any());
    }

    @Test
    @DisplayName("내가 신청한 분양 신청 목록 조회 성공 - 상태 필터 있음")
    void getMyApplications_Success_WithStatusFilter() {
        // Given
        List<DogApply> dogApplies = Arrays.asList(dogApply);
        ApplyStatus status = ApplyStatus.PENDING;

        when(dogApplyRepository.findByApplicantIdAndStatusWithDetails(eq(1L), eq(status), any(Pageable.class)))
                .thenReturn(dogApplies);

        // When
        List<DogApplyListResponseDto> result = dogApplyService.getMyApplications(0, 10, status, sessionUser);

        // Then
        assertThat(result).hasSize(1);

        verify(dogApplyRepository).findByApplicantIdAndStatusWithDetails(eq(1L), eq(status), any(Pageable.class));
        verify(dogApplyRepository, never()).findByApplicantIdWithDetails(any(), any());
    }

    @Test
    @DisplayName("내 유기견에 대한 분양 신청 목록 조회 성공 - 상태 필터 없음")
    void getReceivedApplications_Success_NoStatusFilter() {
        // Given
        List<DogApply> dogApplies = Arrays.asList(dogApply);

        when(dogApplyRepository.findByDogOwnerIdWithDetails(eq(2L), any(Pageable.class)))
                .thenReturn(dogApplies);

        // When
        List<DogApplyListResponseDto> result = dogApplyService.getReceivedApplications(0, 10, null, ownerSession);

        // Then
        assertThat(result).hasSize(1);

        verify(dogApplyRepository).findByDogOwnerIdWithDetails(eq(2L), any(Pageable.class));
        verify(dogApplyRepository, never()).findByDogOwnerIdAndStatusWithDetails(any(), any(), any());
    }

    @Test
    @DisplayName("내 유기견에 대한 분양 신청 목록 조회 성공 - 상태 필터 있음")
    void getReceivedApplications_Success_WithStatusFilter() {
        // Given
        List<DogApply> dogApplies = Arrays.asList(dogApply);
        ApplyStatus status = ApplyStatus.PENDING;

        when(dogApplyRepository.findByDogOwnerIdAndStatusWithDetails(eq(2L), eq(status), any(Pageable.class)))
                .thenReturn(dogApplies);

        // When
        List<DogApplyListResponseDto> result = dogApplyService.getReceivedApplications(0, 10, status, ownerSession);

        // Then
        assertThat(result).hasSize(1);

        verify(dogApplyRepository).findByDogOwnerIdAndStatusWithDetails(eq(2L), eq(status), any(Pageable.class));
        verify(dogApplyRepository, never()).findByDogOwnerIdWithDetails(any(), any());
    }

    @Test
    @DisplayName("특정 유기견에 대한 신청 개수 조회 성공")
    void getApplicationCount_Success() {
        // Given
        Long dogId = 1L;
        Long expectedCount = 5L;

        when(dogApplyRepository.countByDogId(dogId)).thenReturn(expectedCount);

        // When
        Long result = dogApplyService.getApplicationCount(dogId);

        // Then
        assertThat(result).isEqualTo(expectedCount);

        verify(dogApplyRepository).countByDogId(dogId);
    }

    @Test
    @DisplayName("특정 유기견에 대한 대기 중인 신청 개수 조회 성공")
    void getPendingApplicationCount_Success() {
        // Given
        Long dogId = 1L;
        Long expectedCount = 3L;

        when(dogApplyRepository.countPendingByDogId(dogId)).thenReturn(expectedCount);

        // When
        Long result = dogApplyService.getPendingApplicationCount(dogId);

        // Then
        assertThat(result).isEqualTo(expectedCount);

        verify(dogApplyRepository).countPendingByDogId(dogId);
    }

    @Test
    @DisplayName("페이징 파라미터 정확히 전달되는지 확인")
    void pagingParameters_PassedCorrectly() {
        // Given
        int page = 2;
        int size = 20;
        List<DogApply> dogApplies = Arrays.asList(dogApply);

        when(dogApplyRepository.findByApplicantIdWithDetails(eq(1L), any(Pageable.class)))
                .thenReturn(dogApplies);

        // When
        dogApplyService.getMyApplications(page, size, null, sessionUser);

        // Then
        verify(dogApplyRepository).findByApplicantIdWithDetails(eq(1L), argThat(pageable ->
                pageable.getPageNumber() == page &&
                pageable.getPageSize() == size &&
                pageable.getSort().equals(Sort.by(Sort.Direction.DESC, "createdAt"))
        ));
    }

    @Test
    @DisplayName("빈 목록 조회 시 정상 처리")
    void getMyApplications_EmptyList() {
        // Given
        when(dogApplyRepository.findByApplicantIdWithDetails(eq(1L), any(Pageable.class)))
                .thenReturn(List.of());

        // When
        List<DogApplyListResponseDto> result = dogApplyService.getMyApplications(0, 10, null, sessionUser);

        // Then
        assertThat(result).isEmpty();

        verify(dogApplyRepository).findByApplicantIdWithDetails(eq(1L), any(Pageable.class));
    }
}