package com.example.petner.domain.breed.controller;

import com.example.petner.domain.breed.dto.response.BreedSearchResponseDto;
import com.example.petner.domain.breed.service.BreedSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/breeds")
@RequiredArgsConstructor
@Tag(name = "견종 (breeds)", description = "견종 관련 API")
public class BreedSearchController {

    private final BreedSearchService breedSearchService;

    @GetMapping("/search")
    @Operation(summary = "견종 이름으로 ID 조회", description = "견종 이름을 기반으로 해당 견종의 ID와 이름을 조회합니다.")
    public ResponseEntity<BreedSearchResponseDto> searchBreedByName(
            @Parameter(description = "검색할 견종 이름", example = "골든 리트리버")
            @RequestParam String name) {

        BreedSearchResponseDto result = breedSearchService.searchByName(name);
        return ResponseEntity.ok(result);
    }
}