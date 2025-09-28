package com.example.petner.domain.dogApply.dto.response;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.entity.DogApply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 유기견 분양 신청 처리 응답 DTO (승인/거절 후)
 */
@Getter
@Builder
@Schema(description = "유기견 분양 신청 처리 응답")
public class DogApplyProcessResponseDto {

    @Schema(description = "신청 ID", example = "1")
    private Long dogApplyId;

    @Schema(description = "유기견 ID", example = "1")
    private Long dogId;

    @Schema(description = "신청자 ID", example = "3")
    private Long applicantId;

    @Schema(description = "처리된 상태", example = "APPROVED")
    private ApplyStatus status;


    @Schema(description = "처리 시간", example = "2024-09-28T14:20:00")
    private LocalDateTime processedAt;

    @Schema(description = "처리 결과 메시지", example = "분양 신청이 승인되었습니다.")
    private String message;

    /**
     * DogApply 엔티티로부터 DTO 생성
     * @param dogApply 분양 신청 엔티티
     * @return 처리 응답 DTO
     */
    public static DogApplyProcessResponseDto from(DogApply dogApply) {
        String resultMessage = dogApply.getStatus() == ApplyStatus.APPROVED ?
                "분양 신청이 승인되었습니다." : "분양 신청이 거절되었습니다.";

        return DogApplyProcessResponseDto.builder()
                .dogApplyId(dogApply.getDogApplyId())
                .dogId(dogApply.getDog().getDogId())
                .applicantId(dogApply.getApplicant().getMemberId())
                .status(dogApply.getStatus())
                .processedAt(dogApply.getProcessedAt())
                .message(resultMessage)
                .build();
    }
}