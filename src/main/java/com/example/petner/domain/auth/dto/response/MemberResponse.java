package com.example.petner.domain.auth.dto.response;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.config.common.Gender;
import com.example.petner.domain.member.common.HousingType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long memberId;
    private String email;
    private String nickname;
    private Gender gender;
    private HousingType housingType;
    private String contact;
    private Long locationId;
    private String state;
    private String district;
    private String locationName; // 조합된 지역명
    private boolean profileCompleted;

    public static MemberResponse from(Member member) {
        String locationName = null;
        if (member.getLocation() != null && 
            member.getLocation().getState() != null && 
            member.getLocation().getDistrict() != null) {
            locationName = member.getLocation().getState() + " " + member.getLocation().getDistrict();
        }
        
        return MemberResponse.builder()
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
                .build();
    }
    
    /**
     * 로그인 직후 최소 정보만 제공하는 응답
     */
    public static MemberResponse forLogin(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .profileCompleted(member.isProfileComplete())
                .build();
    }
}