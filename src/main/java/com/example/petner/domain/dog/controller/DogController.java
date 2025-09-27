package com.example.petner.domain.dog.controller;

import com.example.petner.domain.dog.dto.request.DogCreateRequestDto;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.dto.response.DogDeleteResponseDto;
import com.example.petner.domain.dog.dto.response.DogListResponseDto;
import com.example.petner.domain.dog.dto.response.DogResponseDto;
import com.example.petner.domain.dog.service.DogService;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.global.config.common.Gender;
import com.example.petner.search.document.DogDocument;
import com.example.petner.search.service.DogSearchService;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 유기견 관리 컨트롤러
 * 유기견 CRUD 기능을 제공하는 REST API 컨트롤러
 */
@Tag(name = "유기견 (dogs)", description = "유기견 관련 API")
@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
public class  DogController {

    private final DogService dogService;
    private final DogSearchService dogSearchService;

    /**
     * 유기견 정보 등록 API
     *
     * @param requestDto 유기견 등록 요청 데이터
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 등록된 유기견 정보 (201 Created)
     *
     * 비즈니스 로직:
     * 1. 세션에서 로그인 사용자 정보 자동 추출
     * 2. 견종 및 보호소 정보 검증
     * 3. 유기견 정보를 데이터베이스에 저장
     * 4. 등록된 유기견 정보 반환
     */
    @PostMapping
    @Operation(summary = "유기견 정보 등록", description = "새로운 유기견 정보를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "유기견 등록 성공")
    public ResponseEntity<DogResponseDto> createDog(
            @Valid @RequestBody DogCreateRequestDto requestDto,
            @SessionMember SessionUser user) {

        DogResponseDto responseDto = dogService.createDog(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 유기견 목록 조회 API (페이징)
     *
     * @param page 페이지 번호 (선택, 기본값: 0)
     * @param size 페이지 크기 (선택, 기본값: 10)
     * @return 유기견 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 데이터베이스에서 모든 유기견 정보를 페이징하여 조회
     * 2. N+1 문제 해결을 위한 페치 조인 적용
     * 3. 최신 등록순으로 정렬하여 반환
     * 4. 페이징 처리로 성능 최적화
     */
    @GetMapping
    @Operation(summary = "유기견 목록 조회 (페이징 o)", description = "유기견 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "유기견 목록 조회 성공")
    public ResponseEntity<List<DogListResponseDto>> getDogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 페이징된 유기견 목록 조회
        List<DogListResponseDto> dogs = dogService.getDogs(page, size);
        return ResponseEntity.ok(dogs);
    }

    /**
     * 전체 유기견 목록 조회 API (페이징 없음)
     *
     * 프론트엔드 테스트용 엔드포인트
     * 실제 프로덕션에서는 위의 페이징 API 사용 권장
     *
     * @return 전체 유기견 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 데이터베이스에서 모든 유기견 정보 조회
     * 2. 최신 등록순으로 정렬
     * 3. 목록 형태의 간소화된 정보 반환
     * 4. N+1 문제 해결을 위한 페치 조인 적용
     */
    @GetMapping("/all")
    @Operation(summary = "유기견 목록 전체 조회 (페이징 x)", description = "전체 유기견 목록을 조회합니다(디버깅용)")
    @ApiResponse(responseCode = "200", description = "유기견 목록 전체 조회 성공")
    public ResponseEntity<List<DogListResponseDto>> getAllDogs() {
        List<DogListResponseDto> dogs = dogService.getAllDogs();
        return ResponseEntity.ok(dogs);
    }

    /**
     * 내가 등록한 유기견 목록 조회 API (페이징)
     *
     * @param page 페이지 번호 (선택, 기본값: 0)
     * @param size 페이지 크기 (선택, 기본값: 10)
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 내가 등록한 유기견 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 세션 사용자 ID로 등록한 유기견만 필터링
     * 2. 데이터베이스에서 해당 사용자가 등록한 유기견 정보를 페이징하여 조회
     * 3. N+1 문제 해결을 위한 페치 조인 적용
     * 4. 최신 등록순으로 정렬하여 반환
     * 5. 페이징 처리로 성능 최적화
     */
    @GetMapping("/my")
    @Operation(summary = "내가 등록한 유기견 목록 조회 (페이징 o)", description = "로그인한 사용자가 등록한 유기견 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내가 등록한 유기견 목록 조회 성공")
    public ResponseEntity<List<DogListResponseDto>> getMyDogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @SessionMember SessionUser user) {

        List<DogListResponseDto> dogs = dogService.getMyDogs(page, size, user);
        return ResponseEntity.ok(dogs);
    }

    /**
     * 유기견 상세 조회 API
     *
     * @param dogId 조회할 유기견 ID
     * @return 유기견 상세 정보 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 요청된 ID로 유기견 정보 조회
     * 2. 존재하지 않는 경우 404 에러 반환
     * 3. 상세 정보 포함하여 반환
     */
    @GetMapping("/{dogId}")
    @Operation(summary = "유기견 상세 조회", description = "특정 유기견의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "유기견 상세 정보 조회 성공")
    public ResponseEntity<DogResponseDto> getDogById(
            @Parameter(description = "유기견 ID", example = "1")
            @PathVariable Long dogId) {

        DogResponseDto dog = dogService.getDogById(dogId);
        return ResponseEntity.ok(dog);
    }

    /**
     * 유기견 정보 수정 API
     *
     * @param dogId 수정할 유기견 ID
     * @param requestDto 유기견 수정 요청 데이터
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 수정된 유기견 정보 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 유기견 존재 여부 확인
     * 2. 권한 검증: 세션 사용자 ID와 유기견 등록자 ID 비교
     * 3. null이 아닌 필드만 선택적으로 업데이트
     * 4. 수정된 정보 반환
     */
    @PatchMapping("/{dogId}")
    @Operation(summary = "유기견 정보 수정", description = "등록한 유기견의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "유기견 정보 수정 성공")
    public ResponseEntity<DogResponseDto> updateDog(
            @Parameter(description = "유기견 ID", example = "1")
            @PathVariable Long dogId,
            @Valid @RequestBody DogUpdateRequestDto requestDto,
            @SessionMember SessionUser user) {

        DogResponseDto responseDto = dogService.updateDog(dogId, requestDto, user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 유기견 정보 삭제 API
     *
     * @param dogId 삭제할 유기견 ID
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 삭제 완료 응답 (204 No Content)
     *
     * 비즈니스 로직:
     * 1. 유기견 존재 여부 확인
     * 2. 권한 검증: 세션 사용자 ID와 유기견 등록자 ID 비교
     * 3. 데이터베이스에서 유기견 정보 삭제
     * 4. 성공 시 삭제된 유기견 정보와 함께 200 상태 코드 반환
     */
    @DeleteMapping("/{dogId}")
    @Operation(summary = "유기견 정보 삭제", description = "등록한 유기견의 정보를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "유기견 정보 삭제 성공")
    public ResponseEntity<DogDeleteResponseDto> deleteDog(
            @Parameter(description = "유기견 ID", example = "1")
            @PathVariable Long dogId,
            @SessionMember SessionUser user) {

        dogService.deleteDog(dogId, user);
        DogDeleteResponseDto responseDto = new DogDeleteResponseDto(dogId, user.getMemberId(), "유기견 정보 삭제 성공");
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 유기견 검색 API
     *
     * @param q 검색 키워드
     * @param dogSize 견종 크기 필터
     * @param breedName 견종명 필터
     * @param gender 성별 필터
     * @param location 지역 필터
     * @param adoptionStatus 입양 상태 필터
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 검색된 유기견 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. OpenSearch를 통한 키워드 검색
     * 2. 다양한 필터 조건 적용
     * 3. 페이징 처리된 결과 반환
     */
    @GetMapping("/search")
    @Operation(summary = "유기견 검색", description = "키워드와 필터를 통해 유기견을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "유기견 검색 성공")
    public ResponseEntity<List<DogDocument>> searchDogs(
            @Parameter(description = "검색 키워드", example = "말티즈")
            @RequestParam(required = false) String q,

            @Parameter(description = "견종 크기", example = "소형")
            @RequestParam(required = false) DogSize dogSize,

            @Parameter(description = "견종명", example = "말티즈")
            @RequestParam(required = false) String breedName,

            @Parameter(description = "성별", example = "MALE")
            @RequestParam(required = false) Gender gender,

            @Parameter(description = "지역", example = "서울")
            @RequestParam(required = false) String location,

            @Parameter(description = "입양 상태", example = "입양_가능")
            @RequestParam(required = false) AdoptionStatus adoptionStatus,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        List<DogDocument> response = dogSearchService.searchDogs(
            q, dogSize, breedName, gender, location, adoptionStatus, page, size
        );
        return ResponseEntity.ok(response);
    }
}