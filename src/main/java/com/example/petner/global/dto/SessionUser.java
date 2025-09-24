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
    
    /**
     * Member 엔티티로부터 SessionUser 생성
     * @param member Member 엔티티
     * @return SessionUser 인스턴스
     * @throws IllegalArgumentException member가 null이거나 필수 필드가 null인 경우
     */
    public static SessionUser from(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member는 null일 수 없습니다");
        }
        
        // 필수 필드 검증 (memberId만 필수, 나머지는 nullable)
        if (member.getMemberId() == null) {
            throw new IllegalArgumentException("Member의 ID는 null일 수 없습니다");
        }
        
        try {
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
        } catch (Exception e) {
            // 지연 로딩 오류나 기타 JPA 관련 오류
            throw new IllegalArgumentException("Member 정보를 SessionUser로 변환하는 중 오류가 발생했습니다", e);
        }
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