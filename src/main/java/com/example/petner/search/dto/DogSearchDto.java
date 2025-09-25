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

    public static DogSearchDto from(Dog dog) {
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
                dog.getShelter() != null && dog.getShelter().getLocation() != null ?
                    dog.getShelter().getLocation().getState() + " " + dog.getShelter().getLocation().getDistrict() : null
        );
    }
}