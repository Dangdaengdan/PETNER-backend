package com.example.petner.domain.favorite.dto.response;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteResponseDtoTest {

    private Favorite mockFavorite;
    private Member mockMember;
    private Dog mockDog;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockDog = createMockDog();
        mockFavorite = createMockFavorite();
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트")
    void createFavoriteResponseDto_Success() {
        // When
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getFavoriteId());
        assertEquals(1L, dto.getMemberId());
        assertEquals(1L, dto.getDogId());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("FavoriteResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // When & Then
        assertEquals(1L, dto.getFavoriteId());
        assertEquals(1L, dto.getMemberId());
        assertEquals(1L, dto.getDogId());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 다양한 ID 값")
    void createFavoriteResponseDto_WithDifferentIds() {
        // Given
        Long favoriteId = 999L;
        Long memberId = 555L;
        Long dogId = 777L;

        when(mockFavorite.getFavoriteId()).thenReturn(favoriteId);
        when(mockMember.getMemberId()).thenReturn(memberId);
        when(mockDog.getDogId()).thenReturn(dogId);

        // When
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(favoriteId, dto.getFavoriteId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals(dogId, dto.getDogId());
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 과거 날짜")
    void createFavoriteResponseDto_WithPastDate() {
        // Given
        LocalDateTime pastDate = LocalDateTime.now().minusDays(10);
        when(mockFavorite.getCreatedAt()).thenReturn(pastDate);

        // When
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(pastDate, dto.getCreatedAt());
        assertTrue(dto.getCreatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 미래 날짜")
    void createFavoriteResponseDto_WithFutureDate() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        when(mockFavorite.getCreatedAt()).thenReturn(futureDate);

        // When
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(futureDate, dto.getCreatedAt());
        assertTrue(dto.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 높은 ID 값")
    void createFavoriteResponseDto_WithHighIds() {
        // Given
        Long highFavoriteId = Long.MAX_VALUE;
        Long highMemberId = Long.MAX_VALUE - 1;
        Long highDogId = Long.MAX_VALUE - 2;

        when(mockFavorite.getFavoriteId()).thenReturn(highFavoriteId);
        when(mockMember.getMemberId()).thenReturn(highMemberId);
        when(mockDog.getDogId()).thenReturn(highDogId);

        // When
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(highFavoriteId, dto.getFavoriteId());
        assertEquals(highMemberId, dto.getMemberId());
        assertEquals(highDogId, dto.getDogId());
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 0 ID 값")
    void createFavoriteResponseDto_WithZeroIds() {
        // Given
        when(mockFavorite.getFavoriteId()).thenReturn(0L);
        when(mockMember.getMemberId()).thenReturn(0L);
        when(mockDog.getDogId()).thenReturn(0L);

        // When
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(0L, dto.getFavoriteId());
        assertEquals(0L, dto.getMemberId());
        assertEquals(0L, dto.getDogId());
    }

    @Test
    @DisplayName("FavoriteResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("FavoriteResponseDto"));
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 정확한 시간")
    void createFavoriteResponseDto_WithExactTime() {
        // Given
        LocalDateTime exactTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        when(mockFavorite.getCreatedAt()).thenReturn(exactTime);

        // When
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(exactTime, dto.getCreatedAt());
        assertEquals(2024, dto.getCreatedAt().getYear());
        assertEquals(1, dto.getCreatedAt().getMonthValue());
        assertEquals(1, dto.getCreatedAt().getDayOfMonth());
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 멤버와 강아지 관계 확인")
    void createFavoriteResponseDto_VerifyMemberDogRelation() {
        // Given
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // When & Then
        verify(mockFavorite).getFavoriteId();
        verify(mockFavorite).getMember();
        verify(mockFavorite).getDog();
        verify(mockFavorite).getCreatedAt();
        verify(mockMember).getMemberId();
        verify(mockDog).getDogId();
    }

    @Test
    @DisplayName("FavoriteResponseDto 필드 null 체크")
    void createFavoriteResponseDto_FieldsNotNull() {
        // Given
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // When & Then
        assertAll(
                () -> assertNotNull(dto.getFavoriteId()),
                () -> assertNotNull(dto.getMemberId()),
                () -> assertNotNull(dto.getDogId()),
                () -> assertNotNull(dto.getCreatedAt())
        );
    }

    @Test
    @DisplayName("FavoriteResponseDto 필드 타입 검증")
    void createFavoriteResponseDto_FieldTypes() {
        // Given
        FavoriteResponseDto dto = new FavoriteResponseDto(mockFavorite);

        // When & Then
        assertTrue(dto.getFavoriteId() instanceof Long);
        assertTrue(dto.getMemberId() instanceof Long);
        assertTrue(dto.getDogId() instanceof Long);
        assertTrue(dto.getCreatedAt() instanceof LocalDateTime);
    }

    @Test
    @DisplayName("FavoriteResponseDto 생성 테스트 - 동일 객체 여러 번 생성")
    void createFavoriteResponseDto_MultipleInstances() {
        // When
        FavoriteResponseDto dto1 = new FavoriteResponseDto(mockFavorite);
        FavoriteResponseDto dto2 = new FavoriteResponseDto(mockFavorite);

        // Then
        assertNotSame(dto1, dto2); // 다른 인스턴스
        assertEquals(dto1.getFavoriteId(), dto2.getFavoriteId()); // 같은 값
        assertEquals(dto1.getMemberId(), dto2.getMemberId());
        assertEquals(dto1.getDogId(), dto2.getDogId());
        assertEquals(dto1.getCreatedAt(), dto2.getCreatedAt());
    }

    // Helper methods
    private Member createMockMember() {
        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);
        when(member.getNickname()).thenReturn("testUser");
        when(member.getEmail()).thenReturn("test@example.com");
        return member;
    }

    private Dog createMockDog() {
        Dog dog = mock(Dog.class);
        when(dog.getDogId()).thenReturn(1L);
        when(dog.getName()).thenReturn("테스트강아지");
        return dog;
    }

    private Favorite createMockFavorite() {
        Favorite favorite = mock(Favorite.class);
        when(favorite.getFavoriteId()).thenReturn(1L);
        when(favorite.getMember()).thenReturn(mockMember);
        when(favorite.getDog()).thenReturn(mockDog);
        when(favorite.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        return favorite;
    }
}