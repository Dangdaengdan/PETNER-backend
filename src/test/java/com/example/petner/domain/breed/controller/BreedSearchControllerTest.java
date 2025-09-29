package com.example.petner.domain.breed.controller;

import com.example.petner.domain.breed.dto.response.BreedSearchResponseDto;
import com.example.petner.domain.breed.service.BreedSearchService;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BreedSearchControllerTest {

    @Mock
    private BreedSearchService breedSearchService;

    @InjectMocks
    private BreedSearchController breedSearchController;

    private BreedSearchResponseDto breedSearchResponseDto;

    @BeforeEach
    void setUp() {
        breedSearchResponseDto = createMockBreedSearchResponseDto();
    }

    @Test
    @DisplayName("견종 이름으로 조회 성공")
    void searchBreedByName_Success() {
        // Given
        String breedName = "골든리트리버";
        when(breedSearchService.searchByName(breedName)).thenReturn(breedSearchResponseDto);

        // When
        ResponseEntity<BreedSearchResponseDto> response = breedSearchController.searchBreedByName(breedName);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(breedSearchResponseDto.getBreedId(), response.getBody().getBreedId());
        assertEquals(breedSearchResponseDto.getName(), response.getBody().getName());

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 실패 - 견종 없음")
    void searchBreedByName_NotFound() {
        // Given
        String breedName = "존재하지않는견종";
        when(breedSearchService.searchByName(breedName))
                .thenThrow(new DogException(ErrorCode.DOG_BREED_NOT_FOUND));

        // When & Then
        assertThrows(DogException.class,
                () -> breedSearchController.searchBreedByName(breedName));

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 빈 문자열")
    void searchBreedByName_EmptyString() {
        // Given
        String breedName = "";
        when(breedSearchService.searchByName(breedName))
                .thenThrow(new DogException(ErrorCode.DOG_BREED_NOT_FOUND));

        // When & Then
        assertThrows(DogException.class,
                () -> breedSearchController.searchBreedByName(breedName));

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - null 값")
    void searchBreedByName_NullValue() {
        // Given
        String breedName = null;
        when(breedSearchService.searchByName(breedName))
                .thenThrow(new DogException(ErrorCode.DOG_BREED_NOT_FOUND));

        // When & Then
        assertThrows(DogException.class,
                () -> breedSearchController.searchBreedByName(breedName));

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 공백 문자열")
    void searchBreedByName_WhitespaceString() {
        // Given
        String breedName = "   ";
        when(breedSearchService.searchByName(breedName))
                .thenThrow(new DogException(ErrorCode.DOG_BREED_NOT_FOUND));

        // When & Then
        assertThrows(DogException.class,
                () -> breedSearchController.searchBreedByName(breedName));

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 대소문자 혼합")
    void searchBreedByName_MixedCase() {
        // Given
        String breedName = "골든리트리버";
        when(breedSearchService.searchByName(breedName)).thenReturn(breedSearchResponseDto);

        // When
        ResponseEntity<BreedSearchResponseDto> response = breedSearchController.searchBreedByName(breedName);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(breedSearchResponseDto.getBreedId(), response.getBody().getBreedId());
        assertEquals(breedSearchResponseDto.getName(), response.getBody().getName());

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 긴 견종 이름")
    void searchBreedByName_LongBreedName() {
        // Given
        String breedName = "아메리칸스태퍼드셔테리어";
        BreedSearchResponseDto longNameDto = BreedSearchResponseDto.builder()
                .breedId(100L)
                .name(breedName)
                .build();
        when(breedSearchService.searchByName(breedName)).thenReturn(longNameDto);

        // When
        ResponseEntity<BreedSearchResponseDto> response = breedSearchController.searchBreedByName(breedName);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().getBreedId());
        assertEquals(breedName, response.getBody().getName());

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 다양한 견종들")
    void searchBreedByName_VariousBreeds() {
        // Given
        String[] breedNames = {"푸들", "말티즈", "진돗개", "시바견", "코기"};

        for (int i = 0; i < breedNames.length; i++) {
            String breedName = breedNames[i];
            BreedSearchResponseDto dto = BreedSearchResponseDto.builder()
                    .breedId((long) (i + 1))
                    .name(breedName)
                    .build();
            when(breedSearchService.searchByName(breedName)).thenReturn(dto);

            // When
            ResponseEntity<BreedSearchResponseDto> response = breedSearchController.searchBreedByName(breedName);

            // Then
            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());
            assertEquals((long) (i + 1), response.getBody().getBreedId());
            assertEquals(breedName, response.getBody().getName());
        }

        verify(breedSearchService, times(breedNames.length)).searchByName(any(String.class));
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 서비스 예외 처리")
    void searchBreedByName_ServiceException() {
        // Given
        String breedName = "골든리트리버";
        when(breedSearchService.searchByName(breedName))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> breedSearchController.searchBreedByName(breedName));

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 높은 ID 값")
    void searchBreedByName_HighId() {
        // Given
        String breedName = "골든리트리버";
        BreedSearchResponseDto highIdDto = BreedSearchResponseDto.builder()
                .breedId(Long.MAX_VALUE)
                .name(breedName)
                .build();
        when(breedSearchService.searchByName(breedName)).thenReturn(highIdDto);

        // When
        ResponseEntity<BreedSearchResponseDto> response = breedSearchController.searchBreedByName(breedName);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(Long.MAX_VALUE, response.getBody().getBreedId());
        assertEquals(breedName, response.getBody().getName());

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 특수문자 포함")
    void searchBreedByName_WithSpecialCharacters() {
        // Given
        String breedName = "세인트 버나드";
        BreedSearchResponseDto specialCharDto = BreedSearchResponseDto.builder()
                .breedId(50L)
                .name(breedName)
                .build();
        when(breedSearchService.searchByName(breedName)).thenReturn(specialCharDto);

        // When
        ResponseEntity<BreedSearchResponseDto> response = breedSearchController.searchBreedByName(breedName);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(50L, response.getBody().getBreedId());
        assertEquals(breedName, response.getBody().getName());

        verify(breedSearchService).searchByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - ResponseEntity 타입 검증")
    void searchBreedByName_ResponseEntityType() {
        // Given
        String breedName = "골든리트리버";
        when(breedSearchService.searchByName(breedName)).thenReturn(breedSearchResponseDto);

        // When
        ResponseEntity<BreedSearchResponseDto> response = breedSearchController.searchBreedByName(breedName);

        // Then
        assertTrue(response instanceof ResponseEntity);
        assertTrue(response.getBody() instanceof BreedSearchResponseDto);
        assertEquals(200, response.getStatusCodeValue());

        verify(breedSearchService).searchByName(breedName);
    }

    // Helper methods
    private BreedSearchResponseDto createMockBreedSearchResponseDto() {
        return BreedSearchResponseDto.builder()
                .breedId(1L)
                .name("골든리트리버")
                .build();
    }
}