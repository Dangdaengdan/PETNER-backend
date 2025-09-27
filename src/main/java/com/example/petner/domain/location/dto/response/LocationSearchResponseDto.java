package com.example.petner.domain.location.dto.response;

import com.example.petner.domain.location.entity.Location;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationSearchResponseDto {

    private Long locationId;
    private String name;

    public static LocationSearchResponseDto from(Location location) {
        return LocationSearchResponseDto.builder()
                .locationId(location.getLocationId())
                .name(location.getState() + " " + location.getDistrict())
                .build();
    }
}