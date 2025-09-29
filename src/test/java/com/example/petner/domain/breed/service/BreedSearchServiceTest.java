package com.example.petner.domain.breed.service;

import com.example.petner.domain.breed.dto.response.BreedSearchResponseDto;
import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.breed.repository.BreedRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BreedSearchServiceTest {

    @Mock
    private BreedRepository breedRepository;

    @InjectMocks
    private BreedSearchService breedSearchService;

    private Breed mockBreed;

    @BeforeEach
    void setUp() {
        mockBreed = createMockBreed();
    }

    @Test
    @DisplayName("견종 이름으로 조회 성공")
    void searchByName_Success() {
        // Given
        String breedName = "골든리트리버";
        when(breedRepository.findByName(breedName)).thenReturn(Optional.of(mockBreed));

        // When
        BreedSearchResponseDto result = breedSearchService.searchByName(breedName);

        // Then
        assertNotNull(result);
        assertEquals(mockBreed.getBreedId(), result.getBreedId());
        assertEquals(mockBreed.getName(), result.getName());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 실패 - 견종 없음")
    void searchByName_NotFound() {
        // Given
        String breedName = "존재하지않는견종";
        when(breedRepository.findByName(breedName)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> breedSearchService.searchByName(breedName));

        assertEquals(ErrorCode.DOG_BREED_NOT_FOUND, exception.getErrorCode());
        assertNotNull(exception.getMessage());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - null 값")
    void searchByName_NullValue() {
        // Given
        String breedName = null;
        when(breedRepository.findByName(breedName)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> breedSearchService.searchByName(breedName));

        assertEquals(ErrorCode.DOG_BREED_NOT_FOUND, exception.getErrorCode());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 빈 문자열")
    void searchByName_EmptyString() {
        // Given
        String breedName = "";
        when(breedRepository.findByName(breedName)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> breedSearchService.searchByName(breedName));

        assertEquals(ErrorCode.DOG_BREED_NOT_FOUND, exception.getErrorCode());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 공백 문자열")
    void searchByName_WhitespaceString() {
        // Given
        String breedName = "   ";
        when(breedRepository.findByName(breedName)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> breedSearchService.searchByName(breedName));

        assertEquals(ErrorCode.DOG_BREED_NOT_FOUND, exception.getErrorCode());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 다양한 견종들")
    void searchByName_VariousBreeds() {
        // Given
        String[] breedNames = {"푸들", "말티즈", "진돗개", "시바견", "코기"};

        for (int i = 0; i < breedNames.length; i++) {
            String breedName = breedNames[i];
            Breed breed = Breed.builder()
                    .name(breedName)
                    .build();
            // breedId를 설정하기 위해 리플렉션 사용
            setBreedId(breed, (long) (i + 1));

            when(breedRepository.findByName(breedName)).thenReturn(Optional.of(breed));

            // When
            BreedSearchResponseDto result = breedSearchService.searchByName(breedName);

            // Then
            assertNotNull(result);
            assertEquals((long) (i + 1), result.getBreedId());
            assertEquals(breedName, result.getName());
        }

        verify(breedRepository, times(breedNames.length)).findByName(any(String.class));
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 긴 견종 이름")
    void searchByName_LongBreedName() {
        // Given
        String longBreedName = "아메리칸스태퍼드셔테리어";
        Breed longNameBreed = Breed.builder()
                .name(longBreedName)
                .build();
        setBreedId(longNameBreed, 100L);

        when(breedRepository.findByName(longBreedName)).thenReturn(Optional.of(longNameBreed));

        // When
        BreedSearchResponseDto result = breedSearchService.searchByName(longBreedName);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getBreedId());
        assertEquals(longBreedName, result.getName());

        verify(breedRepository).findByName(longBreedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 특수문자 포함")
    void searchByName_WithSpecialCharacters() {
        // Given
        String specialCharBreedName = "세인트 버나드";
        Breed specialCharBreed = Breed.builder()
                .name(specialCharBreedName)
                .build();
        setBreedId(specialCharBreed, 50L);

        when(breedRepository.findByName(specialCharBreedName)).thenReturn(Optional.of(specialCharBreed));

        // When
        BreedSearchResponseDto result = breedSearchService.searchByName(specialCharBreedName);

        // Then
        assertNotNull(result);
        assertEquals(50L, result.getBreedId());
        assertEquals(specialCharBreedName, result.getName());

        verify(breedRepository).findByName(specialCharBreedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 높은 ID 값")
    void searchByName_HighId() {
        // Given
        String breedName = "골든리트리버";
        Breed highIdBreed = Breed.builder()
                .name(breedName)
                .build();
        setBreedId(highIdBreed, Long.MAX_VALUE);

        when(breedRepository.findByName(breedName)).thenReturn(Optional.of(highIdBreed));

        // When
        BreedSearchResponseDto result = breedSearchService.searchByName(breedName);

        // Then
        assertNotNull(result);
        assertEquals(Long.MAX_VALUE, result.getBreedId());
        assertEquals(breedName, result.getName());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 저장소 예외 처리")
    void searchByName_RepositoryException() {
        // Given
        String breedName = "골든리트리버";
        when(breedRepository.findByName(breedName))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> breedSearchService.searchByName(breedName));

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 정확한 이름 매칭")
    void searchByName_ExactNameMatching() {
        // Given
        String exactName = "골든리트리버";
        String differentName = "골든 리트리버"; // 공백 포함된 다른 이름

        when(breedRepository.findByName(exactName)).thenReturn(Optional.of(mockBreed));
        when(breedRepository.findByName(differentName)).thenReturn(Optional.empty());

        // When
        BreedSearchResponseDto result1 = breedSearchService.searchByName(exactName);

        // Then
        assertNotNull(result1);
        assertEquals(mockBreed.getBreedId(), result1.getBreedId());

        // When & Then for different name
        assertThrows(DogException.class,
                () -> breedSearchService.searchByName(differentName));

        verify(breedRepository).findByName(exactName);
        verify(breedRepository).findByName(differentName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 반환 타입 검증")
    void searchByName_ReturnType() {
        // Given
        String breedName = "골든리트리버";
        when(breedRepository.findByName(breedName)).thenReturn(Optional.of(mockBreed));

        // When
        BreedSearchResponseDto result = breedSearchService.searchByName(breedName);

        // Then
        assertTrue(result instanceof BreedSearchResponseDto);
        assertNotNull(result.getBreedId());
        assertNotNull(result.getName());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 트랜잭션 읽기 전용 테스트")
    void searchByName_ReadOnlyTransaction() {
        // Given
        String breedName = "골든리트리버";
        when(breedRepository.findByName(breedName)).thenReturn(Optional.of(mockBreed));

        // When
        BreedSearchResponseDto result = breedSearchService.searchByName(breedName);

        // Then
        assertNotNull(result);
        // 읽기 전용 트랜잭션이므로 저장소에서 한 번만 조회
        verify(breedRepository, times(1)).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 0 ID 값")
    void searchByName_ZeroId() {
        // Given
        String breedName = "골든리트리버";
        Breed zeroIdBreed = Breed.builder()
                .name(breedName)
                .build();
        setBreedId(zeroIdBreed, 0L);

        when(breedRepository.findByName(breedName)).thenReturn(Optional.of(zeroIdBreed));

        // When
        BreedSearchResponseDto result = breedSearchService.searchByName(breedName);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getBreedId());
        assertEquals(breedName, result.getName());

        verify(breedRepository).findByName(breedName);
    }

    @Test
    @DisplayName("견종 이름으로 조회 - 예외 메시지 검증")
    void searchByName_ExceptionMessage() {
        // Given
        String breedName = "존재하지않는견종";
        when(breedRepository.findByName(breedName)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> breedSearchService.searchByName(breedName));

        assertEquals(ErrorCode.DOG_BREED_NOT_FOUND, exception.getErrorCode());
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().length() > 0);

        verify(breedRepository).findByName(breedName);
    }

    // Helper methods
    private Breed createMockBreed() {
        Breed breed = Breed.builder()
                .name("골든리트리버")
                .build();
        setBreedId(breed, 1L);
        return breed;
    }

    private void setBreedId(Breed breed, Long id) {
        try {
            java.lang.reflect.Field field = Breed.class.getDeclaredField("breedId");
            field.setAccessible(true);
            field.set(breed, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set breedId", e);
        }
    }
}