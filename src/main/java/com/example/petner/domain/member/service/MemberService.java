package com.example.petner.domain.member.service;

import com.example.petner.domain.auth.dto.KakaoUserInfo;
import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.location.repository.LocationRepository;
import com.example.petner.domain.member.dto.request.ProfileCompleteRequestDto;
import com.example.petner.domain.member.dto.request.ProfileUpdateRequestDto;
import com.example.petner.domain.member.dto.response.ProfileResponseDto;
import com.example.petner.domain.member.dto.response.ValidationResponseDto;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;

    /**
     * 카카오 사용자 정보로 회원을 찾거나 새로 생성합니다.
     * 
     * @param kakaoUserInfo 카카오 사용자 정보
     * @return 회원 엔티티
     */
    @Transactional
    public Member findOrCreateMember(KakaoUserInfo kakaoUserInfo) {
        return memberRepository.findByKakaoId(kakaoUserInfo.getKakaoId())
                .orElseGet(() -> createNewMember(kakaoUserInfo));
    }

    /**
     * 회원 ID로 회원을 조회합니다.
     * 
     * @param memberId 회원 ID
     * @return 회원 엔티티
     * @throws MemberException 회원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * N+1 문제 해결을 위한 Location과 함께 회원을 조회합니다.
     * 
     * @param memberId 회원 ID
     * @return 회원 엔티티 (Location fetch join)
     * @throws MemberException 회원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Member findByIdWithLocation(Long memberId) {
        return memberRepository.findByIdWithLocation(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 새로운 임시 회원을 생성합니다.
     * 
     * @param kakaoUserInfo 카카오 사용자 정보
     * @return 생성된 회원 엔티티
     * @throws MemberException 회원 생성 실패 시
     */
    private Member createNewMember(KakaoUserInfo kakaoUserInfo) {
        try {
            // 카카오 정보로 임시 회원 생성 (kakaoId만 저장)
            Member newMember = Member.createTemporaryMember(
                kakaoUserInfo.getKakaoId()
            );
            
            Member savedMember = memberRepository.save(newMember);
            log.info("[회원가입] 임시 회원 생성 완료: memberId={}", savedMember.getMemberId());
            
            return savedMember;
        } catch (DataIntegrityViolationException e) {
            // 실제 중복 사용자인 경우 (DB 제약 조건 위반)
            log.warn("[회원가입] 중복 사용자 감지 - 카카오 로그인 재시도");
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
        } catch (Exception e) {
            // 기타 시스템 에러
            log.error("[회원가입] 시스템 에러로 인한 회원 생성 실패: 예외타입={}", 
                e.getClass().getSimpleName(), e);
            throw new MemberException(ErrorCode.MEMBER_CREATION_FAILED);
        }
    }

    /**
     * 프로필 완성
     * 
     * @param memberId 회원 ID
     * @param request 프로필 완성 요청 데이터
     * @return 완성된 프로필 정보
     * @throws MemberException 회원을 찾을 수 없거나 중복된 정보가 있는 경우
     */
    @Transactional
    public ProfileResponseDto completeProfile(Long memberId, ProfileCompleteRequestDto request) {
        // 회원 조회
        Member member = findById(memberId);
        
        // 이메일 중복 확인 (자신 제외) - 최적화된 쿼리
        if (memberRepository.existsByEmailAndMemberIdNot(request.getEmail(), memberId)) {
            throw new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATE);
        }
        
        // 닉네임 중복 확인 (자신 제외) - 최적화된 쿼리
        if (memberRepository.existsByNicknameAndMemberIdNot(request.getNickname(), memberId)) {
            throw new MemberException(ErrorCode.MEMBER_NICKNAME_DUPLICATE);
        }
        
        // 위치 정보 조회
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new MemberException(ErrorCode.LOCATION_NOT_FOUND));
        
        // 프로필 완성
        member.completeProfile(
            request.getEmail(),
            request.getNickname(),
            request.getGender(),
            request.getHousingType(),
            request.getContact(),
            location
        );
        
        Member savedMember = memberRepository.save(member);
        log.info("[프로필 완성] 회원 프로필 완성: memberId={}", memberId);
        
        // N+1 문제 방지를 위해 fetch join으로 다시 조회
        Member memberWithLocation = findByIdWithLocation(savedMember.getMemberId());
        return ProfileResponseDto.from(memberWithLocation);
    }

    /**
     * 프로필 조회 (N+1 문제 해결)
     * 
     * @param memberId 회원 ID
     * @return 프로필 정보
     * @throws MemberException 회원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long memberId) {
        Member member = findByIdWithLocation(memberId);
        return ProfileResponseDto.from(member);
    }

    /**
     * 프로필 수정
     * 
     * @param memberId 회원 ID
     * @param request 프로필 수정 요청 데이터
     * @return 수정된 프로필 정보
     * @throws MemberException 회원을 찾을 수 없거나 중복된 정보가 있는 경우
     */
    @Transactional
    public ProfileResponseDto updateProfile(Long memberId, ProfileUpdateRequestDto request) {
        Member member = findById(memberId);
        
        // 이메일 중복 확인 (변경하려는 경우만) - 최적화된 쿼리
        if (request.getEmail() != null && 
            !request.getEmail().equals(member.getEmail()) &&
            memberRepository.existsByEmailAndMemberIdNot(request.getEmail(), memberId)) {
            throw new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATE);
        }
        
        // 닉네임 중복 확인 (변경하려는 경우만) - 최적화된 쿼리
        if (request.getNickname() != null && 
            !request.getNickname().equals(member.getNickname()) &&
            memberRepository.existsByNicknameAndMemberIdNot(request.getNickname(), memberId)) {
            throw new MemberException(ErrorCode.MEMBER_NICKNAME_DUPLICATE);
        }
        
        // 위치 정보 조회 (변경하려는 경우만)
        Location location = null;
        if (request.getLocationId() != null) {
            location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new MemberException(ErrorCode.LOCATION_NOT_FOUND));
        }
        
        // 프로필 업데이트
        member.updateProfile(
            request.getEmail(),
            request.getNickname(),
            request.getGender(),
            request.getHousingType(),
            request.getContact(),
            location
        );
        
        Member savedMember = memberRepository.save(member);
        log.info("[프로필 수정] 회원 프로필 수정: memberId={}", memberId);
        
        // N+1 문제 방지를 위해 fetch join으로 다시 조회
        Member memberWithLocation = findByIdWithLocation(savedMember.getMemberId());
        return ProfileResponseDto.from(memberWithLocation);
    }

    /**
     * 닉네임 중복 확인
     * 
     * @param nickname 확인할 닉네임
     * @return 중복 확인 결과
     */
    @Transactional(readOnly = true)
    public ValidationResponseDto checkNicknameDuplicate(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        return exists ? 
            ValidationResponseDto.unavailable("닉네임") : 
            ValidationResponseDto.available("닉네임");
    }

    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일
     * @return 중복 확인 결과
     */
    @Transactional(readOnly = true)
    public ValidationResponseDto checkEmailDuplicate(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        return exists ? 
            ValidationResponseDto.unavailable("이메일") : 
            ValidationResponseDto.available("이메일");
    }
}