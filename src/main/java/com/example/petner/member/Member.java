package com.example.petner.member;

import com.example.petner.common.Gender;
import com.example.petner.common.HousingType;
import com.example.petner.location.Location;
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
    @Column(name = "memberId")
    private Long memberId;

    @Column(name = "kakaoId", nullable = false, unique = true, length = 255)
    private String kakaoId;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "housingType", nullable = false)
    private HousingType housingType;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "contact", nullable = false, length = 200)
    private String contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationId", nullable = false)
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
}