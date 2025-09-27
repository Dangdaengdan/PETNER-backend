package com.example.petner.domain.location.controller;

import com.example.petner.domain.location.dto.response.LocationSearchResponseDto;
import com.example.petner.domain.location.service.LocationSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "지역 (locations)", description = "지역 관련 API")
public class LocationSearchController {

    private final LocationSearchService locationSearchService;

    @GetMapping("/search")
    @Operation(summary = "지역 이름으로 ID 조회", description = "지역 이름을 기반으로 해당 지역의 ID와 이름을 조회합니다.")
    public ResponseEntity<LocationSearchResponseDto> searchLocationByName(
            @Parameter(description = "검색할 지역 이름 (시/도 구/군 형태)", example = "서울특별시 강남구")
            @RequestParam String name) {

        LocationSearchResponseDto result = locationSearchService.searchByName(name);
        return ResponseEntity.ok(result);
    }
}