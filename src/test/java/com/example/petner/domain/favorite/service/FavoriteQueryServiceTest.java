package com.example.petner.domain.favorite.service;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.favorite.dto.response.FavoriteListResponseDto;
import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.favorite.repository.FavoriteRepository;
import com.example.petner.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FavoriteQueryServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteQueryService favoriteQueryService;

    private Member mockMember;
    private Dog mockDog1;
    private Dog mockDog2;
    private Favorite mockFavorite1;
    private Favorite mockFavorite2;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockDog1 = createMockDog(1L, "강아지1");
        mockDog2 = createMockDog(2L, "강아지2");
        mockFavorite1 = createMockFavorite(1L, mockMember, mockDog1);
        mockFavorite2 = createMockFavorite(2L, mockMember, mockDog2);
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 성공 - 전체 목록")
    void getMemberFavorites_Success() {
        // Given
        Long memberId = 1L;
        List<Favorite> favorites = Arrays.asList(mockFavorite1, mockFavorite2);
        when(favoriteRepository.findByMemberIdWithDogDetails(memberId)).thenReturn(favorites);

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getFavoriteId());
        assertEquals(2L, result.get(1).getFavoriteId());

        verify(favoriteRepository).findByMemberIdWithDogDetails(memberId);
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 성공 - 빈 목록")
    void getMemberFavorites_EmptyList() {
        // Given
        Long memberId = 1L;
        when(favoriteRepository.findByMemberIdWithDogDetails(memberId)).thenReturn(List.of());

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(favoriteRepository).findByMemberIdWithDogDetails(memberId);
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 성공 - 페이징")
    void getMemberFavorites_WithPaging_Success() {
        // Given
        Long memberId = 1L;
        int page = 0;
        int size = 10;
        List<Favorite> favorites = Arrays.asList(mockFavorite1, mockFavorite2);
        when(favoriteRepository.findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class)))
                .thenReturn(favorites);

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getFavoriteId());
        assertEquals(2L, result.get(1).getFavoriteId());

        verify(favoriteRepository).findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 페이징 파라미터 검증")
    void getMemberFavorites_WithPaging_VerifyPageable() {
        // Given
        Long memberId = 1L;
        int page = 2;
        int size = 5;
        when(favoriteRepository.findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class)))
                .thenReturn(List.of());

        // When
        favoriteQueryService.getMemberFavorites(memberId, page, size);

        // Then
        verify(favoriteRepository).findByMemberIdWithDogDetailsPaging(eq(memberId), argThat(pageable ->
                pageable.getPageNumber() == page &&
                pageable.getPageSize() == size &&
                pageable.getSort().isSorted()
        ));
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 첫 번째 페이지")
    void getMemberFavorites_FirstPage() {
        // Given
        Long memberId = 1L;
        int page = 0;
        int size = 2;
        List<Favorite> favorites = Arrays.asList(mockFavorite1, mockFavorite2);
        when(favoriteRepository.findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class)))
                .thenReturn(favorites);

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(favoriteRepository).findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 두 번째 페이지")
    void getMemberFavorites_SecondPage() {
        // Given
        Long memberId = 1L;
        int page = 1;
        int size = 5;
        when(favoriteRepository.findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class)))
                .thenReturn(List.of(mockFavorite1));

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(favoriteRepository).findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 페이징 빈 결과")
    void getMemberFavorites_WithPaging_EmptyResult() {
        // Given
        Long memberId = 1L;
        int page = 5;
        int size = 10;
        when(favoriteRepository.findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class)))
                .thenReturn(List.of());

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId, page, size);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoriteRepository).findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("즐겨찾기 존재 여부 확인 - 즐겨찾기 있음")
    void isFavorite_Exists() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(true);

        // When
        boolean result = favoriteQueryService.isFavorite(memberId, dogId);

        // Then
        assertTrue(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("즐겨찾기 존재 여부 확인 - 즐겨찾기 없음")
    void isFavorite_NotExists() {
        // Given
        Long memberId = 1L;
        Long dogId = 2L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(false);

        // When
        boolean result = favoriteQueryService.isFavorite(memberId, dogId);

        // Then
        assertFalse(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("즐겨찾기 존재 여부 확인 - 다양한 ID 조합")
    void isFavorite_VariousIdCombinations() {
        // Given
        Long[][] testCases = {
                {1L, 1L, true},
                {1L, 2L, false},
                {2L, 1L, false},
                {999L, 888L, false}
        };

        for (Long[] testCase : testCases) {
            Long memberId = testCase[0];
            Long dogId = testCase[1];
            boolean expected = testCase[2] == 1L;

            when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                    .thenReturn(expected);

            // When
            boolean result = favoriteQueryService.isFavorite(memberId, dogId);

            // Then
            assertEquals(expected, result);
            verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
        }
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 높은 ID 값")
    void getMemberFavorites_WithHighMemberId() {
        // Given
        Long highMemberId = Long.MAX_VALUE;
        when(favoriteRepository.findByMemberIdWithDogDetails(highMemberId)).thenReturn(List.of());

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(highMemberId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoriteRepository).findByMemberIdWithDogDetails(highMemberId);
    }

    @Test
    @DisplayName("즐겨찾기 존재 여부 확인 - 0 ID 값")
    void isFavorite_WithZeroIds() {
        // Given
        Long memberId = 0L;
        Long dogId = 0L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(false);

        // When
        boolean result = favoriteQueryService.isFavorite(memberId, dogId);

        // Then
        assertFalse(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 대용량 페이지 크기")
    void getMemberFavorites_WithLargePageSize() {
        // Given
        Long memberId = 1L;
        int page = 0;
        int size = 1000;
        when(favoriteRepository.findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class)))
                .thenReturn(Arrays.asList(mockFavorite1, mockFavorite2));

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(favoriteRepository).findByMemberIdWithDogDetailsPaging(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 저장소 예외 처리")
    void getMemberFavorites_RepositoryException() {
        // Given
        Long memberId = 1L;
        when(favoriteRepository.findByMemberIdWithDogDetails(memberId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteQueryService.getMemberFavorites(memberId));

        verify(favoriteRepository).findByMemberIdWithDogDetails(memberId);
    }

    @Test
    @DisplayName("즐겨찾기 존재 여부 확인 - 저장소 예외 처리")
    void isFavorite_RepositoryException() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteQueryService.isFavorite(memberId, dogId));

        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("멤버 즐겨찾기 목록 조회 - 단일 즐겨찾기")
    void getMemberFavorites_SingleFavorite() {
        // Given
        Long memberId = 1L;
        when(favoriteRepository.findByMemberIdWithDogDetails(memberId))
                .thenReturn(List.of(mockFavorite1));

        // When
        List<FavoriteListResponseDto> result = favoriteQueryService.getMemberFavorites(memberId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFavoriteId());
        verify(favoriteRepository).findByMemberIdWithDogDetails(memberId);
    }

    // Helper methods
    private Member createMockMember() {
        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);
        when(member.getNickname()).thenReturn("testUser");
        when(member.getEmail()).thenReturn("test@example.com");
        return member;
    }

    private Dog createMockDog(Long dogId, String name) {
        Dog dog = mock(Dog.class);
        when(dog.getDogId()).thenReturn(dogId);
        when(dog.getName()).thenReturn(name);
        return dog;
    }

    private Favorite createMockFavorite(Long favoriteId, Member member, Dog dog) {
        Favorite favorite = mock(Favorite.class);
        when(favorite.getFavoriteId()).thenReturn(favoriteId);
        when(favorite.getMember()).thenReturn(member);
        when(favorite.getDog()).thenReturn(dog);
        when(favorite.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(favoriteId));
        return favorite;
    }
}