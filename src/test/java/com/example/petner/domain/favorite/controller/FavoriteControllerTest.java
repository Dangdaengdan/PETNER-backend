package com.example.petner.domain.favorite.controller;

import com.example.petner.domain.favorite.dto.request.FavoriteAddRequestDto;
import com.example.petner.domain.favorite.dto.response.FavoriteActionResponseDto;
import com.example.petner.domain.favorite.dto.response.FavoriteCheckResponseDto;
import com.example.petner.domain.favorite.dto.response.FavoriteListResponseDto;
import com.example.petner.domain.favorite.dto.response.FavoriteResponseDto;
import com.example.petner.domain.favorite.service.FavoriteQueryService;
import com.example.petner.domain.favorite.service.FavoriteService;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.FavoriteException;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private FavoriteQueryService favoriteQueryService;

    @InjectMocks
    private FavoriteController favoriteController;

    private SessionUser sessionUser;
    private FavoriteAddRequestDto addRequestDto;
    private FavoriteResponseDto favoriteResponseDto;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("testUser")
                .build();

        addRequestDto = createMockAddRequestDto();
        favoriteResponseDto = createMockFavoriteResponseDto();
    }

    @Test
    @DisplayName("즐겨찾기 추가 성공")
    void addFavorite_Success() {
        // Given
        when(favoriteService.addFavorite(any(FavoriteAddRequestDto.class), any(SessionUser.class)))
                .thenReturn(favoriteResponseDto);

        // When
        ResponseEntity<FavoriteResponseDto> response = favoriteController.addFavorite(addRequestDto, sessionUser);

        // Then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(favoriteResponseDto.getFavoriteId(), response.getBody().getFavoriteId());
        assertEquals(favoriteResponseDto.getMemberId(), response.getBody().getMemberId());
        assertEquals(favoriteResponseDto.getDogId(), response.getBody().getDogId());

        verify(favoriteService).addFavorite(addRequestDto, sessionUser);
    }

    @Test
    @DisplayName("즐겨찾기 추가 실패 - 중복 즐겨찾기")
    void addFavorite_DuplicateFavorite() {
        // Given
        when(favoriteService.addFavorite(any(FavoriteAddRequestDto.class), any(SessionUser.class)))
                .thenThrow(new FavoriteException(ErrorCode.FAVORITE_ALREADY_EXISTS));

        // When & Then
        assertThrows(FavoriteException.class,
                () -> favoriteController.addFavorite(addRequestDto, sessionUser));

        verify(favoriteService).addFavorite(addRequestDto, sessionUser);
    }

    @Test
    @DisplayName("즐겨찾기 제거 성공")
    void removeFavorite_Success() {
        // Given
        Long dogId = 1L;
        doNothing().when(favoriteService).removeFavorite(dogId, sessionUser);

        // When
        ResponseEntity<FavoriteActionResponseDto> response = favoriteController.removeFavorite(dogId, sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(sessionUser.getMemberId(), response.getBody().getMemberId());
        assertEquals(dogId, response.getBody().getDogId());
        assertEquals("즐겨찾기 제거 성공", response.getBody().getMessage());

        verify(favoriteService).removeFavorite(dogId, sessionUser);
    }

    @Test
    @DisplayName("즐겨찾기 제거 실패 - 즐겨찾기 없음")
    void removeFavorite_NotInFavorites() {
        // Given
        Long dogId = 1L;
        doThrow(new FavoriteException(ErrorCode.FAVORITE_NOT_IN_MY_LIST))
                .when(favoriteService).removeFavorite(dogId, sessionUser);

        // When & Then
        assertThrows(FavoriteException.class,
                () -> favoriteController.removeFavorite(dogId, sessionUser));

        verify(favoriteService).removeFavorite(dogId, sessionUser);
    }

    @Test
    @DisplayName("내 즐겨찾기 목록 조회 성공 - 페이징")
    void getMyFavorites_WithPaging_Success() {
        // Given
        int page = 0;
        int size = 10;
        List<FavoriteListResponseDto> favoriteList = createMockFavoriteList();
        when(favoriteQueryService.getMemberFavorites(sessionUser.getMemberId(), page, size))
                .thenReturn(favoriteList);

        // When
        ResponseEntity<List<FavoriteListResponseDto>> response =
                favoriteController.getMyFavorites(sessionUser, page, size);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(favoriteQueryService).getMemberFavorites(sessionUser.getMemberId(), page, size);
    }

    @Test
    @DisplayName("내 즐겨찾기 목록 조회 성공 - 기본 페이징 값")
    void getMyFavorites_DefaultPaging_Success() {
        // Given
        List<FavoriteListResponseDto> favoriteList = createMockFavoriteList();
        when(favoriteQueryService.getMemberFavorites(sessionUser.getMemberId(), 0, 10))
                .thenReturn(favoriteList);

        // When
        ResponseEntity<List<FavoriteListResponseDto>> response =
                favoriteController.getMyFavorites(sessionUser, 0, 10);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(favoriteQueryService).getMemberFavorites(sessionUser.getMemberId(), 0, 10);
    }

    @Test
    @DisplayName("내 즐겨찾기 목록 전체 조회 성공")
    void getAllMyFavorites_Success() {
        // Given
        List<FavoriteListResponseDto> favoriteList = createMockFavoriteList();
        when(favoriteQueryService.getMemberFavorites(sessionUser.getMemberId()))
                .thenReturn(favoriteList);

        // When
        ResponseEntity<List<FavoriteListResponseDto>> response =
                favoriteController.getAllMyFavorites(sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(favoriteQueryService).getMemberFavorites(sessionUser.getMemberId());
    }

    @Test
    @DisplayName("즐겨찾기 여부 확인 성공 - 즐겨찾기 있음")
    void checkFavorite_IsFavorite_Success() {
        // Given
        Long dogId = 1L;
        when(favoriteQueryService.isFavorite(sessionUser.getMemberId(), dogId))
                .thenReturn(true);

        // When
        ResponseEntity<FavoriteCheckResponseDto> response =
                favoriteController.checkFavorite(dogId, sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(sessionUser.getMemberId(), response.getBody().getMemberId());
        assertEquals(dogId, response.getBody().getDogId());
        assertTrue(response.getBody().isFavorite());

        verify(favoriteQueryService).isFavorite(sessionUser.getMemberId(), dogId);
    }

    @Test
    @DisplayName("즐겨찾기 여부 확인 성공 - 즐겨찾기 없음")
    void checkFavorite_IsNotFavorite_Success() {
        // Given
        Long dogId = 2L;
        when(favoriteQueryService.isFavorite(sessionUser.getMemberId(), dogId))
                .thenReturn(false);

        // When
        ResponseEntity<FavoriteCheckResponseDto> response =
                favoriteController.checkFavorite(dogId, sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(sessionUser.getMemberId(), response.getBody().getMemberId());
        assertEquals(dogId, response.getBody().getDogId());
        assertFalse(response.getBody().isFavorite());

        verify(favoriteQueryService).isFavorite(sessionUser.getMemberId(), dogId);
    }

    @Test
    @DisplayName("빈 즐겨찾기 목록 조회")
    void getMyFavorites_EmptyList_Success() {
        // Given
        when(favoriteQueryService.getMemberFavorites(sessionUser.getMemberId(), 0, 10))
                .thenReturn(List.of());

        // When
        ResponseEntity<List<FavoriteListResponseDto>> response =
                favoriteController.getMyFavorites(sessionUser, 0, 10);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(favoriteQueryService).getMemberFavorites(sessionUser.getMemberId(), 0, 10);
    }

    @Test
    @DisplayName("다양한 페이징 파라미터 테스트")
    void getMyFavorites_VariousPagingParameters() {
        // Given
        List<FavoriteListResponseDto> favoriteList = createMockFavoriteList();

        // Case 1: 두 번째 페이지
        when(favoriteQueryService.getMemberFavorites(sessionUser.getMemberId(), 1, 5))
                .thenReturn(favoriteList);

        // Case 2: 큰 페이지 크기
        when(favoriteQueryService.getMemberFavorites(sessionUser.getMemberId(), 0, 50))
                .thenReturn(favoriteList);

        // When & Then
        ResponseEntity<List<FavoriteListResponseDto>> response1 =
                favoriteController.getMyFavorites(sessionUser, 1, 5);
        ResponseEntity<List<FavoriteListResponseDto>> response2 =
                favoriteController.getMyFavorites(sessionUser, 0, 50);

        assertEquals(200, response1.getStatusCodeValue());
        assertEquals(200, response2.getStatusCodeValue());

        verify(favoriteQueryService).getMemberFavorites(sessionUser.getMemberId(), 1, 5);
        verify(favoriteQueryService).getMemberFavorites(sessionUser.getMemberId(), 0, 50);
    }

    @Test
    @DisplayName("즐겨찾기 서비스 예외 처리")
    void addFavorite_ServiceException() {
        // Given
        when(favoriteService.addFavorite(any(FavoriteAddRequestDto.class), any(SessionUser.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteController.addFavorite(addRequestDto, sessionUser));

        verify(favoriteService).addFavorite(addRequestDto, sessionUser);
    }

    @Test
    @DisplayName("즐겨찾기 쿼리 서비스 예외 처리")
    void checkFavorite_QueryServiceException() {
        // Given
        Long dogId = 1L;
        when(favoriteQueryService.isFavorite(sessionUser.getMemberId(), dogId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteController.checkFavorite(dogId, sessionUser));

        verify(favoriteQueryService).isFavorite(sessionUser.getMemberId(), dogId);
    }

    // Helper methods
    private FavoriteAddRequestDto createMockAddRequestDto() {
        FavoriteAddRequestDto mock = mock(FavoriteAddRequestDto.class);
        when(mock.getDogId()).thenReturn(1L);
        return mock;
    }

    private FavoriteResponseDto createMockFavoriteResponseDto() {
        FavoriteResponseDto mock = mock(FavoriteResponseDto.class);
        when(mock.getFavoriteId()).thenReturn(1L);
        when(mock.getMemberId()).thenReturn(1L);
        when(mock.getDogId()).thenReturn(1L);
        when(mock.getCreatedAt()).thenReturn(LocalDateTime.now());
        return mock;
    }

    private List<FavoriteListResponseDto> createMockFavoriteList() {
        FavoriteListResponseDto favorite1 = mock(FavoriteListResponseDto.class);
        when(favorite1.getFavoriteId()).thenReturn(1L);
        when(favorite1.getFavoriteId()).thenReturn(1L);

        FavoriteListResponseDto favorite2 = mock(FavoriteListResponseDto.class);
        when(favorite2.getFavoriteId()).thenReturn(2L);
        when(favorite2.getFavoriteId()).thenReturn(2L);

        return Arrays.asList(favorite1, favorite2);
    }
}