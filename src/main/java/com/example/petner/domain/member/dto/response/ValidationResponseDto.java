package com.example.petner.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "중복 확인 응답")
public class ValidationResponseDto {
    
    @Schema(description = "사용 가능 여부", example = "true")
    private boolean available;
    
    @Schema(description = "메시지", example = "사용 가능한 닉네임입니다.")
    private String message;

    public static ValidationResponseDto available(String fieldName) {
        return new ValidationResponseDto(true, "사용 가능한 " + fieldName + "입니다.");
    }

    public static ValidationResponseDto unavailable(String fieldName) {
        return new ValidationResponseDto(false, "이미 사용 중인 " + fieldName + "입니다.");
    }
}