package com.example.petner.domain.breed.service;

import com.example.petner.domain.breed.dto.response.BreedSearchResponseDto;
import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.breed.repository.BreedRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BreedSearchService {

    private final BreedRepository breedRepository;

    public BreedSearchResponseDto searchByName(String name) {
        Breed breed = breedRepository.findByName(name)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_BREED_NOT_FOUND));

        return BreedSearchResponseDto.from(breed);
    }
}