package com.example.petner.domain.member.service;

import com.example.petner.domain.auth.dto.KakaoUserInfo;
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
}