package com.example.petner.domain.shelter.controller;

import com.example.petner.domain.shelter.dto.response.ShelterSearchResponseDto;
import com.example.petner.domain.shelter.service.ShelterSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shelters")
@RequiredArgsConstructor
@Tag(name = "보호소 (shelters)", description = "보호소 관련 API")
public class ShelterSearchController {

    private final ShelterSearchService shelterSearchService;

    @GetMapping("/search")
    @Operation(summary = "보호소 이름으로 ID 조회", description = "보호소 이름을 기반으로 해당 보호소의 ID와 이름을 조회합니다.")
    public ResponseEntity<ShelterSearchResponseDto> searchShelterByName(
            @Parameter(description = "검색할 보호소 이름", example = "서울동물복지센터")
            @RequestParam String name) {

        ShelterSearchResponseDto result = shelterSearchService.searchByName(name);
        return ResponseEntity.ok(result);
    }
}