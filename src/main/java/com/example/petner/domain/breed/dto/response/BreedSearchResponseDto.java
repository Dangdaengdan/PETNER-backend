package com.example.petner.domain.breed.dto.response;

import com.example.petner.domain.breed.entity.Breed;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BreedSearchResponseDto {

    private Long breedId;
    private String name;

    public static BreedSearchResponseDto from(Breed breed) {
        return BreedSearchResponseDto.builder()
                .breedId(breed.getBreedId())
                .name(breed.getName())
                .build();
    }
}