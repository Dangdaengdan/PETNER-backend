package com.example.petner.domain.like.service;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.global.exception.customException.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 좋아요 관련 검증 로직을 담당하는 컴포넌트
 * Single Responsibility Principle(SRP)을 준수하여 검증 로직만 담당
 */
@Component
@RequiredArgsConstructor
public class PostLikeValidator {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시물 존재 여부 검증 및 조회
     */
    public Post validateAndGetPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    }

    /**
     * 회원 존재 여부 검증 및 조회
     */
    public Member validateAndGetMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
}