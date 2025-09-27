package com.example.petner.domain.location.service;

import com.example.petner.domain.location.dto.response.LocationSearchResponseDto;
import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.location.repository.LocationRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.LocationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationSearchService {

    private final LocationRepository locationRepository;

    public LocationSearchResponseDto searchByName(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length != 2) {
            throw new LocationException(ErrorCode.LOCATION_INVALID);
        }

        String state = parts[0];
        String district = parts[1];

        Location location = locationRepository.findByStateAndDistrict(state, district)
                .orElseThrow(() -> new LocationException(ErrorCode.LOCATION_NOT_FOUND));

        return LocationSearchResponseDto.from(location);
    }
}