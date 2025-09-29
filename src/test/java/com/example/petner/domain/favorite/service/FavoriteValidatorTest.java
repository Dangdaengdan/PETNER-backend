package com.example.petner.domain.favorite.service;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.repository.DogRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FavoriteValidatorTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DogRepository dogRepository;

    @InjectMocks
    private FavoriteValidator favoriteValidator;

    private Member mockMember;
    private Dog mockDog;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockDog = createMockDog();
    }

    @Test
    @DisplayName("멤버 검증 및 반환 성공")
    void validateAndGetMember_Success() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // When
        Member result = favoriteValidator.validateAndGetMember(memberId);

        // Then
        assertNotNull(result);
        assertEquals(mockMember, result);
        assertEquals(1L, result.getMemberId());
        assertEquals("testUser", result.getNickname());
        assertEquals("test@example.com", result.getEmail());

        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("멤버 검증 실패 - 멤버 없음")
    void validateAndGetMember_MemberNotFound() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> favoriteValidator.validateAndGetMember(memberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("강아지 검증 및 반환 성공")
    void validateAndGetDog_Success() {
        // Given
        Long dogId = 1L;
        when(dogRepository.findById(dogId)).thenReturn(Optional.of(mockDog));

        // When
        Dog result = favoriteValidator.validateAndGetDog(dogId);

        // Then
        assertNotNull(result);
        assertEquals(mockDog, result);
        assertEquals(1L, result.getDogId());
        assertEquals("테스트강아지", result.getName());

        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("강아지 검증 실패 - 강아지 없음")
    void validateAndGetDog_DogNotFound() {
        // Given
        Long dogId = 999L;
        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> favoriteValidator.validateAndGetDog(dogId));

        assertEquals(ErrorCode.DOG_NOT_FOUND, exception.getErrorCode());
        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("멤버 검증 - 높은 ID 값")
    void validateAndGetMember_WithHighId() {
        // Given
        Long highMemberId = Long.MAX_VALUE;
        when(memberRepository.findById(highMemberId)).thenReturn(Optional.of(mockMember));

        // When
        Member result = favoriteValidator.validateAndGetMember(highMemberId);

        // Then
        assertNotNull(result);
        assertEquals(mockMember, result);
        verify(memberRepository).findById(highMemberId);
    }

    @Test
    @DisplayName("강아지 검증 - 높은 ID 값")
    void validateAndGetDog_WithHighId() {
        // Given
        Long highDogId = Long.MAX_VALUE;
        when(dogRepository.findById(highDogId)).thenReturn(Optional.of(mockDog));

        // When
        Dog result = favoriteValidator.validateAndGetDog(highDogId);

        // Then
        assertNotNull(result);
        assertEquals(mockDog, result);
        verify(dogRepository).findById(highDogId);
    }

    @Test
    @DisplayName("멤버 검증 - 0 ID 값")
    void validateAndGetMember_WithZeroId() {
        // Given
        Long zeroMemberId = 0L;
        when(memberRepository.findById(zeroMemberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> favoriteValidator.validateAndGetMember(zeroMemberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(zeroMemberId);
    }

    @Test
    @DisplayName("강아지 검증 - 0 ID 값")
    void validateAndGetDog_WithZeroId() {
        // Given
        Long zeroDogId = 0L;
        when(dogRepository.findById(zeroDogId)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> favoriteValidator.validateAndGetDog(zeroDogId));

        assertEquals(ErrorCode.DOG_NOT_FOUND, exception.getErrorCode());
        verify(dogRepository).findById(zeroDogId);
    }

    @Test
    @DisplayName("멤버 검증 - 음수 ID 값")
    void validateAndGetMember_WithNegativeId() {
        // Given
        Long negativeMemberId = -1L;
        when(memberRepository.findById(negativeMemberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> favoriteValidator.validateAndGetMember(negativeMemberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(negativeMemberId);
    }

    @Test
    @DisplayName("강아지 검증 - 음수 ID 값")
    void validateAndGetDog_WithNegativeId() {
        // Given
        Long negativeDogId = -1L;
        when(dogRepository.findById(negativeDogId)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> favoriteValidator.validateAndGetDog(negativeDogId));

        assertEquals(ErrorCode.DOG_NOT_FOUND, exception.getErrorCode());
        verify(dogRepository).findById(negativeDogId);
    }

    @Test
    @DisplayName("멤버 검증 - 저장소 예외 처리")
    void validateAndGetMember_RepositoryException() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteValidator.validateAndGetMember(memberId));

        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("강아지 검증 - 저장소 예외 처리")
    void validateAndGetDog_RepositoryException() {
        // Given
        Long dogId = 1L;
        when(dogRepository.findById(dogId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteValidator.validateAndGetDog(dogId));

        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("멤버와 강아지 연속 검증")
    void validateMemberAndDog_Sequential() {
        // Given
        Long memberId = 1L;
        Long dogId = 2L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(dogRepository.findById(dogId)).thenReturn(Optional.of(mockDog));

        // When
        Member memberResult = favoriteValidator.validateAndGetMember(memberId);
        Dog dogResult = favoriteValidator.validateAndGetDog(dogId);

        // Then
        assertNotNull(memberResult);
        assertNotNull(dogResult);
        assertEquals(mockMember, memberResult);
        assertEquals(mockDog, dogResult);

        verify(memberRepository).findById(memberId);
        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("멤버 검증 성공, 강아지 검증 실패")
    void validateMemberSuccess_DogFail() {
        // Given
        Long memberId = 1L;
        Long dogId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        // When
        Member memberResult = favoriteValidator.validateAndGetMember(memberId);

        // Then
        assertNotNull(memberResult);
        assertEquals(mockMember, memberResult);

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> favoriteValidator.validateAndGetDog(dogId));

        assertEquals(ErrorCode.DOG_NOT_FOUND, exception.getErrorCode());

        verify(memberRepository).findById(memberId);
        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("멤버 검증 실패, 강아지 검증 성공")
    void validateMemberFail_DogSuccess() {
        // Given
        Long memberId = 999L;
        Long dogId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        when(dogRepository.findById(dogId)).thenReturn(Optional.of(mockDog));

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> favoriteValidator.validateAndGetMember(memberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

        // When
        Dog dogResult = favoriteValidator.validateAndGetDog(dogId);

        // Then
        assertNotNull(dogResult);
        assertEquals(mockDog, dogResult);

        verify(memberRepository).findById(memberId);
        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("동일한 ID로 여러 번 검증")
    void validateSameIdMultipleTimes() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(dogRepository.findById(dogId)).thenReturn(Optional.of(mockDog));

        // When
        Member result1 = favoriteValidator.validateAndGetMember(memberId);
        Member result2 = favoriteValidator.validateAndGetMember(memberId);
        Dog dogResult1 = favoriteValidator.validateAndGetDog(dogId);
        Dog dogResult2 = favoriteValidator.validateAndGetDog(dogId);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(dogResult1);
        assertNotNull(dogResult2);
        assertEquals(result1, result2);
        assertEquals(dogResult1, dogResult2);

        verify(memberRepository, times(2)).findById(memberId);
        verify(dogRepository, times(2)).findById(dogId);
    }

    @Test
    @DisplayName("예외 메시지 검증 - 멤버 없음")
    void validateAndGetMember_ExceptionMessage() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> favoriteValidator.validateAndGetMember(memberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("예외 메시지 검증 - 강아지 없음")
    void validateAndGetDog_ExceptionMessage() {
        // Given
        Long dogId = 999L;
        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        // When & Then
        DogException exception = assertThrows(DogException.class,
                () -> favoriteValidator.validateAndGetDog(dogId));

        assertEquals(ErrorCode.DOG_NOT_FOUND, exception.getErrorCode());
        assertNotNull(exception.getMessage());
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
}