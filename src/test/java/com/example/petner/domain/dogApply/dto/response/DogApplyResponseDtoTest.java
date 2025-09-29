package com.example.petner.domain.dogApply.dto.response;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.entity.DogApply;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.config.common.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DogApplyResponseDtoTest {

    private DogApply dogApply;
    private Dog dog;
    private Breed breed;
    private Member applicant;
    private Member owner;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        owner = Member.builder()
                .kakaoId("67890")
                .email("owner@example.com")
                .nickname("등록자")
                .build();
        ReflectionTestUtils.setField(owner, "memberId", 2L);

        applicant = Member.builder()
                .kakaoId("12345")
                .email("applicant@example.com")
                .nickname("신청자")
                .build();
        ReflectionTestUtils.setField(applicant, "memberId", 1L);

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
                .applicant(applicant)
                .build();
        ReflectionTestUtils.setField(dogApply, "dogApplyId", 1L);
        ReflectionTestUtils.setField(dogApply, "createdAt", testTime);
        ReflectionTestUtils.setField(dogApply, "updatedAt", testTime);
    }

    @Test
    @DisplayName("DogApply 엔티티로부터 DogApplyResponseDto 생성 성공 - PENDING 상태")
    void from_Success_PendingStatus() {
        // When
        DogApplyResponseDto result = DogApplyResponseDto.from(dogApply);

        // Then
        assertThat(result.getDogApplyId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ApplyStatus.PENDING);
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getUpdatedAt()).isEqualTo(testTime);
        assertThat(result.getProcessedAt()).isNull();

        // DogInfo 확인
        assertThat(result.getDog()).isNotNull();
        assertThat(result.getDog().getDogId()).isEqualTo(1L);
        assertThat(result.getDog().getName()).isEqualTo("바둑이");

        // ApplicantInfo 확인
        assertThat(result.getApplicant()).isNotNull();
        assertThat(result.getApplicant().getMemberId()).isEqualTo(1L);
        assertThat(result.getApplicant().getNickname()).isEqualTo("신청자");
    }

    @Test
    @DisplayName("DogApply 엔티티로부터 DogApplyResponseDto 생성 성공 - APPROVED 상태")
    void from_Success_ApprovedStatus() {
        // Given
        dogApply.approve();
        LocalDateTime processedTime = dogApply.getProcessedAt();

        // When
        DogApplyResponseDto result = DogApplyResponseDto.from(dogApply);

        // Then
        assertThat(result.getDogApplyId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ApplyStatus.APPROVED);
        assertThat(result.getProcessedAt()).isEqualTo(processedTime);
        assertThat(result.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("DogApply 엔티티로부터 DogApplyResponseDto 생성 성공 - REJECTED 상태")
    void from_Success_RejectedStatus() {
        // Given
        dogApply.reject();
        LocalDateTime processedTime = dogApply.getProcessedAt();

        // When
        DogApplyResponseDto result = DogApplyResponseDto.from(dogApply);

        // Then
        assertThat(result.getDogApplyId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ApplyStatus.REJECTED);
        assertThat(result.getProcessedAt()).isEqualTo(processedTime);
        assertThat(result.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("Builder 패턴으로 DogApplyResponseDto 생성")
    void builder_Success() {
        // Given
        DogApplyResponseDto.DogInfo dogInfo = DogApplyResponseDto.DogInfo.builder()
                .dogId(1L)
                .name("바둑이")
                .build();
        DogApplyResponseDto.ApplicantInfo applicantInfo = DogApplyResponseDto.ApplicantInfo.builder()
                .memberId(1L)
                .nickname("신청자")
                .email("applicant@example.com")
                .build();

        // When
        DogApplyResponseDto result = DogApplyResponseDto.builder()
                .dogApplyId(1L)
                .dog(dogInfo)
                .applicant(applicantInfo)
                .status(ApplyStatus.PENDING)
                .createdAt(testTime)
                .updatedAt(testTime)
                .processedAt(null)
                .build();

        // Then
        assertThat(result.getDogApplyId()).isEqualTo(1L);
        assertThat(result.getDog()).isEqualTo(dogInfo);
        assertThat(result.getApplicant()).isEqualTo(applicantInfo);
        assertThat(result.getStatus()).isEqualTo(ApplyStatus.PENDING);
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getUpdatedAt()).isEqualTo(testTime);
        assertThat(result.getProcessedAt()).isNull();
    }

    @Test
    @DisplayName("DogInfo 내부 클래스 정상 동작")
    void dogInfo_InnerClass() {
        // When
        DogApplyResponseDto.DogInfo dogInfo = DogApplyResponseDto.DogInfo.builder()
                .dogId(1L)
                .name("바둑이")
                .breedName("믹스견")
                .build();

        // Then
        assertThat(dogInfo.getDogId()).isEqualTo(1L);
        assertThat(dogInfo.getName()).isEqualTo("바둑이");
        assertThat(dogInfo.getBreedName()).isEqualTo("믹스견");
    }

    @Test
    @DisplayName("ApplicantInfo 내부 클래스 정상 동작")
    void applicantInfo_InnerClass() {
        // When
        DogApplyResponseDto.ApplicantInfo applicantInfo = DogApplyResponseDto.ApplicantInfo.builder()
                .memberId(1L)
                .nickname("신청자")
                .email("applicant@example.com")
                .build();

        // Then
        assertThat(applicantInfo.getMemberId()).isEqualTo(1L);
        assertThat(applicantInfo.getNickname()).isEqualTo("신청자");
        assertThat(applicantInfo.getEmail()).isEqualTo("applicant@example.com");
    }

    @Test
    @DisplayName("시간 필드들 정확히 매핑됨")
    void timeFields_MappedCorrectly() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 1, 11, 0, 0);
        ReflectionTestUtils.setField(dogApply, "createdAt", createdAt);
        ReflectionTestUtils.setField(dogApply, "updatedAt", updatedAt);

        dogApply.approve();
        LocalDateTime processedAt = dogApply.getProcessedAt();

        // When
        DogApplyResponseDto result = DogApplyResponseDto.from(dogApply);

        // Then
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(result.getProcessedAt()).isEqualTo(processedAt);
        assertThat(result.getProcessedAt()).isAfter(updatedAt);
    }

    @Test
    @DisplayName("모든 상태에 대한 변환 테스트")
    void allStatusConversion() {
        // PENDING 상태
        DogApplyResponseDto pendingDto = DogApplyResponseDto.from(dogApply);
        assertThat(pendingDto.getStatus()).isEqualTo(ApplyStatus.PENDING);
        assertThat(pendingDto.getProcessedAt()).isNull();

        // APPROVED 상태
        dogApply.approve();
        DogApplyResponseDto approvedDto = DogApplyResponseDto.from(dogApply);
        assertThat(approvedDto.getStatus()).isEqualTo(ApplyStatus.APPROVED);
        assertThat(approvedDto.getProcessedAt()).isNotNull();

        // REJECTED 상태를 위한 새로운 신청 생성
        DogApply rejectedApply = DogApply.builder()
                .dog(dog)
                .applicant(applicant)
                .build();
        ReflectionTestUtils.setField(rejectedApply, "dogApplyId", 2L);
        ReflectionTestUtils.setField(rejectedApply, "createdAt", testTime);

        rejectedApply.reject();
        DogApplyResponseDto rejectedDto = DogApplyResponseDto.from(rejectedApply);
        assertThat(rejectedDto.getStatus()).isEqualTo(ApplyStatus.REJECTED);
        assertThat(rejectedDto.getProcessedAt()).isNotNull();
    }
}