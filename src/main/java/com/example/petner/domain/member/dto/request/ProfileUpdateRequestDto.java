package com.example.petner.domain.member.dto.request;

import com.example.petner.domain.member.common.HousingType;
import com.example.petner.global.config.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 수정 요청")
public class ProfileUpdateRequestDto {

    @Schema(description = "이메일", example = "user@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다.")
    private String email;

    @Schema(description = "닉네임", example = "펫러버")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
    private String nickname;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @Schema(description = "주거 형태", example = "아파트")
    private HousingType housingType;

    @Schema(description = "연락처", example = "010-1234-5678")
    @Size(max = 200, message = "연락처는 200자를 초과할 수 없습니다.")
    private String contact;

    @Schema(description = "위치 ID", example = "1")
    private Long locationId;
}