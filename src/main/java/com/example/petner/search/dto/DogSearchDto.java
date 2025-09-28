package com.example.petner.search.dto;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.global.config.common.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DogSearchDto {
    private final Long dogId;
    private final String name;
    private final String breedName;
    private final String birthDate;
    private final Gender gender;
    private final DogSize dogSize;
    private final BigDecimal weight;
    private final String healthStatus;
    private final String description;
    private final AdoptionStatus adoptionStatus;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final String imageUrl;
    private final Long memberId;
    private final Long shelterId;
    private final String shelterName;
    private final String location;
    private final String memberLocation;
    private final String shelterLocation;

    public static DogSearchDto from(Dog dog) {
        String memberLocation = buildLocationString(dog.getMember().getLocation());
        String shelterLocation = buildLocationString(dog.getShelter() != null ? dog.getShelter().getLocation() : null);
        String primaryLocation = shelterLocation != null ? shelterLocation : memberLocation; // shelter 우선, 없으면 member

        return new DogSearchDto(
                dog.getDogId(),
                dog.getName(),
                dog.getBreed().getName(),
                dog.getBirthDate(),
                dog.getGender(),
                dog.getDogSize(),
                dog.getWeight(),
                dog.getHealthStatus(),
                dog.getDescription(),
                dog.getAdoptionStatus(),
                dog.getCreatedAt(),
                dog.getUpdatedAt(),
                dog.getImageUrl(),
                dog.getMember().getMemberId(),
                dog.getShelter() != null ? dog.getShelter().getShelterId() : null,
                dog.getShelter() != null ? dog.getShelter().getName() : null,
                primaryLocation,
                memberLocation,
                shelterLocation
        );
    }

    private static String buildLocationString(com.example.petner.domain.location.entity.Location location) {
        return location != null ? location.getState() + " " + location.getDistrict() : null;
    }
}