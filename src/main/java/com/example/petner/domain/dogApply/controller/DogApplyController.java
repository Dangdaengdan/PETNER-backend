package com.example.petner.domain.dogApply.controller;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.dto.request.DogApplyCreateRequestDto;
import com.example.petner.domain.dogApply.dto.request.DogApplyProcessRequestDto;
import com.example.petner.domain.dogApply.dto.response.*;
import com.example.petner.domain.dogApply.service.DogApplyService;
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
 * 유기견 분양 신청 컨트롤러
 * 유기견 분양 신청 CRUD 기능을 제공하는 REST API 컨트롤러
 */
@Tag(name = "유기견 분양 신청 (dog-applies)", description = "유기견 분양 신청 관련 API")
@RestController
@RequestMapping("/api/v1/dog-applies")
@RequiredArgsConstructor
public class DogApplyController {

    private final DogApplyService dogApplyService;

    /**
     * 분양 신청 생성 API
     *
     * @param requestDto 신청 요청 데이터
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 생성된 신청 정보 (201 Created)
     *
     * 비즈니스 로직:
     * 1. 세션에서 로그인 사용자 정보 자동 추출
     * 2. 유기견 존재 여부 및 입양 가능 상태 검증
     * 3. 자신의 유기견 신청 방지 및 중복 신청 방지
     * 4. 분양 신청 정보를 데이터베이스에 저장
     */
    @PostMapping
    @Operation(summary = "유기견 분양 신청", description = "특정 유기견에 대해 분양 신청을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "분양 신청 성공")
    public ResponseEntity<DogApplyCreateResponseDto> createApplication(
            @Valid @RequestBody DogApplyCreateRequestDto requestDto,
            @SessionMember SessionUser user) {

        DogApplyCreateResponseDto responseDto = dogApplyService.createApplication(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 분양 신청 처리 API (승인/거절)
     *
     * @param dogApplyId 처리할 신청 ID
     * @param requestDto 처리 요청 데이터 (승인/거절 및 메시지)
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 처리 결과 정보 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 신청 존재 여부 및 처리 권한 검증 (유기견 등록자만 가능)
     * 2. 신청 상태가 대기 중인지 확인
     * 3. 승인 또는 거절 처리 및 응답 메시지 저장
     * 4. 처리 시간 기록
     */
    @PatchMapping("/{dogApplyId}/process")
    @Operation(summary = "분양 신청 처리", description = "대기 중인 분양 신청을 승인 또는 거절합니다. (유기견 등록자만 가능)")
    @ApiResponse(responseCode = "200", description = "분양 신청 처리 성공")
    public ResponseEntity<DogApplyProcessResponseDto> processApplication(
            @Parameter(description = "분양 신청 ID", example = "1")
            @PathVariable Long dogApplyId,
            @Valid @RequestBody DogApplyProcessRequestDto requestDto,
            @SessionMember SessionUser user) {

        DogApplyProcessResponseDto responseDto = dogApplyService.processApplication(dogApplyId, requestDto, user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 분양 신청 삭제 API
     *
     * @param dogApplyId 삭제할 신청 ID
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 삭제 완료 응답 (204 No Content)
     *
     * 비즈니스 로직:
     * 1. 신청 존재 여부 및 삭제 권한 검증 (신청자만 가능)
     * 2. 신청 상태가 대기 중인지 확인 (처리된 신청은 삭제 불가)
     * 3. 데이터베이스에서 신청 정보 삭제
     */
    @DeleteMapping("/{dogApplyId}")
    @Operation(summary = "분양 신청 삭제", description = "대기 중인 분양 신청을 삭제합니다. (신청자만 가능)")
    @ApiResponse(responseCode = "204", description = "분양 신청 삭제 성공")
    public ResponseEntity<Void> deleteApplication(
            @Parameter(description = "분양 신청 ID", example = "1")
            @PathVariable Long dogApplyId,
            @SessionMember SessionUser user) {

        dogApplyService.deleteApplication(dogApplyId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 분양 신청 상세 조회 API
     *
     * @param dogApplyId 조회할 신청 ID
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 신청 상세 정보 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 신청 존재 여부 확인
     * 2. 조회 권한 검증 (신청자 또는 유기견 등록자만 가능)
     * 3. 강아지 정보, 신청자 정보, 처리 상태 등 상세 정보 반환
     */
    @GetMapping("/{dogApplyId}")
    @Operation(summary = "분양 신청 상세 조회", description = "특정 분양 신청의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "분양 신청 상세 조회 성공")
    public ResponseEntity<DogApplyResponseDto> getApplicationDetail(
            @Parameter(description = "분양 신청 ID", example = "1")
            @PathVariable Long dogApplyId,
            @SessionMember SessionUser user) {

        DogApplyResponseDto responseDto = dogApplyService.getApplicationDetail(dogApplyId, user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 내가 신청한 분양 신청 목록 조회 API (신청자 관점)
     *
     * @param page 페이지 번호 (선택, 기본값: 0)
     * @param size 페이지 크기 (선택, 기본값: 10)
     * @param status 상태 필터 (선택)
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 내가 신청한 분양 신청 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 세션 사용자 ID로 신청한 내역만 필터링
     * 2. 상태별 필터링 (선택적)
     * 3. N+1 문제 해결을 위한 페치 조인 적용
     * 4. 최신 신청순으로 정렬하여 반환
     */
    @GetMapping("/my")
    @Operation(summary = "내가 신청한 분양 신청 목록 조회", description = "로그인한 사용자가 신청한 분양 신청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 분양 신청 목록 조회 성공")
    public ResponseEntity<List<DogApplyListResponseDto>> getMyApplications(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "신청 상태 필터", example = "PENDING")
            @RequestParam(required = false) ApplyStatus status,

            @SessionMember SessionUser user) {

        List<DogApplyListResponseDto> responseDto = dogApplyService.getMyApplications(page, size, status, user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 내 유기견에 대한 분양 신청 목록 조회 API (등록자 관점)
     *
     * @param page 페이지 번호 (선택, 기본값: 0)
     * @param size 페이지 크기 (선택, 기본값: 10)
     * @param status 상태 필터 (선택)
     * @param user 세션에서 자동 주입되는 로그인 사용자 정보
     * @return 내 유기견에 대한 분양 신청 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 세션 사용자가 등록한 유기견에 대한 신청만 필터링
     * 2. 상태별 필터링 (선택적)
     * 3. N+1 문제 해결을 위한 페치 조인 적용
     * 4. 최신 신청순으로 정렬하여 반환
     */
    @GetMapping("/received")
    @Operation(summary = "내 유기견에 대한 분양 신청 목록 조회", description = "로그인한 사용자가 등록한 유기견에 대한 분양 신청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "받은 분양 신청 목록 조회 성공")
    public ResponseEntity<List<DogApplyListResponseDto>> getReceivedApplications(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "신청 상태 필터", example = "PENDING")
            @RequestParam(required = false) ApplyStatus status,

            @SessionMember SessionUser user) {

        List<DogApplyListResponseDto> responseDto = dogApplyService.getReceivedApplications(page, size, status, user);
        return ResponseEntity.ok(responseDto);
    }
}