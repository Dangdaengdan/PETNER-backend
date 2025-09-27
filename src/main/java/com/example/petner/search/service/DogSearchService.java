package com.example.petner.search.service;

import com.example.petner.search.document.DogDocument;
import com.example.petner.search.repository.DogSearchRepository;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.global.config.common.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DogSearchService {

    private final DogSearchRepository dogSearchRepository;

    public List<DogDocument> searchDogs(String keyword,
                                       DogSize dogSize,
                                       String breedName,
                                       Gender gender,
                                       String location,
                                       AdoptionStatus adoptionStatus,
                                       int page,
                                       int size) {
        try {
            int from = page * size;
            return dogSearchRepository.searchWithFilters(
                keyword, dogSize, breedName, gender, location, adoptionStatus, from, size
            );
        } catch (IOException e) {
            log.error("Failed to search dogs", e);
            throw new RuntimeException("강아지 검색 중 오류가 발생했습니다.", e);
        }
    }

    public List<DogDocument> searchDogsByKeyword(String keyword, int page, int size) {
        return searchDogs(keyword, null, null, null, null, null, page, size);
    }
}