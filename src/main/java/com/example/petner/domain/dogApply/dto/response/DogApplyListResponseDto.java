package com.example.petner.domain.dogApply.dto.response;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.entity.DogApply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 유기견 분양 신청 목록 응답 DTO
 * 목록 조회용으로 최적화된 간소화된 정보
 */
@Getter
@Builder
@Schema(description = "유기견 분양 신청 목록 정보")
public class DogApplyListResponseDto {

    @Schema(description = "신청 ID", example = "1")
    private Long dogApplyId;

    @Schema(description = "신청 상태", example = "PENDING")
    private ApplyStatus status;

    @Schema(description = "신청 생성 시간", example = "2024-09-28T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "처리 시간", example = "2024-09-28T14:20:00")
    private LocalDateTime processedAt;

    // 강아지 기본 정보
    @Schema(description = "강아지 ID", example = "1")
    private Long dogId;

    @Schema(description = "강아지 이름", example = "멍멍이")
    private String dogName;

    @Schema(description = "견종명", example = "말티즈")
    private String breedName;

    @Schema(description = "강아지 사진 URL")
    private String dogImageUrl;

    @Schema(description = "입양 상태", example = "입양_가능")
    private String adoptionStatus;

    // 상대방 정보 (신청자 관점에서는 등록자, 등록자 관점에서는 신청자)
    @Schema(description = "상대방 ID", example = "2")
    private Long counterpartId;

    @Schema(description = "상대방 닉네임", example = "동물보호자")
    private String counterpartNickname;

    @Schema(description = "지역 정보", example = "서울 강남구")
    private String location;

    /**
     * 신청자 관점에서 DogApply 엔티티로부터 DTO 생성
     * @param dogApply 분양 신청 엔티티
     * @return 목록 응답 DTO
     */
    public static DogApplyListResponseDto fromApplicantView(DogApply dogApply) {
        return DogApplyListResponseDto.builder()
                .dogApplyId(dogApply.getDogApplyId())
                .status(dogApply.getStatus())
                .createdAt(dogApply.getCreatedAt())
                .processedAt(dogApply.getProcessedAt())
                .dogId(dogApply.getDog().getDogId())
                .dogName(dogApply.getDog().getName())
                .breedName(dogApply.getDog().getBreed().getName())
                .dogImageUrl(dogApply.getDog().getImageUrl())
                .adoptionStatus(dogApply.getDog().getAdoptionStatus().name())
                // 신청자 관점에서는 유기견 등록자가 상대방
                .counterpartId(dogApply.getDog().getMember().getMemberId())
                .counterpartNickname(dogApply.getDog().getMember().getNickname())
                .location(dogApply.getDog().getShelter() != null &&
                        dogApply.getDog().getShelter().getLocation() != null ?
                        dogApply.getDog().getShelter().getLocation().getState() + " " +
                        dogApply.getDog().getShelter().getLocation().getDistrict() : null)
                .build();
    }

    /**
     * 유기견 등록자 관점에서 DogApply 엔티티로부터 DTO 생성
     * @param dogApply 분양 신청 엔티티
     * @return 목록 응답 DTO
     */
    public static DogApplyListResponseDto fromOwnerView(DogApply dogApply) {
        return DogApplyListResponseDto.builder()
                .dogApplyId(dogApply.getDogApplyId())
                .status(dogApply.getStatus())
                .createdAt(dogApply.getCreatedAt())
                .processedAt(dogApply.getProcessedAt())
                .dogId(dogApply.getDog().getDogId())
                .dogName(dogApply.getDog().getName())
                .breedName(dogApply.getDog().getBreed().getName())
                .dogImageUrl(dogApply.getDog().getImageUrl())
                .adoptionStatus(dogApply.getDog().getAdoptionStatus().name())
                // 등록자 관점에서는 신청자가 상대방
                .counterpartId(dogApply.getApplicant().getMemberId())
                .counterpartNickname(dogApply.getApplicant().getNickname())
                .location(dogApply.getDog().getShelter() != null &&
                        dogApply.getDog().getShelter().getLocation() != null ?
                        dogApply.getDog().getShelter().getLocation().getState() + " " +
                        dogApply.getDog().getShelter().getLocation().getDistrict() : null)
                .build();
    }
}