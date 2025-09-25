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
 * 유기견 응답 DTO
 * 클라이언트에게 유기견 정보를 전달하기 위한 데이터 전송 객체
 */
@Getter
@Builder
@Schema(description = "유기견 상세 정보 응답 데이터")
public class DogResponseDto {

    @Schema(description = "유기견 ID", example = "1")
    private Long dogId;

    @Schema(description = "유기견 이름", example = "바둑이")
    private String name;

    @Schema(description = "견종 정보")
    private BreedInfo breed;

    @Schema(description = "출생년월 (YYYYMM 형식)", example = "202201")
    private String birthDate;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @Schema(description = "크기", example = "중형")
    private DogSize dogSize;

    @Schema(description = "몸무게 (kg)", example = "15.5")
    private BigDecimal weight;

    @Schema(description = "건강상태", example = "예방접종 완료, 건강상태 양호")
    private String healthStatus;

    @Schema(description = "설명", example = "사람을 좋아하고 온순한 성격입니다")
    private String description;

    @Schema(description = "입양상태", example = "입양_가능")
    private AdoptionStatus adoptionStatus;

    @Schema(description = "이미지 URL", example = "https://example.com/dog-image.jpg")
    private String imageUrl;

    @Schema(description = "등록자 정보")
    private MemberInfo member;

    @Schema(description = "보호소 정보")
    private ShelterInfo shelter;

    @Schema(description = "등록일시", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    /**
     * Dog 엔티티로부터 DogResponseDto 생성
     */
    public static DogResponseDto from(Dog dog) {
        return DogResponseDto.builder()
                .dogId(dog.getDogId())
                .name(dog.getName())
                .breed(BreedInfo.from(dog.getBreed()))
                .birthDate(dog.getBirthDate())
                .gender(dog.getGender())
                .dogSize(dog.getDogSize())
                .weight(dog.getWeight())
                .healthStatus(dog.getHealthStatus())
                .description(dog.getDescription())
                .adoptionStatus(dog.getAdoptionStatus())
                .imageUrl(dog.getImageUrl())
                .member(MemberInfo.from(dog.getMember()))
                .shelter(dog.getShelter() != null ? ShelterInfo.from(dog.getShelter()) : null)
                .createdAt(dog.getCreatedAt())
                .updatedAt(dog.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    @Schema(description = "견종 정보")
    public static class BreedInfo {

        @Schema(description = "견종 ID", example = "1")
        private Long breedId;

        @Schema(description = "견종명", example = "골든 리트리버")
        private String name;

        public static BreedInfo from(com.example.petner.domain.breed.entity.Breed breed) {
            return BreedInfo.builder()
                    .breedId(breed.getBreedId())
                    .name(breed.getName())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "등록자 정보")
    public static class MemberInfo {

        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "닉네임", example = "동물애호가")
        private String nickname;

        public static MemberInfo from(com.example.petner.domain.member.entity.Member member) {
            return MemberInfo.builder()
                    .memberId(member.getMemberId())
                    .nickname(member.getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "보호소 정보")
    public static class ShelterInfo {

        @Schema(description = "보호소 ID", example = "1")
        private Long shelterId;

        @Schema(description = "보호소명", example = "서울시 강남구 동물보호센터")
        private String name;

        @Schema(description = "연락처", example = "02-1234-5678")
        private String contact;

        public static ShelterInfo from(com.example.petner.domain.shelter.entity.Shelter shelter) {
            return ShelterInfo.builder()
                    .shelterId(shelter.getShelterId())
                    .name(shelter.getName())
                    .contact(shelter.getShelterContact())
                    .build();
        }
    }
}