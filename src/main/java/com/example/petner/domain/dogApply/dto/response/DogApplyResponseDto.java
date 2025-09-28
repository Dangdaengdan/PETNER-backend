package com.example.petner.domain.dogApply.dto.response;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.entity.DogApply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 유기견 분양 신청 상세 응답 DTO
 * 강아지 정보와 신청자 정보를 모두 포함
 */
@Getter
@Builder
@Schema(description = "유기견 분양 신청 상세 정보")
public class DogApplyResponseDto {

    @Schema(description = "신청 ID", example = "1")
    private Long dogApplyId;

    @Schema(description = "신청 상태", example = "PENDING")
    private ApplyStatus status;


    @Schema(description = "신청 생성 시간", example = "2024-09-28T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정 시간", example = "2024-09-28T14:20:00")
    private LocalDateTime updatedAt;

    @Schema(description = "처리 시간", example = "2024-09-28T14:20:00")
    private LocalDateTime processedAt;

    // 강아지 정보
    @Schema(description = "강아지 정보")
    private DogInfo dog;

    // 신청자 정보
    @Schema(description = "신청자 정보")
    private ApplicantInfo applicant;

    @Getter
    @Builder
    @Schema(description = "강아지 정보")
    public static class DogInfo {
        @Schema(description = "강아지 ID", example = "1")
        private Long dogId;

        @Schema(description = "강아지 이름", example = "멍멍이")
        private String name;

        @Schema(description = "견종명", example = "말티즈")
        private String breedName;

        @Schema(description = "성별", example = "MALE")
        private String gender;

        @Schema(description = "크기", example = "소형")
        private String dogSize;

        @Schema(description = "강아지 사진 URL")
        private String imageUrl;

        @Schema(description = "입양 상태", example = "입양_가능")
        private String adoptionStatus;

        @Schema(description = "등록자 ID", example = "2")
        private Long ownerId;

        @Schema(description = "등록자 닉네임", example = "동물보호자")
        private String ownerNickname;

        @Schema(description = "보호소명", example = "서울동물보호소")
        private String shelterName;

        @Schema(description = "지역", example = "서울 강남구")
        private String location;
    }

    @Getter
    @Builder
    @Schema(description = "신청자 정보")
    public static class ApplicantInfo {
        @Schema(description = "신청자 ID", example = "3")
        private Long memberId;

        @Schema(description = "신청자 닉네임", example = "동물사랑")
        private String nickname;

        @Schema(description = "신청자 이메일", example = "user@example.com")
        private String email;
    }

    /**
     * DogApply 엔티티로부터 DTO 생성
     * @param dogApply 분양 신청 엔티티
     * @return 응답 DTO
     */
    public static DogApplyResponseDto from(DogApply dogApply) {
        return DogApplyResponseDto.builder()
                .dogApplyId(dogApply.getDogApplyId())
                .status(dogApply.getStatus())
                .createdAt(dogApply.getCreatedAt())
                .updatedAt(dogApply.getUpdatedAt())
                .processedAt(dogApply.getProcessedAt())
                .dog(DogInfo.builder()
                        .dogId(dogApply.getDog().getDogId())
                        .name(dogApply.getDog().getName())
                        .breedName(dogApply.getDog().getBreed().getName())
                        .gender(dogApply.getDog().getGender().name())
                        .dogSize(dogApply.getDog().getDogSize().name())
                        .imageUrl(dogApply.getDog().getImageUrl())
                        .adoptionStatus(dogApply.getDog().getAdoptionStatus().name())
                        .ownerId(dogApply.getDog().getMember().getMemberId())
                        .ownerNickname(dogApply.getDog().getMember().getNickname())
                        .shelterName(dogApply.getDog().getShelter() != null ?
                                dogApply.getDog().getShelter().getName() : null)
                        .location(dogApply.getDog().getShelter() != null &&
                                dogApply.getDog().getShelter().getLocation() != null ?
                                dogApply.getDog().getShelter().getLocation().getState() + " " +
                                dogApply.getDog().getShelter().getLocation().getDistrict() : null)
                        .build())
                .applicant(ApplicantInfo.builder()
                        .memberId(dogApply.getApplicant().getMemberId())
                        .nickname(dogApply.getApplicant().getNickname())
                        .email(dogApply.getApplicant().getEmail())
                        .build())
                .build();
    }
}