package com.example.petner.global.dto;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.common.HousingType;
import com.example.petner.global.config.common.Gender;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Builder
public class SessionUser implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Long memberId;
    private final String nickname;
    private final String email;
    private final Gender gender;
    private final HousingType housingType;
    private final String contact;
    private final Long locationId;
    private final String state;
    private final String district;
    private final boolean profileCompleted;
    
    public static SessionUser from(Member member) {
        return SessionUser.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .gender(member.getGender())
                .housingType(member.getHousingType())
                .contact(member.getContact())
                .locationId(member.getLocation() != null ? member.getLocation().getLocationId() : null)
                .state(member.getLocation() != null ? member.getLocation().getState() : null)
                .district(member.getLocation() != null ? member.getLocation().getDistrict() : null)
                .profileCompleted(member.isProfileComplete())
                .build();
    }
    
    /**
     * 지역 정보를 문자열로 조합
     * @return "서울특별시 강남구" 형태의 지역명
     */
    public String getLocationName() {
        if (state != null && district != null) {
            return state + " " + district;
        }
        return null;
    }
}