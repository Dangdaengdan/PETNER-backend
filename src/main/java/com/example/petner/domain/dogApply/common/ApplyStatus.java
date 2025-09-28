package com.example.petner.domain.dogApply.common;

/**
 * 유기견 분양 신청 상태
 */
public enum ApplyStatus {
    /**
     * 대기 중 - 신청 후 아직 처리되지 않은 상태
     */
    PENDING,

    /**
     * 승인됨 - 분양 신청이 승인된 상태
     */
    APPROVED,

    /**
     * 거절됨 - 분양 신청이 거절된 상태
     */
    REJECTED
}