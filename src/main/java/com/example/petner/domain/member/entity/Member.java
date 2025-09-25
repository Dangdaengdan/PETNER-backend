package com.example.petner.domain.member.entity;

import com.example.petner.domain.member.common.HousingType;
import com.example.petner.global.config.common.Gender;
import com.example.petner.domain.location.entity.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "kakao_id", nullable = false, unique = true, length = 255)
    private String kakaoId;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "nickname", unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "housing_type")
    private HousingType housingType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "contact", length = 200)
    private String contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Builder
    public Member(String kakaoId, String email, String nickname, Gender gender,
                  HousingType housingType, String contact, Location location) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.housingType = housingType;
        this.contact = contact;
        this.location = location;
    }
    
    /**
     * 카카오 로그인용 임시 회원 생성
     */
    public static Member createTemporaryMember(String kakaoId) {
        return Member.builder()
                .kakaoId(kakaoId)
                .build();
    }
    
    /**
     * 프로필 완성 여부 확인
     */
    public boolean isProfileComplete() {
        return nickname != null 
                && email != null 
                && gender != null 
                && housingType != null 
                && contact != null 
                && location != null;
    }
    
    /**
     * 프로필 완성
     */
    public void completeProfile(String email, String nickname, Gender gender,
                               HousingType housingType, String contact, Location location) {
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.housingType = housingType;
        this.contact = contact;
        this.location = location;
    }
    
    /**
     * 프로필 업데이트
     */
    public void updateProfile(String email, String nickname, Gender gender,
                             HousingType housingType, String contact, Location location) {
        if (email != null) this.email = email;
        if (nickname != null) this.nickname = nickname;
        if (gender != null) this.gender = gender;
        if (housingType != null) this.housingType = housingType;
        if (contact != null) this.contact = contact;
        if (location != null) this.location = location;
    }
}