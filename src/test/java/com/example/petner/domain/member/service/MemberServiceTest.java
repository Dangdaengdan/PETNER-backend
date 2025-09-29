package com.example.petner.domain.member.service;

import com.example.petner.domain.auth.dto.KakaoUserInfo;
import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.location.repository.LocationRepository;
import com.example.petner.domain.member.common.HousingType;
import com.example.petner.domain.member.dto.request.ProfileCompleteRequestDto;
import com.example.petner.domain.member.dto.request.ProfileUpdateRequestDto;
import com.example.petner.domain.member.dto.response.ProfileResponseDto;
import com.example.petner.domain.member.dto.response.ValidationResponseDto;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.config.common.Gender;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private Member temporaryMember;
    private Location location;
    private ProfileCompleteRequestDto profileCompleteRequest;
    private ProfileUpdateRequestDto profileUpdateRequest;
    private KakaoUserInfo kakaoUserInfo;

    @BeforeEach
    void setUp() {
        location = createLocation();
        member = createMember();
        temporaryMember = createTemporaryMember();
        profileCompleteRequest = createProfileCompleteRequest();
        profileUpdateRequest = createProfileUpdateRequest();
        kakaoUserInfo = createKakaoUserInfo();
    }

    @Test
    @DisplayName("카카오 사용자 정보로 기존 회원 찾기 성공")
    void findOrCreateMember_ExistingMember() {
        // Given
        when(memberRepository.findByKakaoId(kakaoUserInfo.getKakaoId()))
                .thenReturn(Optional.of(member));

        // When
        Member result = memberService.findOrCreateMember(kakaoUserInfo);

        // Then
        assertEquals(member.getMemberId(), result.getMemberId());
        assertEquals(member.getKakaoId(), result.getKakaoId());
        verify(memberRepository).findByKakaoId(kakaoUserInfo.getKakaoId());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("카카오 사용자 정보로 새 회원 생성 성공")
    void findOrCreateMember_NewMember() {
        // Given
        when(memberRepository.findByKakaoId(kakaoUserInfo.getKakaoId()))
                .thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class)))
                .thenReturn(temporaryMember);

        // When
        Member result = memberService.findOrCreateMember(kakaoUserInfo);

        // Then
        assertEquals(temporaryMember.getMemberId(), result.getMemberId());
        assertEquals(temporaryMember.getKakaoId(), result.getKakaoId());
        verify(memberRepository).findByKakaoId(kakaoUserInfo.getKakaoId());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("새 회원 생성 실패 - 중복 사용자")
    void findOrCreateMember_DuplicateUser() {
        // Given
        when(memberRepository.findByKakaoId(kakaoUserInfo.getKakaoId()))
                .thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.findOrCreateMember(kakaoUserInfo));

        assertEquals(ErrorCode.MEMBER_ALREADY_EXISTS, exception.getErrorCode());
        verify(memberRepository).findByKakaoId(kakaoUserInfo.getKakaoId());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("새 회원 생성 실패 - 시스템 오류")
    void findOrCreateMember_SystemError() {
        // Given
        when(memberRepository.findByKakaoId(kakaoUserInfo.getKakaoId()))
                .thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new RuntimeException("System error"));

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.findOrCreateMember(kakaoUserInfo));

        assertEquals(ErrorCode.MEMBER_CREATION_FAILED, exception.getErrorCode());
        verify(memberRepository).findByKakaoId(kakaoUserInfo.getKakaoId());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 ID로 조회 성공")
    void findById_Success() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When
        Member result = memberService.findById(memberId);

        // Then
        assertEquals(member.getMemberId(), result.getMemberId());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("회원 ID로 조회 실패 - 회원 없음")
    void findById_MemberNotFound() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.findById(memberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("Location과 함께 회원 조회 성공")
    void findByIdWithLocation_Success() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findByIdWithLocation(memberId)).thenReturn(Optional.of(member));

        // When
        Member result = memberService.findByIdWithLocation(memberId);

        // Then
        assertEquals(member.getMemberId(), result.getMemberId());
        verify(memberRepository).findByIdWithLocation(memberId);
    }

    @Test
    @DisplayName("Location과 함께 회원 조회 실패 - 회원 없음")
    void findByIdWithLocation_MemberNotFound() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findByIdWithLocation(memberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.findByIdWithLocation(memberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findByIdWithLocation(memberId);
    }

    @Test
    @DisplayName("프로필 완성 성공")
    void completeProfile_Success() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(temporaryMember));
        when(memberRepository.existsByEmailAndMemberIdNot(profileCompleteRequest.getEmail(), memberId))
                .thenReturn(false);
        when(memberRepository.existsByNicknameAndMemberIdNot(profileCompleteRequest.getNickname(), memberId))
                .thenReturn(false);
        when(locationRepository.findById(profileCompleteRequest.getLocationId()))
                .thenReturn(Optional.of(location));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findByIdWithLocation(memberId)).thenReturn(Optional.of(member));

        // When
        ProfileResponseDto result = memberService.completeProfile(memberId, profileCompleteRequest);

        // Then
        assertNotNull(result);
        assertEquals(member.getMemberId(), result.getMemberId());
        assertEquals(member.getEmail(), result.getEmail());
        assertEquals(member.getNickname(), result.getNickname());

        verify(memberRepository).findById(memberId);
        verify(memberRepository).existsByEmailAndMemberIdNot(profileCompleteRequest.getEmail(), memberId);
        verify(memberRepository).existsByNicknameAndMemberIdNot(profileCompleteRequest.getNickname(), memberId);
        verify(locationRepository).findById(profileCompleteRequest.getLocationId());
        verify(memberRepository).save(any(Member.class));
        verify(memberRepository).findByIdWithLocation(memberId);
    }

    @Test
    @DisplayName("프로필 완성 실패 - 회원 없음")
    void completeProfile_MemberNotFound() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.completeProfile(memberId, profileCompleteRequest));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("프로필 완성 실패 - 이메일 중복")
    void completeProfile_EmailDuplicate() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(temporaryMember));
        when(memberRepository.existsByEmailAndMemberIdNot(profileCompleteRequest.getEmail(), memberId))
                .thenReturn(true);

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.completeProfile(memberId, profileCompleteRequest));

        assertEquals(ErrorCode.MEMBER_EMAIL_DUPLICATE, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
        verify(memberRepository).existsByEmailAndMemberIdNot(profileCompleteRequest.getEmail(), memberId);
    }

    @Test
    @DisplayName("프로필 완성 실패 - 닉네임 중복")
    void completeProfile_NicknameDuplicate() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(temporaryMember));
        when(memberRepository.existsByEmailAndMemberIdNot(profileCompleteRequest.getEmail(), memberId))
                .thenReturn(false);
        when(memberRepository.existsByNicknameAndMemberIdNot(profileCompleteRequest.getNickname(), memberId))
                .thenReturn(true);

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.completeProfile(memberId, profileCompleteRequest));

        assertEquals(ErrorCode.MEMBER_NICKNAME_DUPLICATE, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
        verify(memberRepository).existsByEmailAndMemberIdNot(profileCompleteRequest.getEmail(), memberId);
        verify(memberRepository).existsByNicknameAndMemberIdNot(profileCompleteRequest.getNickname(), memberId);
    }

    @Test
    @DisplayName("프로필 완성 실패 - 위치 정보 없음")
    void completeProfile_LocationNotFound() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(temporaryMember));
        when(memberRepository.existsByEmailAndMemberIdNot(profileCompleteRequest.getEmail(), memberId))
                .thenReturn(false);
        when(memberRepository.existsByNicknameAndMemberIdNot(profileCompleteRequest.getNickname(), memberId))
                .thenReturn(false);
        when(locationRepository.findById(profileCompleteRequest.getLocationId()))
                .thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.completeProfile(memberId, profileCompleteRequest));

        assertEquals(ErrorCode.LOCATION_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
        verify(locationRepository).findById(profileCompleteRequest.getLocationId());
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getProfile_Success() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findByIdWithLocation(memberId)).thenReturn(Optional.of(member));

        // When
        ProfileResponseDto result = memberService.getProfile(memberId);

        // Then
        assertNotNull(result);
        assertEquals(member.getMemberId(), result.getMemberId());
        assertEquals(member.getEmail(), result.getEmail());
        assertEquals(member.getNickname(), result.getNickname());
        verify(memberRepository).findByIdWithLocation(memberId);
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile_Success() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.existsByEmailAndMemberIdNot(profileUpdateRequest.getEmail(), memberId))
                .thenReturn(false);
        when(memberRepository.existsByNicknameAndMemberIdNot(profileUpdateRequest.getNickname(), memberId))
                .thenReturn(false);
        when(locationRepository.findById(profileUpdateRequest.getLocationId()))
                .thenReturn(Optional.of(location));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findByIdWithLocation(memberId)).thenReturn(Optional.of(member));

        // When
        ProfileResponseDto result = memberService.updateProfile(memberId, profileUpdateRequest);

        // Then
        assertNotNull(result);
        assertEquals(member.getMemberId(), result.getMemberId());
        verify(memberRepository).findById(memberId);
        verify(memberRepository).save(any(Member.class));
        verify(memberRepository).findByIdWithLocation(memberId);
    }

    @Test
    @DisplayName("프로필 수정 실패 - 이메일 중복")
    void updateProfile_EmailDuplicate() {
        // Given
        Long memberId = 1L;
        Member memberWithDifferentEmail = Member.builder()
                .kakaoId("different_kakao_id")
                .email("different@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .location(location)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberWithDifferentEmail));
        when(memberRepository.existsByEmailAndMemberIdNot(profileUpdateRequest.getEmail(), memberId))
                .thenReturn(true);

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.updateProfile(memberId, profileUpdateRequest));

        assertEquals(ErrorCode.MEMBER_EMAIL_DUPLICATE, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
        verify(memberRepository).existsByEmailAndMemberIdNot(profileUpdateRequest.getEmail(), memberId);
    }

    @Test
    @DisplayName("프로필 수정 - 같은 이메일은 중복 확인하지 않음")
    void updateProfile_SameEmailNoDuplicateCheck() {
        // Given
        Long memberId = 1L;
        ProfileUpdateRequestDto sameEmailRequest = new ProfileUpdateRequestDto(
                member.getEmail(), // 같은 이메일
                "새닉네임",
                Gender.FEMALE,
                HousingType.단독_주택,
                "010-9876-5432",
                2L
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.existsByNicknameAndMemberIdNot(sameEmailRequest.getNickname(), memberId))
                .thenReturn(false);
        when(locationRepository.findById(sameEmailRequest.getLocationId()))
                .thenReturn(Optional.of(location));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findByIdWithLocation(memberId)).thenReturn(Optional.of(member));

        // When
        ProfileResponseDto result = memberService.updateProfile(memberId, sameEmailRequest);

        // Then
        assertNotNull(result);
        verify(memberRepository).findById(memberId);
        verify(memberRepository, never()).existsByEmailAndMemberIdNot(any(), any());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("프로필 수정 - null 값 처리")
    void updateProfile_NullValues() {
        // Given
        Long memberId = 1L;
        ProfileUpdateRequestDto nullRequest = new ProfileUpdateRequestDto(
                null, null, null, null, null, null
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findByIdWithLocation(memberId)).thenReturn(Optional.of(member));

        // When
        ProfileResponseDto result = memberService.updateProfile(memberId, nullRequest);

        // Then
        assertNotNull(result);
        verify(memberRepository).findById(memberId);
        verify(memberRepository, never()).existsByEmailAndMemberIdNot(any(), any());
        verify(memberRepository, never()).existsByNicknameAndMemberIdNot(any(), any());
        verify(locationRepository, never()).findById(any());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 사용 가능")
    void checkNicknameDuplicate_Available() {
        // Given
        String nickname = "사용가능닉네임";
        when(memberRepository.existsByNickname(nickname)).thenReturn(false);

        // When
        ValidationResponseDto result = memberService.checkNicknameDuplicate(nickname);

        // Then
        assertTrue(result.isAvailable());
        assertEquals("사용 가능한 닉네임입니다.", result.getMessage());
        verify(memberRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복됨")
    void checkNicknameDuplicate_Duplicate() {
        // Given
        String nickname = "중복닉네임";
        when(memberRepository.existsByNickname(nickname)).thenReturn(true);

        // When
        ValidationResponseDto result = memberService.checkNicknameDuplicate(nickname);

        // Then
        assertFalse(result.isAvailable());
        assertEquals("이미 사용 중인 닉네임입니다.", result.getMessage());
        verify(memberRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 사용 가능")
    void checkEmailDuplicate_Available() {
        // Given
        String email = "available@example.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // When
        ValidationResponseDto result = memberService.checkEmailDuplicate(email);

        // Then
        assertTrue(result.isAvailable());
        assertEquals("사용 가능한 이메일입니다.", result.getMessage());
        verify(memberRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복됨")
    void checkEmailDuplicate_Duplicate() {
        // Given
        String email = "duplicate@example.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);

        // When
        ValidationResponseDto result = memberService.checkEmailDuplicate(email);

        // Then
        assertFalse(result.isAvailable());
        assertEquals("이미 사용 중인 이메일입니다.", result.getMessage());
        verify(memberRepository).existsByEmail(email);
    }

    // Helper methods
    private Member createMember() {
        Member member = Member.builder()
                .kakaoId("test_kakao_id")
                .email("test@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .location(location)
                .build();
        // Reflection을 이용하여 memberId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(member, 1L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }
        return member;
    }

    private Member createTemporaryMember() {
        Member member = Member.builder()
                .kakaoId("temp_kakao_id")
                .build();
        // Reflection을 이용하여 memberId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(member, 2L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }
        return member;
    }

    private Location createLocation() {
        Location location = Location.builder()
                .state("서울특별시")
                .district("강남구")
                .build();
        // Reflection을 이용하여 locationId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Location.class.getDeclaredField("locationId");
            field.setAccessible(true);
            field.set(location, 1L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }
        return location;
    }

    private ProfileCompleteRequestDto createProfileCompleteRequest() {
        return new ProfileCompleteRequestDto(
                "test@example.com",
                "테스트유저",
                Gender.MALE,
                HousingType.아파트,
                "010-1234-5678",
                1L
        );
    }

    private ProfileUpdateRequestDto createProfileUpdateRequest() {
        return new ProfileUpdateRequestDto(
                "updated@example.com",
                "업데이트유저",
                Gender.FEMALE,
                HousingType.단독_주택,
                "010-9876-5432",
                2L
        );
    }

    private KakaoUserInfo createKakaoUserInfo() {
        return KakaoUserInfo.builder()
                .kakaoId("test_kakao_id")
                .email("test@example.com")
                .nickname("카카오유저")
                .build();
    }
}