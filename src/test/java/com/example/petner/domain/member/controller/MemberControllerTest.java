package com.example.petner.domain.member.controller;

import com.example.petner.domain.member.common.HousingType;
import com.example.petner.domain.member.dto.request.ProfileCompleteRequestDto;
import com.example.petner.domain.member.dto.request.ProfileUpdateRequestDto;
import com.example.petner.domain.member.dto.response.ProfileResponseDto;
import com.example.petner.domain.member.dto.response.ValidationResponseDto;
import com.example.petner.domain.member.service.MemberService;
import com.example.petner.global.config.common.Gender;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private SessionUser sessionUser;
    private ProfileCompleteRequestDto profileCompleteRequest;
    private ProfileUpdateRequestDto profileUpdateRequest;
    private ProfileResponseDto profileResponseDto;
    private ValidationResponseDto validationResponseDto;

    @BeforeEach
    void setUp() {
        sessionUser = createSessionUser();
        profileCompleteRequest = createProfileCompleteRequest();
        profileUpdateRequest = createProfileUpdateRequest();
        profileResponseDto = createProfileResponseDto();
        validationResponseDto = createValidationResponseDto();
    }

    @Test
    @DisplayName("프로필 완성 성공")
    void completeProfile_Success() {
        // Given
        when(memberService.completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class)))
                .thenReturn(profileResponseDto);

        // When
        ResponseEntity<ProfileResponseDto> response = memberController.completeProfile(profileCompleteRequest, sessionUser);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(profileResponseDto.getMemberId(), response.getBody().getMemberId());

        verify(memberService).completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class));
    }

    @Test
    @DisplayName("프로필 완성 실패 - 이메일 중복")
    void completeProfile_EmailDuplicate() {
        // Given
        when(memberService.completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class)))
                .thenThrow(new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATE));

        // When & Then
        assertThrows(MemberException.class,
                () -> memberController.completeProfile(profileCompleteRequest, sessionUser));

        verify(memberService).completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class));
    }

    @Test
    @DisplayName("프로필 완성 실패 - 닉네임 중복")
    void completeProfile_NicknameDuplicate() {
        // Given
        when(memberService.completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class)))
                .thenThrow(new MemberException(ErrorCode.MEMBER_NICKNAME_DUPLICATE));

        // When & Then
        assertThrows(MemberException.class,
                () -> memberController.completeProfile(profileCompleteRequest, sessionUser));

        verify(memberService).completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class));
    }

    @Test
    @DisplayName("프로필 완성 실패 - 위치 정보 없음")
    void completeProfile_LocationNotFound() {
        // Given
        when(memberService.completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class)))
                .thenThrow(new MemberException(ErrorCode.LOCATION_NOT_FOUND));

        // When & Then
        assertThrows(MemberException.class,
                () -> memberController.completeProfile(profileCompleteRequest, sessionUser));

        verify(memberService).completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class));
    }

    @Test
    @DisplayName("프로필 완성 실패 - 회원 정보 없음")
    void completeProfile_MemberNotFound() {
        // Given
        when(memberService.completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class)))
                .thenThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // When & Then
        assertThrows(MemberException.class,
                () -> memberController.completeProfile(profileCompleteRequest, sessionUser));

        verify(memberService).completeProfile(eq(sessionUser.getMemberId()), any(ProfileCompleteRequestDto.class));
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getProfile_Success() {
        // Given
        when(memberService.getProfile(sessionUser.getMemberId())).thenReturn(profileResponseDto);

        // When
        ResponseEntity<ProfileResponseDto> response = memberController.getProfile(sessionUser);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(profileResponseDto.getMemberId(), response.getBody().getMemberId());
        assertEquals(profileResponseDto.getNickname(), response.getBody().getNickname());

        verify(memberService).getProfile(sessionUser.getMemberId());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 사용자 없음")
    void getProfile_MemberNotFound() {
        // Given
        when(memberService.getProfile(sessionUser.getMemberId()))
                .thenThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // When & Then
        assertThrows(MemberException.class,
                () -> memberController.getProfile(sessionUser));

        verify(memberService).getProfile(sessionUser.getMemberId());
    }

    @Test
    @DisplayName("프로필 조회 - SessionUser null")
    void getProfile_NullSessionUser() {
        // Given
        SessionUser nullSession = null;

        // When & Then
        assertThrows(NullPointerException.class,
                () -> memberController.getProfile(nullSession));

        verify(memberService, never()).getProfile(any());
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_Success() {
        // Given
        when(memberService.updateProfile(eq(sessionUser.getMemberId()), any(ProfileUpdateRequestDto.class)))
                .thenReturn(profileResponseDto);

        // When
        ResponseEntity<ProfileResponseDto> response = memberController.updateProfile(profileUpdateRequest, sessionUser);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(profileResponseDto.getMemberId(), response.getBody().getMemberId());

        verify(memberService).updateProfile(eq(sessionUser.getMemberId()), any(ProfileUpdateRequestDto.class));
    }

    @Test
    @DisplayName("프로필 업데이트 실패 - 사용자 없음")
    void updateProfile_MemberNotFound() {
        // Given
        when(memberService.updateProfile(eq(sessionUser.getMemberId()), any(ProfileUpdateRequestDto.class)))
                .thenThrow(new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // When & Then
        assertThrows(MemberException.class,
                () -> memberController.updateProfile(profileUpdateRequest, sessionUser));

        verify(memberService).updateProfile(eq(sessionUser.getMemberId()), any(ProfileUpdateRequestDto.class));
    }

    @Test
    @DisplayName("프로필 업데이트 실패 - 이메일 중복")
    void updateProfile_EmailDuplicate() {
        // Given
        when(memberService.updateProfile(eq(sessionUser.getMemberId()), any(ProfileUpdateRequestDto.class)))
                .thenThrow(new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATE));

        // When & Then
        assertThrows(MemberException.class,
                () -> memberController.updateProfile(profileUpdateRequest, sessionUser));

        verify(memberService).updateProfile(eq(sessionUser.getMemberId()), any(ProfileUpdateRequestDto.class));
    }

    @Test
    @DisplayName("닉네임 중복 체크 성공 - 사용 가능")
    void checkNicknameDuplicate_Available() {
        // Given
        String nickname = "사용가능닉네임";
        ValidationResponseDto availableDto = ValidationResponseDto.available("닉네임");
        when(memberService.checkNicknameDuplicate(nickname)).thenReturn(availableDto);

        // When
        ResponseEntity<ValidationResponseDto> response = memberController.checkNicknameDuplicate(nickname);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAvailable());
        assertEquals("사용 가능한 닉네임입니다.", response.getBody().getMessage());

        verify(memberService).checkNicknameDuplicate(nickname);
    }

    @Test
    @DisplayName("닉네임 중복 체크 성공 - 중복됨")
    void checkNicknameDuplicate_Duplicate() {
        // Given
        String nickname = "중복닉네임";
        ValidationResponseDto duplicateDto = ValidationResponseDto.unavailable("닉네임");
        when(memberService.checkNicknameDuplicate(nickname)).thenReturn(duplicateDto);

        // When
        ResponseEntity<ValidationResponseDto> response = memberController.checkNicknameDuplicate(nickname);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAvailable());
        assertEquals("이미 사용 중인 닉네임입니다.", response.getBody().getMessage());

        verify(memberService).checkNicknameDuplicate(nickname);
    }

    @Test
    @DisplayName("이메일 중복 체크 성공 - 사용 가능")
    void checkEmailDuplicate_Available() {
        // Given
        String email = "test@example.com";
        ValidationResponseDto availableDto = ValidationResponseDto.available("이메일");
        when(memberService.checkEmailDuplicate(email)).thenReturn(availableDto);

        // When
        ResponseEntity<ValidationResponseDto> response = memberController.checkEmailDuplicate(email);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAvailable());
        assertEquals("사용 가능한 이메일입니다.", response.getBody().getMessage());

        verify(memberService).checkEmailDuplicate(email);
    }

    @Test
    @DisplayName("이메일 중복 체크 성공 - 중복됨")
    void checkEmailDuplicate_Duplicate() {
        // Given
        String email = "duplicate@example.com";
        ValidationResponseDto duplicateDto = ValidationResponseDto.unavailable("이메일");
        when(memberService.checkEmailDuplicate(email)).thenReturn(duplicateDto);

        // When
        ResponseEntity<ValidationResponseDto> response = memberController.checkEmailDuplicate(email);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAvailable());
        assertEquals("이미 사용 중인 이메일입니다.", response.getBody().getMessage());

        verify(memberService).checkEmailDuplicate(email);
    }

    @Test
    @DisplayName("컨트롤러 응답 타입 검증")
    void responseTypeValidation() {
        // Given
        when(memberService.getProfile(sessionUser.getMemberId())).thenReturn(profileResponseDto);
        when(memberService.checkNicknameDuplicate("테스트")).thenReturn(validationResponseDto);

        // When
        ResponseEntity<ProfileResponseDto> profileResponse = memberController.getProfile(sessionUser);
        ResponseEntity<ValidationResponseDto> validationResponse = memberController.checkNicknameDuplicate("테스트");

        // Then
        assertTrue(profileResponse instanceof ResponseEntity);
        assertTrue(validationResponse instanceof ResponseEntity);
        assertTrue(profileResponse.getBody() instanceof ProfileResponseDto);
        assertTrue(validationResponse.getBody() instanceof ValidationResponseDto);

        verify(memberService).getProfile(sessionUser.getMemberId());
        verify(memberService).checkNicknameDuplicate("테스트");
    }

    @Test
    @DisplayName("세션 사용자 ID 전달 확인")
    void sessionUserIdPassing() {
        // Given
        Long memberId = 123L;
        SessionUser customSession = SessionUser.builder()
                .memberId(memberId)
                .email("test@example.com")
                .nickname("테스트")
                .build();
        when(memberService.getProfile(memberId)).thenReturn(profileResponseDto);

        // When
        memberController.getProfile(customSession);

        // Then
        verify(memberService).getProfile(memberId);
    }

    @Test
    @DisplayName("서비스 예외 전파 확인")
    void serviceExceptionPropagation() {
        // Given
        RuntimeException serviceException = new RuntimeException("Service error");
        when(memberService.getProfile(sessionUser.getMemberId())).thenThrow(serviceException);

        // When & Then
        assertThrows(RuntimeException.class,
                () -> memberController.getProfile(sessionUser));

        verify(memberService).getProfile(sessionUser.getMemberId());
    }

    // Helper methods
    private SessionUser createSessionUser() {
        return SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트사용자")
                .build();
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

    private ProfileResponseDto createProfileResponseDto() {
        return ProfileResponseDto.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .locationId(1L)
                .state("서울특별시")
                .district("강남구")
                .locationName("서울특별시 강남구")
                .profileCompleted(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ValidationResponseDto createValidationResponseDto() {
        return ValidationResponseDto.available("테스트");
    }
}