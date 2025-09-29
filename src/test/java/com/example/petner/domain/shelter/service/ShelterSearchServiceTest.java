package com.example.petner.domain.shelter.service;

import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.shelter.dto.response.ShelterSearchResponseDto;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.shelter.repository.ShelterRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ShelterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelterSearchServiceTest {

    @Mock
    private ShelterRepository shelterRepository;

    @InjectMocks
    private ShelterSearchService shelterSearchService;

    private Shelter shelter;
    private Location location;

    @BeforeEach
    void setUp() {
        location = Location.builder()
                .state("서울시")
                .district("강남구")
                .build();
        ReflectionTestUtils.setField(location, "locationId", 1L);

        shelter = Shelter.builder()
                .name("서울시 강남구 동물보호센터")
                .detailAddress("서울시 강남구 테헤란로 123")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(shelter, "shelterId", 1L);
    }

    @Test
    @DisplayName("보호소 이름으로 검색 성공")
    void searchByName_Success() {
        // Given
        String shelterName = "서울시 강남구 동물보호센터";
        when(shelterRepository.findByName(shelterName)).thenReturn(Optional.of(shelter));

        // When
        ShelterSearchResponseDto result = shelterSearchService.searchByName(shelterName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShelterId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("서울시 강남구 동물보호센터");
        verify(shelterRepository).findByName(shelterName);
    }

    @Test
    @DisplayName("보호소 이름으로 검색 실패 - 존재하지 않는 보호소")
    void searchByName_NotFound() {
        // Given
        String nonExistentShelterName = "존재하지않는보호소";
        when(shelterRepository.findByName(nonExistentShelterName)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelterSearchService.searchByName(nonExistentShelterName))
                .isInstanceOf(ShelterException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHELTER_NOT_FOUND);

        verify(shelterRepository).findByName(nonExistentShelterName);
    }

    @Test
    @DisplayName("다양한 보호소 이름으로 검색 테스트")
    void searchByName_VariousNames() {
        // Given
        String[] shelterNames = {
                "부산시 동물보호센터",
                "대구시 해외동물보호센터",
                "인천시 서구 동물보호소",
                "광주시 남구 반려동물센터"
        };

        for (int i = 0; i < shelterNames.length; i++) {
            Location testLocation = Location.builder()
                    .state("테스트시")
                    .district("테스트구")
                    .build();
            ReflectionTestUtils.setField(testLocation, "locationId", (long) (i + 2));

            Shelter testShelter = Shelter.builder()
                    .name(shelterNames[i])
                    .detailAddress("테스트 주소")
                    .shelterContact("010-1234-567" + (i + 1))
                    .location(testLocation)
                    .build();
            ReflectionTestUtils.setField(testShelter, "shelterId", (long) (i + 2));

            when(shelterRepository.findByName(shelterNames[i])).thenReturn(Optional.of(testShelter));

            // When
            ShelterSearchResponseDto result = shelterSearchService.searchByName(shelterNames[i]);

            // Then
            assertThat(result.getShelterId()).isEqualTo((long) (i + 2));
            assertThat(result.getName()).isEqualTo(shelterNames[i]);
            verify(shelterRepository).findByName(shelterNames[i]);
        }
    }

    @Test
    @DisplayName("빈 문자열로 검색 시 예외 발생")
    void searchByName_EmptyString() {
        // Given
        String emptyName = "";
        when(shelterRepository.findByName(emptyName)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelterSearchService.searchByName(emptyName))
                .isInstanceOf(ShelterException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHELTER_NOT_FOUND);

        verify(shelterRepository).findByName(emptyName);
    }

    @Test
    @DisplayName("공백 문자열로 검색 시 예외 발생")
    void searchByName_BlankString() {
        // Given
        String blankName = "   ";
        when(shelterRepository.findByName(blankName)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shelterSearchService.searchByName(blankName))
                .isInstanceOf(ShelterException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHELTER_NOT_FOUND);

        verify(shelterRepository).findByName(blankName);
    }

    @Test
    @DisplayName("다른 이름으로 검색하여 구분 확인")
    void searchByName_DifferentNames() {
        // Given
        String originalName = "서울시 강남구 동물보호센터";
        String differentName = "부산시 동물보호센터";
        when(shelterRepository.findByName(originalName)).thenReturn(Optional.of(shelter));
        when(shelterRepository.findByName(differentName)).thenReturn(Optional.empty());

        // When - 정확한 이름으로 검색
        ShelterSearchResponseDto result1 = shelterSearchService.searchByName(originalName);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result1.getName()).isEqualTo(originalName);

        // When & Then - 다른 이름으로 검색
        assertThatThrownBy(() -> shelterSearchService.searchByName(differentName))
                .isInstanceOf(ShelterException.class);

        verify(shelterRepository).findByName(originalName);
        verify(shelterRepository).findByName(differentName);
    }

    @Test
    @DisplayName("Repository 호출 확인")
    void shelterRepository_CallVerification() {
        // Given
        String shelterName = "테스트보호소";
        when(shelterRepository.findByName(shelterName)).thenReturn(Optional.of(shelter));

        // When
        shelterSearchService.searchByName(shelterName);

        // Then
        verify(shelterRepository, times(1)).findByName(shelterName);
        verifyNoMoreInteractions(shelterRepository);
    }

    @Test
    @DisplayName("긴 이름의 보호소 검색")
    void searchByName_LongName() {
        // Given
        String longName = "매우긴이름을가진보호소입니다".repeat(5);
        Shelter longNameShelter = Shelter.builder()
                .name(longName)
                .detailAddress("테스트 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(longNameShelter, "shelterId", 999L);

        when(shelterRepository.findByName(longName)).thenReturn(Optional.of(longNameShelter));

        // When
        ShelterSearchResponseDto result = shelterSearchService.searchByName(longName);

        // Then
        assertThat(result.getShelterId()).isEqualTo(999L);
        assertThat(result.getName()).isEqualTo(longName);
        verify(shelterRepository).findByName(longName);
    }

    @Test
    @DisplayName("특수문자가 포함된 보호소 이름 검색")
    void searchByName_SpecialCharacters() {
        // Given
        String specialName = "서울시(강남구)동물보호센터&펫샵";
        Shelter specialShelter = Shelter.builder()
                .name(specialName)
                .detailAddress("특수문자 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(specialShelter, "shelterId", 888L);

        when(shelterRepository.findByName(specialName)).thenReturn(Optional.of(specialShelter));

        // When
        ShelterSearchResponseDto result = shelterSearchService.searchByName(specialName);

        // Then
        assertThat(result.getShelterId()).isEqualTo(888L);
        assertThat(result.getName()).isEqualTo(specialName);
        verify(shelterRepository).findByName(specialName);
    }

    @Test
    @DisplayName("숫자가 포함된 보호소 이름 검색")
    void searchByName_WithNumbers() {
        // Given
        String nameWithNumbers = "서울시 제1동물보호센터 2024";
        Shelter numberShelter = Shelter.builder()
                .name(nameWithNumbers)
                .detailAddress("숫자포함 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(numberShelter, "shelterId", 777L);

        when(shelterRepository.findByName(nameWithNumbers)).thenReturn(Optional.of(numberShelter));

        // When
        ShelterSearchResponseDto result = shelterSearchService.searchByName(nameWithNumbers);

        // Then
        assertThat(result.getShelterId()).isEqualTo(777L);
        assertThat(result.getName()).isEqualTo(nameWithNumbers);
        verify(shelterRepository).findByName(nameWithNumbers);
    }
}