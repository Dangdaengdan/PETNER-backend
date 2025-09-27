package com.example.petner.domain.member.dto.response;

import com.example.petner.domain.member.common.HousingType;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.config.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "프로필 조회 응답")
public class ProfileResponseDto {
    
    @Schema(description = "회원 ID", example = "1")
    private Long memberId;
    
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    @Schema(description = "닉네임", example = "펫러버")
    private String nickname;
    
    @Schema(description = "성별", example = "MALE")
    private Gender gender;
    
    @Schema(description = "주거 형태", example = "아파트")
    private HousingType housingType;
    
    @Schema(description = "연락처", example = "010-1234-5678")
    private String contact;
    
    @Schema(description = "위치 ID", example = "1")
    private Long locationId;
    
    @Schema(description = "주(시/도)", example = "서울특별시")
    private String state;
    
    @Schema(description = "구(시/군/구)", example = "강남구")
    private String district;
    
    @Schema(description = "지역명", example = "서울특별시 강남구")
    private String locationName;
    
    @Schema(description = "프로필 완성 여부", example = "true")
    private boolean profileCompleted;

    @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    public static ProfileResponseDto from(Member member) {
        String locationName = null;
        if (member.getLocation() != null && 
            member.getLocation().getState() != null && 
            member.getLocation().getDistrict() != null) {
            locationName = member.getLocation().getState() + " " + member.getLocation().getDistrict();
        }
        
        return ProfileResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .housingType(member.getHousingType())
                .contact(member.getContact())
                .locationId(member.getLocation() != null ? member.getLocation().getLocationId() : null)
                .state(member.getLocation() != null ? member.getLocation().getState() : null)
                .district(member.getLocation() != null ? member.getLocation().getDistrict() : null)
                .locationName(locationName)
                .profileCompleted(member.isProfileComplete())
                .createdAt(member.getCreatedAt())
                .build();
    }
}