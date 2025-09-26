package com.example.petner.domain.favorite.controller;

import com.example.petner.domain.favorite.dto.request.FavoriteAddRequestDto;
import com.example.petner.domain.favorite.dto.response.FavoriteActionResponseDto;
import com.example.petner.domain.favorite.dto.response.FavoriteCheckResponseDto;
import com.example.petner.domain.favorite.dto.response.FavoriteListResponseDto;
import com.example.petner.domain.favorite.dto.response.FavoriteResponseDto;
import com.example.petner.domain.favorite.service.FavoriteService;
import com.example.petner.domain.favorite.service.FavoriteQueryService;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "즐겨찾기 (favorites)", description = "즐겨찾기 관련 API")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteQueryService favoriteQueryService;

    /**
     * 즐겨찾기 추가 API
     *
     * @param requestDto 즐겨찾기 추가 요청 데이터 (강아지 ID)
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 추가된 즐겨찾기 정보 (201 Created)
     *
     * 비즈니스 로직:
     * 1. 세션에서 로그인 사용자 정보 자동 추출
     * 2. 요청된 강아지가 존재하는지 검증
     * 3. 이미 즐겨찾기에 추가되어 있는지 중복 확인
     * 4. 즐겨찾기 추가 및 결과 반환
     */
    @PostMapping("")
    @Operation(summary = "즐겨찾기 추가", description = "강아지를 즐겨찾기에 추가합니다.")
    @ApiResponse(responseCode = "201", description = "즐겨찾기 추가 성공")
    public ResponseEntity<FavoriteResponseDto> addFavorite(
            @RequestBody FavoriteAddRequestDto requestDto,
            @SessionMember SessionUser user) {
        FavoriteResponseDto responseDto = favoriteService.addFavorite(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 즐겨찾기 제거 API
     *
     * @param dogId 제거할 강아지 ID
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 처리 결과 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 세션에서 로그인 사용자 정보 자동 추출
     * 2. 즐겨찾기 존재 여부 확인
     * 3. 즐겨찾기 제거 처리
     */
    @DeleteMapping("/{dogId}")
    @Operation(summary = "즐겨찾기 제거", description = "강아지를 즐겨찾기에서 제거합니다.")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 제거 성공")
    public ResponseEntity<FavoriteActionResponseDto> removeFavorite(
            @PathVariable Long dogId,
            @SessionMember SessionUser user) {
        favoriteService.removeFavorite(dogId, user);
        FavoriteActionResponseDto responseDto = new FavoriteActionResponseDto(
                user.getMemberId(), dogId, "즐겨찾기 제거 성공");
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 내 즐겨찾기 목록 조회 API
     *
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 즐겨찾기 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 세션에서 로그인 사용자 정보 자동 추출
     * 2. 해당 사용자의 모든 즐겨찾기 조회
     * 3. 강아지 상세 정보와 함께 반환
     * 4. N+1 문제 방지를 위한 효율적인 조회 수행
     */
    @GetMapping("/my")
    @Operation(summary = "내 즐겨찾기 목록 조회", description = "로그인한 사용자의 즐겨찾기 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 목록 조회 성공")
    public ResponseEntity<List<FavoriteListResponseDto>> getMyFavorites(@SessionMember SessionUser user) {
        List<FavoriteListResponseDto> favorites = favoriteQueryService.getMemberFavorites(user.getMemberId());
        return ResponseEntity.ok(favorites);
    }

    /**
     * 즐겨찾기 여부 확인 API
     *
     * @param dogId 확인할 강아지 ID
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 즐겨찾기 여부 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 세션에서 로그인 사용자 정보 자동 추출
     * 2. 특정 강아지에 대한 즐겨찾기 여부 확인
     * 3. 구조화된 DTO로 결과 반환
     */
    @GetMapping("/check/{dogId}")
    @Operation(summary = "즐겨찾기 여부 확인", description = "특정 강아지가 즐겨찾기에 있는지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 여부 확인 성공")
    public ResponseEntity<FavoriteCheckResponseDto> checkFavorite(
            @PathVariable Long dogId,
            @SessionMember SessionUser user) {
        boolean isFavorite = favoriteQueryService.isFavorite(user.getMemberId(), dogId);
        FavoriteCheckResponseDto responseDto = new FavoriteCheckResponseDto(
                user.getMemberId(), dogId, isFavorite);
        return ResponseEntity.ok(responseDto);
    }
}