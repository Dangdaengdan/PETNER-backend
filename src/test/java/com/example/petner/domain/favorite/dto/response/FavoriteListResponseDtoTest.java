package com.example.petner.domain.favorite.dto.response;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.config.common.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteListResponseDtoTest {

    private Favorite mockFavorite;
    private Member mockMember;
    private Dog mockDog;
    private Breed mockBreed;
    private Shelter mockShelter;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockBreed = createMockBreed();
        mockShelter = createMockShelter();
        mockDog = createMockDog();
        mockFavorite = createMockFavorite();
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트")
    void createFavoriteListResponseDto_Success() {
        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getFavoriteId());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getDogInfo());

        // DogInfo 검증
        FavoriteListResponseDto.DogInfo dogInfo = dto.getDogInfo();
        assertEquals(1L, dogInfo.getDogId());
        assertEquals("테스트강아지", dogInfo.getName());
        assertEquals("골든리트리버", dogInfo.getBreedName());
        assertEquals("MALE", dogInfo.getGender());
        assertEquals("LARGE", dogInfo.getDogSize());
        assertEquals(new BigDecimal("30.5"), dogInfo.getWeight());
        assertEquals("AVAILABLE", dogInfo.getAdoptionStatus());
        assertEquals("dog-image.jpg", dogInfo.getImageUrl());
        assertEquals("테스트보호소", dogInfo.getShelterName());
    }

    @Test
    @DisplayName("FavoriteListResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // When & Then
        assertEquals(1L, dto.getFavoriteId());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getDogInfo());
    }

    @Test
    @DisplayName("FavoriteListResponseDto DogInfo 생성 테스트")
    void createDogInfo_Success() {
        // When
        FavoriteListResponseDto.DogInfo dogInfo = new FavoriteListResponseDto.DogInfo(mockDog);

        // Then
        assertNotNull(dogInfo);
        assertEquals(1L, dogInfo.getDogId());
        assertEquals("테스트강아지", dogInfo.getName());
        assertEquals("골든리트리버", dogInfo.getBreedName());
        assertEquals("MALE", dogInfo.getGender());
        assertEquals("LARGE", dogInfo.getDogSize());
        assertEquals(new BigDecimal("30.5"), dogInfo.getWeight());
        assertEquals("AVAILABLE", dogInfo.getAdoptionStatus());
        assertEquals("dog-image.jpg", dogInfo.getImageUrl());
        assertEquals("테스트보호소", dogInfo.getShelterName());
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 보호소 정보 없음")
    void createFavoriteListResponseDto_WithoutShelter() {
        // Given
        when(mockDog.getShelter()).thenReturn(null);

        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.getDogInfo());
        assertNull(dto.getDogInfo().getShelterName());
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 다양한 견종")
    void createFavoriteListResponseDto_WithDifferentBreeds() {
        // Given
        String[] breedNames = {"진돗개", "말티즈", "푸들", "시바견", "코기"};

        for (String breedName : breedNames) {
            when(mockBreed.getName()).thenReturn(breedName);

            // When
            FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

            // Then
            assertNotNull(dto);
            assertEquals(breedName, dto.getDogInfo().getBreedName());
        }
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 다양한 성별")
    void createFavoriteListResponseDto_WithDifferentGenders() {
        // Given
        Gender[] genders = {Gender.MALE, Gender.FEMALE};

        for (Gender gender : genders) {
            when(mockDog.getGender()).thenReturn(gender);

            // When
            FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

            // Then
            assertNotNull(dto);
            assertEquals(gender.name(), dto.getDogInfo().getGender());
        }
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 다양한 크기")
    void createFavoriteListResponseDto_WithDifferentSizes() {
        // Given
        DogSize[] sizes = {DogSize.SMALL, DogSize.MEDIUM, DogSize.LARGE};

        for (DogSize size : sizes) {
            when(mockDog.getDogSize()).thenReturn(size);

            // When
            FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

            // Then
            assertNotNull(dto);
            assertEquals(size.name(), dto.getDogInfo().getDogSize());
        }
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 다양한 입양 상태")
    void createFavoriteListResponseDto_WithDifferentAdoptionStatuses() {
        // Given
        AdoptionStatus[] statuses = {AdoptionStatus.AVAILABLE, AdoptionStatus.ADOPTED, AdoptionStatus.PENDING};

        for (AdoptionStatus status : statuses) {
            when(mockDog.getAdoptionStatus()).thenReturn(status);

            // When
            FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

            // Then
            assertNotNull(dto);
            assertEquals(status.name(), dto.getDogInfo().getAdoptionStatus());
        }
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 다양한 몸무게")
    void createFavoriteListResponseDto_WithDifferentWeights() {
        // Given
        BigDecimal[] weights = {
                new BigDecimal("5.5"),
                new BigDecimal("15.0"),
                new BigDecimal("25.8"),
                new BigDecimal("40.2")
        };

        for (BigDecimal weight : weights) {
            when(mockDog.getWeight()).thenReturn(weight);

            // When
            FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

            // Then
            assertNotNull(dto);
            assertEquals(weight, dto.getDogInfo().getWeight());
        }
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 과거 날짜")
    void createFavoriteListResponseDto_WithPastDate() {
        // Given
        LocalDateTime pastDate = LocalDateTime.now().minusDays(7);
        when(mockFavorite.getCreatedAt()).thenReturn(pastDate);

        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(pastDate, dto.getCreatedAt());
        assertTrue(dto.getCreatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 긴 강아지 이름")
    void createFavoriteListResponseDto_WithLongDogName() {
        // Given
        String longName = "매우긴이름을가진강아지".repeat(5);
        when(mockDog.getName()).thenReturn(longName);

        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(longName, dto.getDogInfo().getName());
        assertTrue(dto.getDogInfo().getName().length() > 50);
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 이미지 URL 없음")
    void createFavoriteListResponseDto_WithoutImageUrl() {
        // Given
        when(mockDog.getImageUrl()).thenReturn(null);

        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertNull(dto.getDogInfo().getImageUrl());
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 빈 이미지 URL")
    void createFavoriteListResponseDto_WithEmptyImageUrl() {
        // Given
        when(mockDog.getImageUrl()).thenReturn("");

        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals("", dto.getDogInfo().getImageUrl());
    }

    @Test
    @DisplayName("FavoriteListResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("FavoriteListResponseDto"));
    }

    @Test
    @DisplayName("FavoriteListResponseDto DogInfo toString 메서드 동작 확인")
    void testDogInfoToStringMethod() {
        // Given
        FavoriteListResponseDto.DogInfo dogInfo = new FavoriteListResponseDto.DogInfo(mockDog);

        // When
        String toStringResult = dogInfo.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("DogInfo"));
    }

    @Test
    @DisplayName("FavoriteListResponseDto 필드 null 체크")
    void createFavoriteListResponseDto_FieldsNotNull() {
        // Given
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // When & Then
        assertAll(
                () -> assertNotNull(dto.getFavoriteId()),
                () -> assertNotNull(dto.getCreatedAt()),
                () -> assertNotNull(dto.getDogInfo())
        );
    }

    @Test
    @DisplayName("FavoriteListResponseDto DogInfo 필드 타입 검증")
    void createFavoriteListResponseDto_DogInfoFieldTypes() {
        // Given
        FavoriteListResponseDto.DogInfo dogInfo = new FavoriteListResponseDto.DogInfo(mockDog);

        // When & Then
        assertTrue(dogInfo.getDogId() instanceof Long);
        assertTrue(dogInfo.getName() instanceof String);
        assertTrue(dogInfo.getBreedName() instanceof String);
        assertTrue(dogInfo.getGender() instanceof String);
        assertTrue(dogInfo.getDogSize() instanceof String);
        assertTrue(dogInfo.getWeight() instanceof BigDecimal);
        assertTrue(dogInfo.getAdoptionStatus() instanceof String);
        assertTrue(dogInfo.getImageUrl() instanceof String);
        assertTrue(dogInfo.getShelterName() instanceof String || dogInfo.getShelterName() == null);
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 동일 객체 여러 번 생성")
    void createFavoriteListResponseDto_MultipleInstances() {
        // When
        FavoriteListResponseDto dto1 = new FavoriteListResponseDto(mockFavorite);
        FavoriteListResponseDto dto2 = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotSame(dto1, dto2); // 다른 인스턴스
        assertEquals(dto1.getFavoriteId(), dto2.getFavoriteId()); // 같은 값
        assertEquals(dto1.getCreatedAt(), dto2.getCreatedAt());
        assertEquals(dto1.getDogInfo().getDogId(), dto2.getDogInfo().getDogId());
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 높은 ID 값")
    void createFavoriteListResponseDto_WithHighIds() {
        // Given
        Long highFavoriteId = Long.MAX_VALUE;
        Long highDogId = Long.MAX_VALUE - 1;

        when(mockFavorite.getFavoriteId()).thenReturn(highFavoriteId);
        when(mockDog.getDogId()).thenReturn(highDogId);

        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(highFavoriteId, dto.getFavoriteId());
        assertEquals(highDogId, dto.getDogInfo().getDogId());
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 0 몸무게")
    void createFavoriteListResponseDto_WithZeroWeight() {
        // Given
        when(mockDog.getWeight()).thenReturn(BigDecimal.ZERO);

        // When
        FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(BigDecimal.ZERO, dto.getDogInfo().getWeight());
    }

    @Test
    @DisplayName("FavoriteListResponseDto 생성 테스트 - 다양한 이미지 URL 형식")
    void createFavoriteListResponseDto_WithVariousImageUrls() {
        // Given
        String[] imageUrls = {
                "dog.jpg",
                "https://example.com/dog.png",
                "/images/dog.webp",
                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD//"
        };

        for (String imageUrl : imageUrls) {
            when(mockDog.getImageUrl()).thenReturn(imageUrl);

            // When
            FavoriteListResponseDto dto = new FavoriteListResponseDto(mockFavorite);

            // Then
            assertNotNull(dto);
            assertEquals(imageUrl, dto.getDogInfo().getImageUrl());
        }
    }

    // Helper methods
    private Member createMockMember() {
        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);
        when(member.getNickname()).thenReturn("testUser");
        when(member.getEmail()).thenReturn("test@example.com");
        return member;
    }

    private Breed createMockBreed() {
        Breed breed = mock(Breed.class);
        when(breed.getBreedId()).thenReturn(1L);
        when(breed.getName()).thenReturn("골든리트리버");
        return breed;
    }

    private Shelter createMockShelter() {
        Shelter shelter = mock(Shelter.class);
        when(shelter.getShelterId()).thenReturn(1L);
        when(shelter.getName()).thenReturn("테스트보호소");
        return shelter;
    }

    private Dog createMockDog() {
        Dog dog = mock(Dog.class);
        when(dog.getDogId()).thenReturn(1L);
        when(dog.getName()).thenReturn("테스트강아지");
        when(dog.getBreed()).thenReturn(mockBreed);
        when(dog.getGender()).thenReturn(Gender.MALE);
        when(dog.getDogSize()).thenReturn(DogSize.LARGE);
        when(dog.getWeight()).thenReturn(new BigDecimal("30.5"));
        when(dog.getAdoptionStatus()).thenReturn(AdoptionStatus.AVAILABLE);
        when(dog.getImageUrl()).thenReturn("dog-image.jpg");
        when(dog.getShelter()).thenReturn(mockShelter);
        return dog;
    }

    private Favorite createMockFavorite() {
        Favorite favorite = mock(Favorite.class);
        when(favorite.getFavoriteId()).thenReturn(1L);
        when(favorite.getMember()).thenReturn(mockMember);
        when(favorite.getDog()).thenReturn(mockDog);
        when(favorite.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(2));
        return favorite;
    }
}