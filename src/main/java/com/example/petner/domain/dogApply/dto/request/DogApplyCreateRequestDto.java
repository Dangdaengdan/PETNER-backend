package com.example.petner.domain.dogApply.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유기견 분양 신청 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "유기견 분양 신청 생성 요청")
public class DogApplyCreateRequestDto {

    @NotNull(message = "유기견 ID는 필수입니다")
    @Schema(description = "분양 신청할 유기견 ID", example = "1", required = true)
    private Long dogId;

    public DogApplyCreateRequestDto(Long dogId) {
        this.dogId = dogId;
    }
}