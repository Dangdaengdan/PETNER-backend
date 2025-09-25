package com.example.petner.domain.member.dto.request;

import com.example.petner.domain.member.common.HousingType;
import com.example.petner.global.config.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 완성 요청")
public class ProfileCompleteRequestDto {

    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다.")
    private String email;

    @Schema(description = "닉네임", example = "펫러버")
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
    private String nickname;

    @Schema(description = "성별", example = "MALE")
    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @Schema(description = "주거 형태", example = "아파트")
    @NotNull(message = "주거 형태는 필수입니다.")
    private HousingType housingType;

    @Schema(description = "연락처", example = "010-1234-5678")
    @NotBlank(message = "연락처는 필수입니다.")
    @Size(max = 200, message = "연락처는 200자를 초과할 수 없습니다.")
    private String contact;

    @Schema(description = "위치 ID", example = "1")
    @NotNull(message = "위치 정보는 필수입니다.")
    private Long locationId;
}