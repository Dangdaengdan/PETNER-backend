package com.example.petner.dog;

import com.example.petner.breed.Breed;
import com.example.petner.common.AdoptionStatus;
import com.example.petner.common.DogSize;
import com.example.petner.common.Gender;
import com.example.petner.member.Member;
import com.example.petner.shelter.Shelter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dogs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dogId")
    private Long dogId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breedId", nullable = false)
    private Breed breed;

    @Column(name = "birthDate", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "dogSize", nullable = false)
    private DogSize dogSize;

    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "healthStatus", columnDefinition = "TEXT")
    private String healthStatus;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "adoptionStatus", nullable = false)
    private AdoptionStatus adoptionStatus;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "imageUrl", nullable = false, length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelterId")
    private Shelter shelter;

    @Builder
    public Dog(String name, Breed breed, LocalDate birthDate, Gender gender, DogSize dogSize,
               BigDecimal weight, String healthStatus, String description, AdoptionStatus adoptionStatus,
               String imageUrl, Member member, Shelter shelter) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.gender = gender;
        this.dogSize = dogSize;
        this.weight = weight;
        this.healthStatus = healthStatus;
        this.description = description;
        this.adoptionStatus = adoptionStatus;
        this.imageUrl = imageUrl;
        this.member = member;
        this.shelter = shelter;
    }
}