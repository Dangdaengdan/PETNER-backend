package com.example.petner.domain.dogApply.dto.request;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

}