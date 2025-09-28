package com.example.petner.domain.dogApply.entity;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 유기견 분양 신청 엔티티
 * 사용자가 특정 유기견에 대해 분양을 신청하는 정보를 관리
 */
@Entity
@Table(name = "dog_applies",
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_dog_applies_dog_applicant",
               columnNames = {"dog_id", "applicant_id"}
           )
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DogApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_apply_id")
    private Long dogApplyId;

    /**
     * 분양 신청 대상 유기견
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    /**
     * 분양 신청자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Member applicant;

    /**
     * 신청 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplyStatus status = ApplyStatus.PENDING;

    /**
     * 처리 메시지 (승인/거절 시 유기견 등록자가 작성하는 메시지)
     */
    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    /**
     * 신청 생성 시간
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 마지막 수정 시간 (상태 변경 시)
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 처리 시간 (승인/거절 시점)
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Builder
    public DogApply(Dog dog, Member applicant) {
        this.dog = dog;
        this.applicant = applicant;
        this.status = ApplyStatus.PENDING;
    }

    /**
     * 분양 신청 승인
     * @param responseMessage 승인 메시지
     */
    public void approve(String responseMessage) {
        validatePendingStatus();
        this.status = ApplyStatus.APPROVED;
        this.responseMessage = responseMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 분양 신청 거절
     * @param responseMessage 거절 메시지
     */
    public void reject(String responseMessage) {
        validatePendingStatus();
        this.status = ApplyStatus.REJECTED;
        this.responseMessage = responseMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 대기 상태인지 확인
     * @return 대기 상태 여부
     */
    public boolean isPending() {
        return this.status == ApplyStatus.PENDING;
    }

    /**
     * 승인된 상태인지 확인
     * @return 승인 상태 여부
     */
    public boolean isApproved() {
        return this.status == ApplyStatus.APPROVED;
    }

    /**
     * 거절된 상태인지 확인
     * @return 거절 상태 여부
     */
    public boolean isRejected() {
        return this.status == ApplyStatus.REJECTED;
    }

    /**
     * 처리 완료된 상태인지 확인 (승인 또는 거절)
     * @return 처리 완료 여부
     */
    public boolean isProcessed() {
        return this.status == ApplyStatus.APPROVED || this.status == ApplyStatus.REJECTED;
    }

    /**
     * 대기 상태 검증 (승인/거절 전에 호출)
     */
    private void validatePendingStatus() {
        if (!isPending()) {
            throw new IllegalStateException("이미 처리된 신청입니다. 현재 상태: " + this.status);
        }
    }
}