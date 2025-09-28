package com.example.petner.search.document;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.global.config.common.Gender;
import com.example.petner.search.dto.DogSearchDto;
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
    private String location; // 통합 위치 정보 (기존 호환성 유지)
    private String memberLocation; // member 위치 정보
    private String shelterLocation; // shelter 위치 정보

    public static DogDocument from(Dog dog) {
        String memberLocation = buildLocationString(dog.getMember().getLocation());
        String shelterLocation = buildLocationString(dog.getShelter() != null ? dog.getShelter().getLocation() : null);
        String primaryLocation = shelterLocation != null ? shelterLocation : memberLocation; // shelter 우선, 없으면 member

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
                .location(primaryLocation)
                .memberLocation(memberLocation)
                .shelterLocation(shelterLocation)
                .build();
    }

    private static String buildLocationString(com.example.petner.domain.location.entity.Location location) {
        return location != null ? location.getState() + " " + location.getDistrict() : null;
    }

    public static DogDocument from(DogSearchDto dto) {
        return DogDocument.builder()
                .id(String.valueOf(dto.getDogId()))
                .dogId(dto.getDogId())
                .name(dto.getName())
                .breedName(dto.getBreedName())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .dogSize(dto.getDogSize())
                .weight(dto.getWeight())
                .healthStatus(dto.getHealthStatus())
                .description(dto.getDescription())
                .adoptionStatus(dto.getAdoptionStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .imageUrl(dto.getImageUrl())
                .memberId(dto.getMemberId())
                .shelterId(dto.getShelterId())
                .shelterName(dto.getShelterName())
                .location(dto.getLocation())
                .memberLocation(dto.getMemberLocation())
                .shelterLocation(dto.getShelterLocation())
                .build();
    }
}