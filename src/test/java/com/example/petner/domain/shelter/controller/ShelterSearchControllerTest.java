package com.example.petner.domain.shelter.controller;

import com.example.petner.domain.shelter.dto.response.ShelterSearchResponseDto;
import com.example.petner.domain.shelter.service.ShelterSearchService;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ShelterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelterSearchControllerTest {

    @Mock
    private ShelterSearchService shelterSearchService;

    @InjectMocks
    private ShelterSearchController shelterSearchController;

    private ShelterSearchResponseDto shelterSearchResponseDto;

    @BeforeEach
    void setUp() {
        shelterSearchResponseDto = ShelterSearchResponseDto.builder()
                .shelterId(1L)
                .name("서울시 강남구 동물보호센터")
                .build();
    }

    @Test
    @DisplayName("보호소 이름으로 검색 성공")
    void searchShelterByName_Success() {
        // Given
        String shelterName = "서울시 강남구 동물보호센터";
        when(shelterSearchService.searchByName(shelterName)).thenReturn(shelterSearchResponseDto);

        // When
        ResponseEntity<ShelterSearchResponseDto> result =
                shelterSearchController.searchShelterByName(shelterName);

        // Then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getShelterId()).isEqualTo(1L);
        assertThat(result.getBody().getName()).isEqualTo("서울시 강남구 동물보호센터");
        verify(shelterSearchService).searchByName(shelterName);
    }

    @Test
    @DisplayName("보호소 검색 실패 - 존재하지 않는 보호소")
    void searchShelterByName_NotFound() {
        // Given
        String nonExistentShelterName = "존재하지않는보호소";
        when(shelterSearchService.searchByName(nonExistentShelterName))
                .thenThrow(new ShelterException(ErrorCode.SHELTER_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> shelterSearchController.searchShelterByName(nonExistentShelterName))
                .isInstanceOf(ShelterException.class);

        verify(shelterSearchService).searchByName(nonExistentShelterName);
    }

    @Test
    @DisplayName("다양한 보호소 이름으로 검색 테스트")
    void searchShelterByName_VariousNames() {
        // Given
        String[] shelterNames = {
                "부산시 동물보호센터",
                "대구시 해외동물보호센터",
                "인천시 서구 동물보호소",
                "광주시 남구 반려동물센터"
        };

        for (int i = 0; i < shelterNames.length; i++) {
            ShelterSearchResponseDto responseDto = ShelterSearchResponseDto.builder()
                    .shelterId((long) (i + 2))
                    .name(shelterNames[i])
                    .build();

            when(shelterSearchService.searchByName(shelterNames[i])).thenReturn(responseDto);

            // When
            ResponseEntity<ShelterSearchResponseDto> result =
                    shelterSearchController.searchShelterByName(shelterNames[i]);

            // Then
            assertThat(result.getStatusCodeValue()).isEqualTo(200);
            assertThat(result.getBody().getShelterId()).isEqualTo((long) (i + 2));
            assertThat(result.getBody().getName()).isEqualTo(shelterNames[i]);

            verify(shelterSearchService).searchByName(shelterNames[i]);
        }
    }

    @Test
    @DisplayName("빈 문자열로 보호소 검색")
    void searchShelterByName_EmptyString() {
        // Given
        String emptyName = "";
        when(shelterSearchService.searchByName(emptyName))
                .thenThrow(new ShelterException(ErrorCode.SHELTER_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> shelterSearchController.searchShelterByName(emptyName))
                .isInstanceOf(ShelterException.class);

        verify(shelterSearchService).searchByName(emptyName);
    }

    @Test
    @DisplayName("공백 문자열로 보호소 검색")
    void searchShelterByName_BlankString() {
        // Given
        String blankName = "   ";
        when(shelterSearchService.searchByName(blankName))
                .thenThrow(new ShelterException(ErrorCode.SHELTER_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> shelterSearchController.searchShelterByName(blankName))
                .isInstanceOf(ShelterException.class);

        verify(shelterSearchService).searchByName(blankName);
    }

    @Test
    @DisplayName("한글 보호소 이름으로 검색")
    void searchShelterByName_KoreanName() {
        // Given
        String koreanName = "서울특별시동물보호센터";
        ShelterSearchResponseDto koreanResponseDto = ShelterSearchResponseDto.builder()
                .shelterId(100L)
                .name(koreanName)
                .build();

        when(shelterSearchService.searchByName(koreanName)).thenReturn(koreanResponseDto);

        // When
        ResponseEntity<ShelterSearchResponseDto> result =
                shelterSearchController.searchShelterByName(koreanName);

        // Then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getShelterId()).isEqualTo(100L);
        assertThat(result.getBody().getName()).isEqualTo(koreanName);
        verify(shelterSearchService).searchByName(koreanName);
    }

    @Test
    @DisplayName("특수문자가 포함된 보호소 이름으로 검색")
    void searchShelterByName_SpecialCharacters() {
        // Given
        String specialName = "서울시(강남구)동물보호센터&펫샵";
        ShelterSearchResponseDto specialResponseDto = ShelterSearchResponseDto.builder()
                .shelterId(200L)
                .name(specialName)
                .build();

        when(shelterSearchService.searchByName(specialName)).thenReturn(specialResponseDto);

        // When
        ResponseEntity<ShelterSearchResponseDto> result =
                shelterSearchController.searchShelterByName(specialName);

        // Then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getShelterId()).isEqualTo(200L);
        assertThat(result.getBody().getName()).isEqualTo(specialName);
        verify(shelterSearchService).searchByName(specialName);
    }

    @Test
    @DisplayName("숫자가 포함된 보호소 이름으로 검색")
    void searchShelterByName_WithNumbers() {
        // Given
        String nameWithNumbers = "서울시 제1동물보호센터 2024";
        ShelterSearchResponseDto numberResponseDto = ShelterSearchResponseDto.builder()
                .shelterId(300L)
                .name(nameWithNumbers)
                .build();

        when(shelterSearchService.searchByName(nameWithNumbers)).thenReturn(numberResponseDto);

        // When
        ResponseEntity<ShelterSearchResponseDto> result =
                shelterSearchController.searchShelterByName(nameWithNumbers);

        // Then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getShelterId()).isEqualTo(300L);
        assertThat(result.getBody().getName()).isEqualTo(nameWithNumbers);
        verify(shelterSearchService).searchByName(nameWithNumbers);
    }

    @Test
    @DisplayName("긴 이름의 보호소 검색")
    void searchShelterByName_LongName() {
        // Given
        String longName = "매우긴이름을가진보호소입니다".repeat(3);
        ShelterSearchResponseDto longResponseDto = ShelterSearchResponseDto.builder()
                .shelterId(400L)
                .name(longName)
                .build();

        when(shelterSearchService.searchByName(longName)).thenReturn(longResponseDto);

        // When
        ResponseEntity<ShelterSearchResponseDto> result =
                shelterSearchController.searchShelterByName(longName);

        // Then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getShelterId()).isEqualTo(400L);
        assertThat(result.getBody().getName()).isEqualTo(longName);
        verify(shelterSearchService).searchByName(longName);
    }

    @Test
    @DisplayName("서비스 호출 확인")
    void shelterSearchService_CallVerification() {
        // Given
        String shelterName = "테스트보호소";
        ShelterSearchResponseDto testResponseDto = ShelterSearchResponseDto.builder()
                .shelterId(999L)
                .name(shelterName)
                .build();

        when(shelterSearchService.searchByName(shelterName)).thenReturn(testResponseDto);

        // When
        ResponseEntity<ShelterSearchResponseDto> result =
                shelterSearchController.searchShelterByName(shelterName);

        // Then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(shelterSearchService, times(1)).searchByName(shelterName);
        verifyNoMoreInteractions(shelterSearchService);
    }

    @Test
    @DisplayName("ResponseEntity 응답 상태 확인")
    void responseEntity_StatusCheck() {
        // Given
        String shelterName = "상태확인보호소";
        when(shelterSearchService.searchByName(shelterName)).thenReturn(shelterSearchResponseDto);

        // When
        ResponseEntity<ShelterSearchResponseDto> result =
                shelterSearchController.searchShelterByName(shelterName);

        // Then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(shelterSearchResponseDto);
        verify(shelterSearchService).searchByName(shelterName);
    }
}