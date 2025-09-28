package com.example.petner.domain.like.service;

import com.example.petner.domain.like.dto.response.PostLikeResponseDto;
import com.example.petner.domain.like.entity.PostLike;
import com.example.petner.domain.like.repository.PostLikeRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.global.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 게시물 좋아요 서비스
 * 좋아요 추가/취소 및 조회 기능을 제공
 * SOLID 원칙을 준수하여 리팩토링된 버전
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostLikeValidator postLikeValidator;
    private final PostRepository postRepository;

    /**
     * 게시물 좋아요 토글 (좋아요/좋아요 취소)
     *
     * @param postId 게시물 ID
     * @param user 세션 사용자 정보
     * @return 좋아요 결과 정보
     */
    @Transactional
    public PostLikeResponseDto toggleLike(Long postId, SessionUser user) {
        Post post = postLikeValidator.validateAndGetPost(postId);
        Member member = postLikeValidator.validateAndGetMember(user.getMemberId());

        Optional<PostLike> existingLike = postLikeRepository.findByMemberAndPost(member, post);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            return unlikePost(post, member, existingLike.get());
        } else {
            // 좋아요 추가
            return likePost(post, member);
        }
    }

    /**
     * 특정 게시물의 좋아요 정보 조회
     *
     * @param postId 게시물 ID
     * @param user 세션 사용자 정보 (nullable)
     * @return 좋아요 정보
     */
    public PostLikeResponseDto getLikeInfo(Long postId, SessionUser user) {
        Post post = postLikeValidator.validateAndGetPost(postId);

        boolean isLiked = false;
        if (user != null) {
            Member member = postLikeValidator.validateAndGetMember(user.getMemberId());
            isLiked = postLikeRepository.existsByMemberAndPost(member, post);
        }

        return PostLikeResponseDto.builder()
                .postId(postId)
                .likeCount(post.getLikeCount())
                .isLiked(isLiked)
                .build();
    }

    /**
     * 여러 게시물에 대한 사용자의 좋아요 상태를 일괄 조회 (N+1 문제 해결)
     *
     * @param posts 게시물 목록
     * @param user 세션 사용자 정보
     * @return 게시물 ID와 좋아요 여부 매핑
     */
    public Map<Long, Boolean> getLikeStatusMap(List<Post> posts, SessionUser user) {
        if (user == null || posts.isEmpty()) {
            return posts.stream()
                    .collect(Collectors.toMap(Post::getPostId, post -> false));
        }

        Member member = postLikeValidator.validateAndGetMember(user.getMemberId());
        List<PostLike> userLikes = postLikeRepository.findByMemberAndPostIn(member, posts);

        Set<Long> likedPostIds = userLikes.stream()
                .map(like -> like.getPost().getPostId())
                .collect(Collectors.toSet());

        return posts.stream()
                .collect(Collectors.toMap(
                        Post::getPostId,
                        post -> likedPostIds.contains(post.getPostId())
                ));
    }

    /**
     * 사용자가 좋아요한 게시물 목록 조회
     *
     * @param user 세션 사용자 정보
     * @return 좋아요한 게시물 목록
     */
    public List<Post> getLikedPostsByUser(SessionUser user) {
        Member member = postLikeValidator.validateAndGetMember(user.getMemberId());
        return postLikeRepository.findLikedPostsByMember(member);
    }

    /**
     * 좋아요 추가 처리
     */
    @Transactional
    protected PostLikeResponseDto likePost(Post post, Member member) {
        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();

        postLikeRepository.save(postLike);
        postRepository.increaseLikeCount(post.getPostId());

        // Post 인스턴스도 업데이트 (중요!)
        post.increaseLikeCount();

        log.info("사용자 {}가 게시물 {}에 좋아요를 눌렀습니다.", member.getMemberId(), post.getPostId());

        return PostLikeResponseDto.builder()
                .postId(post.getPostId())
                .likeCount(post.getLikeCount())
                .isLiked(true)
                .build();
    }

    /**
     * 좋아요 취소 처리
     */
    @Transactional
    protected PostLikeResponseDto unlikePost(Post post, Member member, PostLike existingLike) {
        postLikeRepository.delete(existingLike);
        postRepository.decreaseLikeCount(post.getPostId());

        // Post 인스턴스도 업데이트 (중요!)
        post.decreaseLikeCount();

        log.info("사용자 {}가 게시물 {}의 좋아요를 취소했습니다.", member.getMemberId(), post.getPostId());

        return PostLikeResponseDto.builder()
                .postId(post.getPostId())
                .likeCount(post.getLikeCount())
                .isLiked(false)
                .build();
    }
}