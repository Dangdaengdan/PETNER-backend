package com.example.petner.domain.dogApply.controller;

import com.example.petner.domain.dogApply.common.ApplyStatus;
import com.example.petner.domain.dogApply.dto.request.DogApplyCreateRequestDto;
import com.example.petner.domain.dogApply.dto.request.DogApplyProcessRequestDto;
import com.example.petner.domain.dogApply.dto.response.*;
import com.example.petner.domain.dogApply.service.DogApplyService;
import com.example.petner.global.dto.SessionUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogApplyControllerTest {

    @Mock
    private DogApplyService dogApplyService;

    @InjectMocks
    private DogApplyController dogApplyController;

    private SessionUser sessionUser;
    private DogApplyCreateRequestDto createRequestDto;
    private DogApplyProcessRequestDto processRequestDto;
    private DogApplyCreateResponseDto createResponseDto;
    private DogApplyProcessResponseDto processResponseDto;
    private DogApplyResponseDto responseDto;
    private List<DogApplyListResponseDto> listResponseDto;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("user@example.com")
                .nickname("사용자")
                .build();

        createRequestDto = new DogApplyCreateRequestDto(1L);
        processRequestDto = new DogApplyProcessRequestDto(ApplyStatus.APPROVED);

        createResponseDto = DogApplyCreateResponseDto.builder()
                .dogApplyId(1L)
                .dogId(1L)
                .applicantId(1L)
                .status(ApplyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        processResponseDto = DogApplyProcessResponseDto.builder()
                .dogApplyId(1L)
                .status(ApplyStatus.APPROVED)
                .processedAt(LocalDateTime.now())
                .build();

        responseDto = DogApplyResponseDto.builder()
                .dogApplyId(1L)
                .dog(DogApplyResponseDto.DogInfo.builder()
                        .dogId(1L)
                        .name("바둑이")
                        .breedName("믹스견")
                        .build())
                .applicant(DogApplyResponseDto.ApplicantInfo.builder()
                        .memberId(1L)
                        .nickname("신청자")
                        .email("applicant@example.com")
                        .build())
                .status(ApplyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        DogApplyListResponseDto listItem = DogApplyListResponseDto.builder()
                .dogApplyId(1L)
                .dogId(1L)
                .dogName("바둑이")
                .breedName("믹스견")
                .status(ApplyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        listResponseDto = Arrays.asList(listItem);
    }

    @Test
    @DisplayName("분양 신청 생성 API 성공")
    void createApplication_Success() {
        // Given
        when(dogApplyService.createApplication(createRequestDto, sessionUser))
                .thenReturn(createResponseDto);

        // When
        ResponseEntity<DogApplyCreateResponseDto> response =
                dogApplyController.createApplication(createRequestDto, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(createResponseDto);
        assertThat(response.getBody().getDogApplyId()).isEqualTo(1L);
        assertThat(response.getBody().getStatus()).isEqualTo(ApplyStatus.PENDING);

        verify(dogApplyService).createApplication(createRequestDto, sessionUser);
    }

    @Test
    @DisplayName("분양 신청 처리 API 성공")
    void processApplication_Success() {
        // Given
        Long dogApplyId = 1L;
        when(dogApplyService.processApplication(dogApplyId, processRequestDto, sessionUser))
                .thenReturn(processResponseDto);

        // When
        ResponseEntity<DogApplyProcessResponseDto> response =
                dogApplyController.processApplication(dogApplyId, processRequestDto, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(processResponseDto);
        assertThat(response.getBody().getDogApplyId()).isEqualTo(1L);
        assertThat(response.getBody().getStatus()).isEqualTo(ApplyStatus.APPROVED);

        verify(dogApplyService).processApplication(dogApplyId, processRequestDto, sessionUser);
    }

    @Test
    @DisplayName("분양 신청 삭제 API 성공")
    void deleteApplication_Success() {
        // Given
        Long dogApplyId = 1L;

        // When
        ResponseEntity<Void> response = dogApplyController.deleteApplication(dogApplyId, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(dogApplyService).deleteApplication(dogApplyId, sessionUser);
    }

    @Test
    @DisplayName("분양 신청 상세 조회 API 성공")
    void getApplicationDetail_Success() {
        // Given
        Long dogApplyId = 1L;
        when(dogApplyService.getApplicationDetail(dogApplyId, sessionUser))
                .thenReturn(responseDto);

        // When
        ResponseEntity<DogApplyResponseDto> response =
                dogApplyController.getApplicationDetail(dogApplyId, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);
        assertThat(response.getBody().getDogApplyId()).isEqualTo(1L);
        assertThat(response.getBody().getStatus()).isEqualTo(ApplyStatus.PENDING);

        verify(dogApplyService).getApplicationDetail(dogApplyId, sessionUser);
    }

    @Test
    @DisplayName("내가 신청한 분양 신청 목록 조회 API 성공 - 상태 필터 없음")
    void getMyApplications_Success_NoStatusFilter() {
        // Given
        int page = 0;
        int size = 10;
        when(dogApplyService.getMyApplications(page, size, null, sessionUser))
                .thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogApplyListResponseDto>> response =
                dogApplyController.getMyApplications(page, size, null, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listResponseDto);
        assertThat(response.getBody()).hasSize(1);

        verify(dogApplyService).getMyApplications(page, size, null, sessionUser);
    }

    @Test
    @DisplayName("내가 신청한 분양 신청 목록 조회 API 성공 - 상태 필터 있음")
    void getMyApplications_Success_WithStatusFilter() {
        // Given
        int page = 0;
        int size = 10;
        ApplyStatus status = ApplyStatus.PENDING;
        when(dogApplyService.getMyApplications(page, size, status, sessionUser))
                .thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogApplyListResponseDto>> response =
                dogApplyController.getMyApplications(page, size, status, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listResponseDto);
        assertThat(response.getBody()).hasSize(1);

        verify(dogApplyService).getMyApplications(page, size, status, sessionUser);
    }

    @Test
    @DisplayName("내 유기견에 대한 분양 신청 목록 조회 API 성공 - 상태 필터 없음")
    void getReceivedApplications_Success_NoStatusFilter() {
        // Given
        int page = 0;
        int size = 10;
        when(dogApplyService.getReceivedApplications(page, size, null, sessionUser))
                .thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogApplyListResponseDto>> response =
                dogApplyController.getReceivedApplications(page, size, null, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listResponseDto);
        assertThat(response.getBody()).hasSize(1);

        verify(dogApplyService).getReceivedApplications(page, size, null, sessionUser);
    }

    @Test
    @DisplayName("내 유기견에 대한 분양 신청 목록 조회 API 성공 - 상태 필터 있음")
    void getReceivedApplications_Success_WithStatusFilter() {
        // Given
        int page = 1;
        int size = 20;
        ApplyStatus status = ApplyStatus.APPROVED;
        when(dogApplyService.getReceivedApplications(page, size, status, sessionUser))
                .thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogApplyListResponseDto>> response =
                dogApplyController.getReceivedApplications(page, size, status, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listResponseDto);

        verify(dogApplyService).getReceivedApplications(page, size, status, sessionUser);
    }

    @Test
    @DisplayName("기본 페이징 파라미터 확인")
    void defaultPagingParameters() {
        // Given
        when(dogApplyService.getMyApplications(0, 10, null, sessionUser))
                .thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogApplyListResponseDto>> response =
                dogApplyController.getMyApplications(0, 10, null, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(dogApplyService).getMyApplications(0, 10, null, sessionUser);
    }

    @Test
    @DisplayName("빈 목록 응답 처리")
    void emptyListResponse() {
        // Given
        when(dogApplyService.getMyApplications(0, 10, null, sessionUser))
                .thenReturn(List.of());

        // When
        ResponseEntity<List<DogApplyListResponseDto>> response =
                dogApplyController.getMyApplications(0, 10, null, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        verify(dogApplyService).getMyApplications(0, 10, null, sessionUser);
    }

    @Test
    @DisplayName("다양한 ApplyStatus 처리 확인")
    void variousApplyStatusHandling() {
        // Given
        ApplyStatus[] statuses = {ApplyStatus.PENDING, ApplyStatus.APPROVED, ApplyStatus.REJECTED};

        for (ApplyStatus status : statuses) {
            when(dogApplyService.getMyApplications(0, 10, status, sessionUser))
                    .thenReturn(listResponseDto);

            // When
            ResponseEntity<List<DogApplyListResponseDto>> response =
                    dogApplyController.getMyApplications(0, 10, status, sessionUser);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(dogApplyService).getMyApplications(0, 10, status, sessionUser);
        }
    }

    @Test
    @DisplayName("서비스 호출 순서 확인")
    void serviceCallOrder() {
        // Given
        Long dogApplyId = 1L;
        when(dogApplyService.getApplicationDetail(dogApplyId, sessionUser))
                .thenReturn(responseDto);

        // When
        dogApplyController.getApplicationDetail(dogApplyId, sessionUser);

        // Then
        verify(dogApplyService, times(1)).getApplicationDetail(dogApplyId, sessionUser);
        verifyNoMoreInteractions(dogApplyService);
    }
}