package com.example.petner.domain.dogApply.dto.response;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.entity.DogApply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 유기견 분양 신청 생성 응답 DTO
 */
@Getter
@Builder
@Schema(description = "유기견 분양 신청 생성 응답")
public class DogApplyCreateResponseDto {

    @Schema(description = "신청 ID", example = "1")
    private Long dogApplyId;

    @Schema(description = "유기견 ID", example = "1")
    private Long dogId;

    @Schema(description = "신청자 ID", example = "3")
    private Long applicantId;

    @Schema(description = "신청 상태", example = "PENDING")
    private ApplyStatus status;

    @Schema(description = "신청 생성 시간", example = "2024-09-28T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "처리 메시지", example = "분양 신청이 성공적으로 등록되었습니다.")
    private String message;

    /**
     * DogApply 엔티티로부터 DTO 생성
     * @param dogApply 분양 신청 엔티티
     * @return 생성 응답 DTO
     */
    public static DogApplyCreateResponseDto from(DogApply dogApply) {
        return DogApplyCreateResponseDto.builder()
                .dogApplyId(dogApply.getDogApplyId())
                .dogId(dogApply.getDog().getDogId())
                .applicantId(dogApply.getApplicant().getMemberId())
                .status(dogApply.getStatus())
                .createdAt(dogApply.getCreatedAt())
                .message("분양 신청이 성공적으로 등록되었습니다.")
                .build();
    }
}