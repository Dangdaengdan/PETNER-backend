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
    private boolean profileCompleted;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .housingType(member.getHousingType())
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