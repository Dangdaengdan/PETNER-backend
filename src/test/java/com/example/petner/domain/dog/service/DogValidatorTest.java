package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.breed.repository.BreedRepository;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.shelter.repository.ShelterRepository;
import com.example.petner.global.dto.SessionUser;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogValidatorTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private ShelterRepository shelterRepository;

    @InjectMocks
    private DogValidator dogValidator;

    private SessionUser sessionUser;
    private Member member;
    private Breed breed;
    private Shelter shelter;
    private Dog dog;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("user@example.com")
                .nickname("사용자")
                .build();

        member = Member.builder()
                .kakaoId("12345")
                .email("user@example.com")
                .nickname("사용자")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        breed = Breed.builder()
                .name("골든 리트리버")
                .build();
        ReflectionTestUtils.setField(breed, "breedId", 1L);

        Location location = Location.builder()
                .state("서울시")
                .district("강남구")
                .build();

        shelter = Shelter.builder()
                .name("서울시 강남구 동물보호센터")
                .location(location)
                .shelterContact("02-1234-5678")
                .build();
        ReflectionTestUtils.setField(shelter, "shelterId", 1L);

        dog = Dog.builder()
                .name("바둑이")
                .member(member)
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);
    }

    @Test
    @DisplayName("사용자 검증 및 조회 성공")
    void validateAndGetMember_Success() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // When
        Member result = dogValidator.validateAndGetMember(sessionUser);

        // Then
        assertThat(result).isEqualTo(member);
        verify(memberRepository).findById(1L);
    }

    @Test
    @DisplayName("사용자 검증 실패 - 존재하지 않는 사용자")
    void validateAndGetMember_NotFound() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogValidator.validateAndGetMember(sessionUser))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);

        verify(memberRepository).findById(1L);
    }

    @Test
    @DisplayName("견종 검증 및 조회 성공")
    void validateAndGetBreed_Success() {
        // Given
        when(breedRepository.findById(1L)).thenReturn(Optional.of(breed));

        // When
        Breed result = dogValidator.validateAndGetBreed(1L);

        // Then
        assertThat(result).isEqualTo(breed);
        verify(breedRepository).findById(1L);
    }

    @Test
    @DisplayName("견종 검증 실패 - 존재하지 않는 견종")
    void validateAndGetBreed_NotFound() {
        // Given
        when(breedRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogValidator.validateAndGetBreed(1L))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_BREED_NOT_FOUND);

        verify(breedRepository).findById(1L);
    }

    @Test
    @DisplayName("보호소 검증 및 조회 성공")
    void validateAndGetShelter_Success() {
        // Given
        when(shelterRepository.findById(1L)).thenReturn(Optional.of(shelter));

        // When
        Shelter result = dogValidator.validateAndGetShelter(1L);

        // Then
        assertThat(result).isEqualTo(shelter);
        verify(shelterRepository).findById(1L);
    }

    @Test
    @DisplayName("보호소 검증 실패 - 존재하지 않는 보호소")
    void validateAndGetShelter_NotFound() {
        // Given
        when(shelterRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogValidator.validateAndGetShelter(1L))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_SHELTER_NOT_FOUND);

        verify(shelterRepository).findById(1L);
    }

    @Test
    @DisplayName("보호소 검증 - null ID 처리")
    void validateAndGetShelter_NullId() {
        // When
        Shelter result = dogValidator.validateAndGetShelter(null);

        // Then
        assertThat(result).isNull();
        verify(shelterRepository, never()).findById(any());
    }

    @Test
    @DisplayName("유기견 접근 권한 검증 성공")
    void validateDogAccess_Success() {
        // Given
        // dog의 member는 이미 setUp에서 설정됨 (memberId = 1L)

        // When & Then (예외 발생하지 않아야 함)
        dogValidator.validateDogAccess(dog, sessionUser);
    }

    @Test
    @DisplayName("유기견 접근 권한 검증 실패 - 다른 사용자")
    void validateDogAccess_AccessDenied() {
        // Given
        SessionUser otherUser = SessionUser.builder()
                .memberId(2L)
                .email("other@example.com")
                .nickname("다른사용자")
                .build();

        // When & Then
        assertThatThrownBy(() -> dogValidator.validateDogAccess(dog, otherUser))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_ACCESS_DENIED);
    }

    @Test
    @DisplayName("유기견 접근 권한 검증 - 소유자 확인")
    void validateDogAccess_OwnerCheck() {
        // Given
        Member owner = Member.builder()
                .kakaoId("owner123")
                .email("owner@example.com")
                .nickname("소유자")
                .build();
        ReflectionTestUtils.setField(owner, "memberId", 2L);

        Dog ownedDog = Dog.builder()
                .name("내강아지")
                .member(owner)
                .build();

        SessionUser ownerSession = SessionUser.builder()
                .memberId(2L)
                .email("owner@example.com")
                .nickname("소유자")
                .build();

        // When & Then (소유자이므로 예외 발생하지 않아야 함)
        dogValidator.validateDogAccess(ownedDog, ownerSession);
    }

    @Test
    @DisplayName("다양한 멤버 ID로 접근 권한 테스트")
    void validateDogAccess_VariousMemberIds() {
        // Given
        for (long memberId = 1L; memberId <= 3L; memberId++) {
            Member testMember = Member.builder()
                    .kakaoId("kakao" + memberId)
                    .email("test" + memberId + "@example.com")
                    .nickname("사용자" + memberId)
                    .build();
            ReflectionTestUtils.setField(testMember, "memberId", memberId);

            Dog testDog = Dog.builder()
                    .name("강아지" + memberId)
                    .member(testMember)
                    .build();

            SessionUser testSession = SessionUser.builder()
                    .memberId(memberId)
                    .email("test" + memberId + "@example.com")
                    .nickname("사용자" + memberId)
                    .build();

            // When & Then (같은 memberId이므로 예외 발생하지 않아야 함)
            dogValidator.validateDogAccess(testDog, testSession);
        }
    }

    @Test
    @DisplayName("견종 Repository 호출 확인")
    void breedRepository_CallVerification() {
        // Given
        when(breedRepository.findById(1L)).thenReturn(Optional.of(breed));

        // When
        dogValidator.validateAndGetBreed(1L);

        // Then
        verify(breedRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(breedRepository);
    }

    @Test
    @DisplayName("보호소 Repository 호출 확인")
    void shelterRepository_CallVerification() {
        // Given
        when(shelterRepository.findById(1L)).thenReturn(Optional.of(shelter));

        // When
        dogValidator.validateAndGetShelter(1L);

        // Then
        verify(shelterRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(shelterRepository);
    }

    @Test
    @DisplayName("멤버 Repository 호출 확인")
    void memberRepository_CallVerification() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // When
        dogValidator.validateAndGetMember(sessionUser);

        // Then
        verify(memberRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(memberRepository);
    }
}