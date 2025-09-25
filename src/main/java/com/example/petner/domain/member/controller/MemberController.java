package com.example.petner.domain.member.controller;

import com.example.petner.domain.member.dto.request.ProfileCompleteRequestDto;
import com.example.petner.domain.member.dto.request.ProfileUpdateRequestDto;
import com.example.petner.domain.member.dto.response.ProfileResponseDto;
import com.example.petner.domain.member.dto.response.ValidationResponseDto;
import com.example.petner.domain.member.service.MemberService;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "회원 (Members)", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/profile/complete")
    @Operation(summary = "프로필 완성", description = "카카오 로그인 후 추가 정보를 입력하여 프로필을 완성합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 완성 성공")
    public ResponseEntity<ProfileResponseDto> completeProfile(
            @Valid @RequestBody ProfileCompleteRequestDto request,
            @SessionMember SessionUser user) {
        
        ProfileResponseDto response = memberService.completeProfile(user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    public ResponseEntity<ProfileResponseDto> getProfile(@SessionMember SessionUser user) {
        ProfileResponseDto response = memberService.getProfile(user.getMemberId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정", description = "사용자 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 수정 성공")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @Valid @RequestBody ProfileUpdateRequestDto request,
            @SessionMember SessionUser user) {
        
        ProfileResponseDto response = memberService.updateProfile(user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/nickname")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임의 중복 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "중복 확인 완료")
    public ResponseEntity<ValidationResponseDto> checkNicknameDuplicate(
            @Parameter(description = "확인할 닉네임", example = "펫러버")
            @RequestParam String nickname) {
        
        ValidationResponseDto response = memberService.checkNicknameDuplicate(nickname);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/email")
    @Operation(summary = "이메일 중복 확인", description = "이메일의 중복 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "중복 확인 완료")
    public ResponseEntity<ValidationResponseDto> checkEmailDuplicate(
            @Parameter(description = "확인할 이메일", example = "user@example.com")
            @RequestParam String email) {
        
        ValidationResponseDto response = memberService.checkEmailDuplicate(email);
        return ResponseEntity.ok(response);
    }
}