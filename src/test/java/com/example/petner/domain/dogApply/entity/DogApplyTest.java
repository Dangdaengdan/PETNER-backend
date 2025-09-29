package com.example.petner.domain.dogApply.entity;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DogApplyTest {

    private DogApply dogApply;
    private Dog dog;
    private Member applicant;

    @BeforeEach
    void setUp() {
        applicant = Member.builder()
                .kakaoId("12345")
                .email("applicant@example.com")
                .nickname("신청자")
                .build();
        ReflectionTestUtils.setField(applicant, "memberId", 1L);

        Member owner = Member.builder()
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

        dogApply = DogApply.builder()
                .dog(dog)
                .applicant(applicant)
                .build();
        ReflectionTestUtils.setField(dogApply, "dogApplyId", 1L);
        ReflectionTestUtils.setField(dogApply, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(dogApply, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("DogApply 엔티티 생성 성공")
    void createDogApply_Success() {
        // Then
        assertThat(dogApply.getDog()).isEqualTo(dog);
        assertThat(dogApply.getApplicant()).isEqualTo(applicant);
        assertThat(dogApply.getStatus()).isEqualTo(ApplyStatus.PENDING);
        assertThat(dogApply.getProcessedAt()).isNull();
    }

    @Test
    @DisplayName("분양 신청 승인 성공")
    void approve_Success() {
        // When
        dogApply.approve();

        // Then
        assertThat(dogApply.getStatus()).isEqualTo(ApplyStatus.APPROVED);
        assertThat(dogApply.getProcessedAt()).isNotNull();
        assertThat(dogApply.isApproved()).isTrue();
        assertThat(dogApply.isProcessed()).isTrue();
        assertThat(dogApply.isPending()).isFalse();
    }

    @Test
    @DisplayName("분양 신청 거절 성공")
    void reject_Success() {
        // When
        dogApply.reject();

        // Then
        assertThat(dogApply.getStatus()).isEqualTo(ApplyStatus.REJECTED);
        assertThat(dogApply.getProcessedAt()).isNotNull();
        assertThat(dogApply.isRejected()).isTrue();
        assertThat(dogApply.isProcessed()).isTrue();
        assertThat(dogApply.isPending()).isFalse();
    }

    @Test
    @DisplayName("이미 승인된 신청을 다시 승인하려 할 때 예외 발생")
    void approve_AlreadyApproved_ThrowsException() {
        // Given
        dogApply.approve();

        // When & Then
        assertThatThrownBy(() -> dogApply.approve())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 처리된 신청입니다");
    }

    @Test
    @DisplayName("이미 거절된 신청을 다시 거절하려 할 때 예외 발생")
    void reject_AlreadyRejected_ThrowsException() {
        // Given
        dogApply.reject();

        // When & Then
        assertThatThrownBy(() -> dogApply.reject())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 처리된 신청입니다");
    }

    @Test
    @DisplayName("이미 승인된 신청을 거절하려 할 때 예외 발생")
    void reject_AlreadyApproved_ThrowsException() {
        // Given
        dogApply.approve();

        // When & Then
        assertThatThrownBy(() -> dogApply.reject())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 처리된 신청입니다");
    }

    @Test
    @DisplayName("이미 거절된 신청을 승인하려 할 때 예외 발생")
    void approve_AlreadyRejected_ThrowsException() {
        // Given
        dogApply.reject();

        // When & Then
        assertThatThrownBy(() -> dogApply.approve())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 처리된 신청입니다");
    }

    @Test
    @DisplayName("상태 확인 메서드들 정상 동작")
    void statusCheckMethods_Work() {
        // 초기 상태 (PENDING)
        assertThat(dogApply.isPending()).isTrue();
        assertThat(dogApply.isApproved()).isFalse();
        assertThat(dogApply.isRejected()).isFalse();
        assertThat(dogApply.isProcessed()).isFalse();

        // 승인 후 상태
        dogApply.approve();
        assertThat(dogApply.isPending()).isFalse();
        assertThat(dogApply.isApproved()).isTrue();
        assertThat(dogApply.isRejected()).isFalse();
        assertThat(dogApply.isProcessed()).isTrue();
    }

    @Test
    @DisplayName("processedAt 시간이 승인/거절 시 정확히 설정됨")
    void processedAt_SetCorrectly() {
        // Given
        LocalDateTime beforeApproval = LocalDateTime.now().minusSeconds(1);

        // When
        dogApply.approve();

        // Then
        LocalDateTime afterApproval = LocalDateTime.now().plusSeconds(1);
        assertThat(dogApply.getProcessedAt()).isAfter(beforeApproval);
        assertThat(dogApply.getProcessedAt()).isBefore(afterApproval);
    }

    @Test
    @DisplayName("Builder 패턴으로 DogApply 생성 시 기본값 설정 확인")
    void builder_DefaultValues() {
        // When
        DogApply newDogApply = DogApply.builder()
                .dog(dog)
                .applicant(applicant)
                .build();

        // Then
        assertThat(newDogApply.getStatus()).isEqualTo(ApplyStatus.PENDING);
        assertThat(newDogApply.getProcessedAt()).isNull();
    }
}