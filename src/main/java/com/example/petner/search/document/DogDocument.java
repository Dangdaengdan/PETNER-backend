package com.example.petner.search.document;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.global.config.common.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogDocument {

    private String id; // OpenSearch document ID (dogId를 문자열로)
    private Long dogId;
    private String name;
    private String breedName;
    private String birthDate;
    private Gender gender;
    private DogSize dogSize;
    private BigDecimal weight;
    private String healthStatus;
    private String description;
    private AdoptionStatus adoptionStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String imageUrl;
    private Long memberId;
    private Long shelterId;
    private String shelterName;
    private String location; // shelter 위치 정보

    public static DogDocument from(Dog dog) {
        return DogDocument.builder()
                .id(String.valueOf(dog.getDogId()))
                .dogId(dog.getDogId())
                .name(dog.getName())
                .breedName(dog.getBreed().getName())
                .birthDate(dog.getBirthDate())
                .gender(dog.getGender())
                .dogSize(dog.getDogSize())
                .weight(dog.getWeight())
                .healthStatus(dog.getHealthStatus())
                .description(dog.getDescription())
                .adoptionStatus(dog.getAdoptionStatus())
                .createdAt(dog.getCreatedAt())
                .updatedAt(dog.getUpdatedAt())
                .imageUrl(dog.getImageUrl())
                .memberId(dog.getMember().getMemberId())
                .shelterId(dog.getShelter() != null ? dog.getShelter().getShelterId() : null)
                .shelterName(dog.getShelter() != null ? dog.getShelter().getName() : null)
                .location(dog.getShelter() != null && dog.getShelter().getLocation() != null ?
                    dog.getShelter().getLocation().getState() + " " + dog.getShelter().getLocation().getDistrict() : null)
                .build();
    }
}