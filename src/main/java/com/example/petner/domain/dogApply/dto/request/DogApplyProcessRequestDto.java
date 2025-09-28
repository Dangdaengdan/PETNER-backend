package com.example.petner.domain.dogApply.dto.request;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유기견 분양 신청 처리 요청 DTO (승인/거절)
 */
@Getter
@NoArgsConstructor
@Schema(description = "유기견 분양 신청 처리 요청")
public class DogApplyProcessRequestDto {

    @NotNull(message = "처리 상태는 필수입니다")
    @Schema(description = "처리 상태 (APPROVED: 승인, REJECTED: 거절)",
            example = "APPROVED",
            allowableValues = {"APPROVED", "REJECTED"},
            required = true)
    private ApplyStatus status;

    public DogApplyProcessRequestDto(ApplyStatus status) {
        this.status = status;
    }

    /**
     * 승인 상태인지 확인
     * @return 승인 여부
     */
    public boolean isApproval() {
        return this.status == ApplyStatus.APPROVED;
    }

    /**
     * 거절 상태인지 확인
     * @return 거절 여부
     */
    public boolean isRejection() {
        return this.status == ApplyStatus.REJECTED;
    }
}