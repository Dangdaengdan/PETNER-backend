package com.example.petner.domain.shelter.service;

import com.example.petner.domain.shelter.dto.response.ShelterSearchResponseDto;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.shelter.repository.ShelterRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ShelterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShelterSearchService {

    private final ShelterRepository shelterRepository;

    public ShelterSearchResponseDto searchByName(String name) {
        Shelter shelter = shelterRepository.findByName(name)
                .orElseThrow(() -> new ShelterException(ErrorCode.SHELTER_NOT_FOUND));

        return ShelterSearchResponseDto.from(shelter);
    }
}