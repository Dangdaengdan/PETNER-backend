package com.example.petner.domain.dog.dto.request;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.global.config.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 유기견 수정 요청 DTO
 * 클라이언트로부터 유기견 수정 정보를 받기 위한 데이터 전송 객체
 */
@Getter
@NoArgsConstructor
@Schema(description = "유기견 수정 요청 데이터")
public class DogUpdateRequestDto {

    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    @Schema(description = "유기견 이름", example = "바둑이")
    private String name;

    @Positive(message = "견종 ID는 양수여야 합니다")
    @Schema(description = "견종 ID", example = "1")
    private Long breedId;

    @Pattern(regexp = "^\\d{6}$", message = "출생년월은 YYYYMM 형식이어야 합니다")
    @Schema(description = "출생년월 (YYYYMM 형식)", example = "202201")
    private String birthDate;

    @Schema(description = "성별 (MALE/FEMALE)", example = "MALE")
    private Gender gender;

    @Schema(description = "크기 (소형/중형/대형)", example = "중형")
    private DogSize dogSize;

    @DecimalMin(value = "0.0", inclusive = false, message = "몸무게는 0보다 커야 합니다")
    @DecimalMax(value = "999.99", message = "몸무게는 999.99kg 이하여야 합니다")
    @Schema(description = "몸무게 (kg)", example = "15.5")
    private BigDecimal weight;

    @Size(max = 1000, message = "건강상태는 1000자 이하여야 합니다")
    @Schema(description = "건강상태", example = "예방접종 완료, 건강상태 양호")
    private String healthStatus;

    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
    @Schema(description = "설명", example = "사람을 좋아하고 온순한 성격입니다")
    private String description;

    @Schema(description = "입양상태 (입양_가능/입양_절차_중/입양_완료)", example = "입양_가능")
    private AdoptionStatus adoptionStatus;

    @Size(max = 500, message = "이미지 URL은 500자 이하여야 합니다")
    @Schema(description = "이미지 URL", example = "https://example.com/dog-image.jpg")
    private String imageUrl;

    @Positive(message = "보호소 ID는 양수여야 합니다")
    @Schema(description = "보호소 ID", example = "1")
    private Long shelterId;
}