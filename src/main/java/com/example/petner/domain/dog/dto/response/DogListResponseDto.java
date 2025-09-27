package com.example.petner.domain.dog.dto.response;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.global.config.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 유기견 목록 응답 DTO
 * 유기견 목록 조회 시 사용되는 간소화된 정보
 */
@Getter
@Builder
@Schema(description = "유기견 목록 응답 데이터")
public class DogListResponseDto {

    @Schema(description = "유기견 ID", example = "1")
    private Long dogId;

    @Schema(description = "유기견 이름", example = "바둑이")
    private String name;

    @Schema(description = "견종명", example = "골든 리트리버")
    private String breedName;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @Schema(description = "크기", example = "중형")
    private DogSize dogSize;

    @Schema(description = "몸무게 (kg)", example = "15.5")
    private BigDecimal weight;

    @Schema(description = "입양상태", example = "입양_가능")
    private AdoptionStatus adoptionStatus;

    @Schema(description = "이미지 URL", example = "https://example.com/dog-image.jpg")
    private String imageUrl;

    @Schema(description = "등록자 닉네임", example = "동물애호가")
    private String memberNickname;

    @Schema(description = "보호소명", example = "서울시 강남구 동물보호센터")
    private String shelterName;

    @Schema(description = "생년월일", example = "202301")
    private String birthDate;

    @Schema(description = "등록일시", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    /**
     * Dog 엔티티로부터 DogListResponseDto 생성
     */
    public static DogListResponseDto from(Dog dog) {
        return DogListResponseDto.builder()
                .dogId(dog.getDogId())
                .name(dog.getName())
                .breedName(dog.getBreed().getName())
                .gender(dog.getGender())
                .dogSize(dog.getDogSize())
                .weight(dog.getWeight())
                .adoptionStatus(dog.getAdoptionStatus())
                .imageUrl(dog.getImageUrl())
                .memberNickname(dog.getMember().getNickname())
                .shelterName(dog.getShelter() != null ? dog.getShelter().getName() : null)
                .birthDate(dog.getBirthDate())
                .createdAt(dog.getCreatedAt())
                .build();
    }
}