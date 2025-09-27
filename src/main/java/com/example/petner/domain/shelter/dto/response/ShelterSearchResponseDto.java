package com.example.petner.domain.shelter.dto.response;

import com.example.petner.domain.shelter.entity.Shelter;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShelterSearchResponseDto {

    private Long shelterId;
    private String name;

    public static ShelterSearchResponseDto from(Shelter shelter) {
        return ShelterSearchResponseDto.builder()
                .shelterId(shelter.getShelterId())
                .name(shelter.getName())
                .build();
    }
}