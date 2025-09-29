package com.example.petner.domain.breed.dto.response;

import com.example.petner.domain.breed.entity.Breed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BreedSearchResponseDtoTest {

    private Breed mockBreed;

    @BeforeEach
    void setUp() {
        mockBreed = createMockBreed();
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트")
    void createBreedSearchResponseDto_Success() {
        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(mockBreed);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getBreedId());
        assertEquals("골든리트리버", dto.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto Builder 테스트")
    void createBreedSearchResponseDto_Builder() {
        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.builder()
                .breedId(2L)
                .name("푸들")
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(2L, dto.getBreedId());
        assertEquals("푸들", dto.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(mockBreed);

        // When & Then
        assertEquals(1L, dto.getBreedId());
        assertEquals("골든리트리버", dto.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto from 정적 메서드 테스트")
    void testFromStaticMethod() {
        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(mockBreed);

        // Then
        assertNotNull(dto);
        assertEquals(mockBreed.getBreedId(), dto.getBreedId());
        assertEquals(mockBreed.getName(), dto.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 긴 견종 이름")
    void createBreedSearchResponseDto_LongBreedName() {
        // Given
        String longBreedName = "아메리칸스태퍼드셔테리어";
        Breed longNameBreed = Breed.builder()
                .name(longBreedName)
                .build();
        setBreedId(longNameBreed, 100L);

        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(longNameBreed);

        // Then
        assertNotNull(dto);
        assertEquals(100L, dto.getBreedId());
        assertEquals(longBreedName, dto.getName());
        assertTrue(dto.getName().length() > 10);
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 짧은 견종 이름")
    void createBreedSearchResponseDto_ShortBreedName() {
        // Given
        String shortBreedName = "진";
        Breed shortNameBreed = Breed.builder()
                .name(shortBreedName)
                .build();
        setBreedId(shortNameBreed, 5L);

        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(shortNameBreed);

        // Then
        assertNotNull(dto);
        assertEquals(5L, dto.getBreedId());
        assertEquals(shortBreedName, dto.getName());
        assertEquals(1, dto.getName().length());
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 특수문자 포함")
    void createBreedSearchResponseDto_WithSpecialCharacters() {
        // Given
        String specialCharBreedName = "세인트 버나드";
        Breed specialCharBreed = Breed.builder()
                .name(specialCharBreedName)
                .build();
        setBreedId(specialCharBreed, 50L);

        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(specialCharBreed);

        // Then
        assertNotNull(dto);
        assertEquals(50L, dto.getBreedId());
        assertEquals(specialCharBreedName, dto.getName());
        assertTrue(dto.getName().contains(" "));
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 높은 ID 값")
    void createBreedSearchResponseDto_HighId() {
        // Given
        Breed highIdBreed = Breed.builder()
                .name("골든리트리버")
                .build();
        setBreedId(highIdBreed, Long.MAX_VALUE);

        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(highIdBreed);

        // Then
        assertNotNull(dto);
        assertEquals(Long.MAX_VALUE, dto.getBreedId());
        assertEquals("골든리트리버", dto.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 0 ID 값")
    void createBreedSearchResponseDto_ZeroId() {
        // Given
        Breed zeroIdBreed = Breed.builder()
                .name("골든리트리버")
                .build();
        setBreedId(zeroIdBreed, 0L);

        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(zeroIdBreed);

        // Then
        assertNotNull(dto);
        assertEquals(0L, dto.getBreedId());
        assertEquals("골든리트리버", dto.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 다양한 견종들")
    void createBreedSearchResponseDto_VariousBreeds() {
        // Given
        String[] breedNames = {"푸들", "말티즈", "진돗개", "시바견", "코기"};

        for (int i = 0; i < breedNames.length; i++) {
            Breed breed = Breed.builder()
                    .name(breedNames[i])
                    .build();
            setBreedId(breed, (long) (i + 1));

            // When
            BreedSearchResponseDto dto = BreedSearchResponseDto.from(breed);

            // Then
            assertNotNull(dto);
            assertEquals((long) (i + 1), dto.getBreedId());
            assertEquals(breedNames[i], dto.getName());
        }
    }

    @Test
    @DisplayName("BreedSearchResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(mockBreed);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("BreedSearchResponseDto"));
    }

    @Test
    @DisplayName("BreedSearchResponseDto 필드 null 체크")
    void createBreedSearchResponseDto_FieldsNotNull() {
        // Given
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(mockBreed);

        // When & Then
        assertAll(
                () -> assertNotNull(dto.getBreedId()),
                () -> assertNotNull(dto.getName())
        );
    }

    @Test
    @DisplayName("BreedSearchResponseDto 필드 타입 검증")
    void createBreedSearchResponseDto_FieldTypes() {
        // Given
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(mockBreed);

        // When & Then
        assertTrue(dto.getBreedId() instanceof Long);
        assertTrue(dto.getName() instanceof String);
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 동일 객체 여러 번 생성")
    void createBreedSearchResponseDto_MultipleInstances() {
        // When
        BreedSearchResponseDto dto1 = BreedSearchResponseDto.from(mockBreed);
        BreedSearchResponseDto dto2 = BreedSearchResponseDto.from(mockBreed);

        // Then
        assertNotSame(dto1, dto2); // 다른 인스턴스
        assertEquals(dto1.getBreedId(), dto2.getBreedId()); // 같은 값
        assertEquals(dto1.getName(), dto2.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto Builder vs from 메서드 비교")
    void compareBuilderVsFromMethod() {
        // Given
        Long breedId = 10L;
        String breedName = "래브라도";

        // When
        BreedSearchResponseDto builderDto = BreedSearchResponseDto.builder()
                .breedId(breedId)
                .name(breedName)
                .build();

        Breed breed = Breed.builder()
                .name(breedName)
                .build();
        setBreedId(breed, breedId);
        BreedSearchResponseDto fromDto = BreedSearchResponseDto.from(breed);

        // Then
        assertEquals(builderDto.getBreedId(), fromDto.getBreedId());
        assertEquals(builderDto.getName(), fromDto.getName());
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 한글 견종 이름")
    void createBreedSearchResponseDto_KoreanBreedName() {
        // Given
        String[] koreanBreedNames = {"진돗개", "풍산개", "삽살개", "동경이"};

        for (int i = 0; i < koreanBreedNames.length; i++) {
            Breed koreanBreed = Breed.builder()
                    .name(koreanBreedNames[i])
                    .build();
            setBreedId(koreanBreed, (long) (i + 1));

            // When
            BreedSearchResponseDto dto = BreedSearchResponseDto.from(koreanBreed);

            // Then
            assertNotNull(dto);
            assertEquals((long) (i + 1), dto.getBreedId());
            assertEquals(koreanBreedNames[i], dto.getName());
        }
    }

    @Test
    @DisplayName("BreedSearchResponseDto 생성 테스트 - 숫자 포함 견종 이름")
    void createBreedSearchResponseDto_WithNumbers() {
        // Given
        String numberBreedName = "독일셰퍼드1";
        Breed numberBreed = Breed.builder()
                .name(numberBreedName)
                .build();
        setBreedId(numberBreed, 99L);

        // When
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(numberBreed);

        // Then
        assertNotNull(dto);
        assertEquals(99L, dto.getBreedId());
        assertEquals(numberBreedName, dto.getName());
        assertTrue(dto.getName().contains("1"));
    }

    @Test
    @DisplayName("BreedSearchResponseDto 불변성 테스트")
    void testImmutability() {
        // Given
        BreedSearchResponseDto dto = BreedSearchResponseDto.from(mockBreed);
        Long originalBreedId = dto.getBreedId();
        String originalName = dto.getName();

        // When
        // getter로 가져온 값들은 변경되지 않아야 함
        Long retrievedBreedId = dto.getBreedId();
        String retrievedName = dto.getName();

        // Then
        assertEquals(originalBreedId, retrievedBreedId);
        assertEquals(originalName, retrievedName);
        // 객체가 불변이므로 여러 번 호출해도 같은 값
        assertEquals(dto.getBreedId(), dto.getBreedId());
        assertEquals(dto.getName(), dto.getName());
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