package com.example.petner.domain.favorite.service;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.favorite.dto.request.FavoriteAddRequestDto;
import com.example.petner.domain.favorite.dto.response.FavoriteResponseDto;
import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.favorite.repository.FavoriteRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import com.example.petner.global.exception.customException.FavoriteException;
import com.example.petner.global.exception.customException.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteValidator favoriteValidator;

    @Mock
    private FavoriteDuplicateChecker duplicateChecker;

    @InjectMocks
    private FavoriteService favoriteService;

    private SessionUser sessionUser;
    private FavoriteAddRequestDto addRequestDto;
    private Member mockMember;
    private Dog mockDog;
    private Favorite mockFavorite;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("testUser")
                .build();

        addRequestDto = createMockAddRequestDto();
        mockMember = createMockMember();
        mockDog = createMockDog();
        mockFavorite = createMockFavorite();
    }

    @Test
    @DisplayName("즐겨찾기 추가 성공")
    void addFavorite_Success() {
        // Given
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(1L)).thenReturn(mockDog);
        doNothing().when(duplicateChecker).checkDuplicate(1L, 1L);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(mockFavorite);

        // When
        FavoriteResponseDto result = favoriteService.addFavorite(addRequestDto, sessionUser);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getFavoriteId());
        assertEquals(1L, result.getMemberId());
        assertEquals(1L, result.getDogId());

        verify(favoriteValidator).validateAndGetMember(1L);
        verify(favoriteValidator).validateAndGetDog(1L);
        verify(duplicateChecker).checkDuplicate(1L, 1L);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("즐겨찾기 추가 실패 - 멤버 없음")
    void addFavorite_MemberNotFound() {
        // Given
        when(favoriteValidator.validateAndGetMember(1L))
                .thenThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> favoriteService.addFavorite(addRequestDto, sessionUser));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(favoriteValidator).validateAndGetMember(1L);
        verifyNoInteractions(duplicateChecker, favoriteRepository);
    }

    @Test
    @DisplayName("즐겨찾기 추가 실패 - 강아지 없음")
    void addFavorite_DogNotFound() {
        // Given
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(1L))
                .thenThrow(new DogException(ErrorCode.DOG_NOT_FOUND));

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> favoriteService.addFavorite(addRequestDto, sessionUser));

        assertEquals(ErrorCode.DOG_NOT_FOUND, exception.getErrorCode());
        verify(favoriteValidator).validateAndGetMember(1L);
        verify(favoriteValidator).validateAndGetDog(1L);
        verifyNoInteractions(duplicateChecker, favoriteRepository);
    }

    @Test
    @DisplayName("즐겨찾기 추가 실패 - 중복 즐겨찾기")
    void addFavorite_DuplicateFavorite() {
        // Given
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(1L)).thenReturn(mockDog);
        doThrow(new FavoriteException(ErrorCode.FAVORITE_ALREADY_EXISTS))
                .when(duplicateChecker).checkDuplicate(1L, 1L);

        // When & Then
        FavoriteException exception = assertThrows(FavoriteException.class,
                () -> favoriteService.addFavorite(addRequestDto, sessionUser));

        assertEquals(ErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());
        verify(favoriteValidator).validateAndGetMember(1L);
        verify(favoriteValidator).validateAndGetDog(1L);
        verify(duplicateChecker).checkDuplicate(1L, 1L);
        verifyNoInteractions(favoriteRepository);
    }

    @Test
    @DisplayName("즐겨찾기 추가 - 저장소 오류")
    void addFavorite_RepositoryError() {
        // Given
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(1L)).thenReturn(mockDog);
        doNothing().when(duplicateChecker).checkDuplicate(1L, 1L);
        when(favoriteRepository.save(any(Favorite.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteService.addFavorite(addRequestDto, sessionUser));

        verify(favoriteValidator).validateAndGetMember(1L);
        verify(favoriteValidator).validateAndGetDog(1L);
        verify(duplicateChecker).checkDuplicate(1L, 1L);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("즐겨찾기 제거 성공")
    void removeFavorite_Success() {
        // Given
        Long dogId = 1L;
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(dogId)).thenReturn(mockDog);
        when(duplicateChecker.exists(1L, dogId)).thenReturn(true);
        doNothing().when(favoriteRepository).deleteByMemberMemberIdAndDogDogId(1L, dogId);

        // When
        favoriteService.removeFavorite(dogId, sessionUser);

        // Then
        verify(favoriteValidator).validateAndGetMember(1L);
        verify(favoriteValidator).validateAndGetDog(dogId);
        verify(duplicateChecker).exists(1L, dogId);
        verify(favoriteRepository).deleteByMemberMemberIdAndDogDogId(1L, dogId);
    }

    @Test
    @DisplayName("즐겨찾기 제거 실패 - 멤버 없음")
    void removeFavorite_MemberNotFound() {
        // Given
        Long dogId = 1L;
        when(favoriteValidator.validateAndGetMember(1L))
                .thenThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> favoriteService.removeFavorite(dogId, sessionUser));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(favoriteValidator).validateAndGetMember(1L);
        verifyNoMoreInteractions(favoriteValidator);
        verifyNoInteractions(duplicateChecker, favoriteRepository);
    }

    @Test
    @DisplayName("즐겨찾기 제거 실패 - 강아지 없음")
    void removeFavorite_DogNotFound() {
        // Given
        Long dogId = 1L;
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(dogId))
                .thenThrow(new DogException(ErrorCode.DOG_NOT_FOUND));

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> favoriteService.removeFavorite(dogId, sessionUser));

        assertEquals(ErrorCode.DOG_NOT_FOUND, exception.getErrorCode());
        verify(favoriteValidator).validateAndGetMember(1L);
        verify(favoriteValidator).validateAndGetDog(dogId);
        verifyNoInteractions(duplicateChecker, favoriteRepository);
    }

    @Test
    @DisplayName("즐겨찾기 제거 실패 - 즐겨찾기 없음")
    void removeFavorite_FavoriteNotExists() {
        // Given
        Long dogId = 1L;
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(dogId)).thenReturn(mockDog);
        when(duplicateChecker.exists(1L, dogId)).thenReturn(false);

        // When & Then
        FavoriteException exception = assertThrows(FavoriteException.class,
                () -> favoriteService.removeFavorite(dogId, sessionUser));

        assertEquals(ErrorCode.FAVORITE_NOT_IN_MY_LIST, exception.getErrorCode());
        verify(favoriteValidator).validateAndGetMember(1L);
        verify(favoriteValidator).validateAndGetDog(dogId);
        verify(duplicateChecker).exists(1L, dogId);
        verifyNoInteractions(favoriteRepository);
    }

    @Test
    @DisplayName("즐겨찾기 제거 - 삭제 실행 확인")
    void removeFavorite_VerifyDeletion() {
        // Given
        Long dogId = 2L;
        Long memberId = 3L;
        SessionUser customUser = SessionUser.builder()
                .memberId(memberId)
                .email("custom@example.com")
                .nickname("customUser")
                .build();

        Member customMember = mock(Member.class);
        when(customMember.getMemberId()).thenReturn(memberId);

        Dog customDog = mock(Dog.class);
        when(customDog.getDogId()).thenReturn(dogId);

        when(favoriteValidator.validateAndGetMember(memberId)).thenReturn(customMember);
        when(favoriteValidator.validateAndGetDog(dogId)).thenReturn(customDog);
        when(duplicateChecker.exists(memberId, dogId)).thenReturn(true);

        // When
        favoriteService.removeFavorite(dogId, customUser);

        // Then
        verify(favoriteRepository).deleteByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("즐겨찾기 추가 - 서로 다른 멤버와 강아지")
    void addFavorite_DifferentMemberAndDog() {
        // Given
        Long memberId = 5L;
        Long dogId = 10L;

        SessionUser customUser = SessionUser.builder()
                .memberId(memberId)
                .email("member5@example.com")
                .nickname("member5")
                .build();

        FavoriteAddRequestDto customRequest = mock(FavoriteAddRequestDto.class);
        when(customRequest.getDogId()).thenReturn(dogId);

        Member customMember = mock(Member.class);
        when(customMember.getMemberId()).thenReturn(memberId);

        Dog customDog = mock(Dog.class);
        when(customDog.getDogId()).thenReturn(dogId);

        Favorite customFavorite = mock(Favorite.class);
        when(customFavorite.getFavoriteId()).thenReturn(100L);
        when(customFavorite.getMember()).thenReturn(customMember);
        when(customFavorite.getDog()).thenReturn(customDog);

        when(favoriteValidator.validateAndGetMember(memberId)).thenReturn(customMember);
        when(favoriteValidator.validateAndGetDog(dogId)).thenReturn(customDog);
        doNothing().when(duplicateChecker).checkDuplicate(memberId, dogId);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(customFavorite);

        // When
        FavoriteResponseDto result = favoriteService.addFavorite(customRequest, customUser);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getFavoriteId());
        assertEquals(memberId, result.getMemberId());
        assertEquals(dogId, result.getDogId());

        verify(favoriteValidator).validateAndGetMember(memberId);
        verify(favoriteValidator).validateAndGetDog(dogId);
        verify(duplicateChecker).checkDuplicate(memberId, dogId);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("즐겨찾기 서비스 호출 순서 확인")
    void addFavorite_VerifyCallOrder() {
        // Given
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(1L)).thenReturn(mockDog);
        doNothing().when(duplicateChecker).checkDuplicate(1L, 1L);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(mockFavorite);

        // When
        favoriteService.addFavorite(addRequestDto, sessionUser);

        // Then - 호출 순서 확인
        var inOrder = inOrder(favoriteValidator, duplicateChecker, favoriteRepository);
        inOrder.verify(favoriteValidator).validateAndGetMember(1L);
        inOrder.verify(favoriteValidator).validateAndGetDog(1L);
        inOrder.verify(duplicateChecker).checkDuplicate(1L, 1L);
        inOrder.verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("즐겨찾기 제거 서비스 호출 순서 확인")
    void removeFavorite_VerifyCallOrder() {
        // Given
        Long dogId = 1L;
        when(favoriteValidator.validateAndGetMember(1L)).thenReturn(mockMember);
        when(favoriteValidator.validateAndGetDog(dogId)).thenReturn(mockDog);
        when(duplicateChecker.exists(1L, dogId)).thenReturn(true);

        // When
        favoriteService.removeFavorite(dogId, sessionUser);

        // Then - 호출 순서 확인
        var inOrder = inOrder(favoriteValidator, duplicateChecker, favoriteRepository);
        inOrder.verify(favoriteValidator).validateAndGetMember(1L);
        inOrder.verify(favoriteValidator).validateAndGetDog(dogId);
        inOrder.verify(duplicateChecker).exists(1L, dogId);
        inOrder.verify(favoriteRepository).deleteByMemberMemberIdAndDogDogId(1L, dogId);
    }

    // Helper methods
    private FavoriteAddRequestDto createMockAddRequestDto() {
        FavoriteAddRequestDto mock = mock(FavoriteAddRequestDto.class);
        when(mock.getDogId()).thenReturn(1L);
        return mock;
    }

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
        when(favorite.getCreatedAt()).thenReturn(LocalDateTime.now());
        return favorite;
    }
}