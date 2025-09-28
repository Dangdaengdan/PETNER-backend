package com.example.petner.domain.like.repository;

import com.example.petner.domain.like.entity.PostLike;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 특정 사용자가 특정 게시물에 좋아요를 눌렀는지 확인
     */
    boolean existsByMemberAndPost(Member member, Post post);

    /**
     * 특정 사용자의 특정 게시물 좋아요 조회
     */
    Optional<PostLike> findByMemberAndPost(Member member, Post post);

    /**
     * 특정 사용자가 좋아요한 게시물 목록 조회 (FETCH JOIN으로 N+1 해결)
     */
    @Query("SELECT pl.post FROM PostLike pl " +
           "JOIN FETCH pl.post.author " +
           "WHERE pl.member = :member " +
           "ORDER BY pl.createdAt DESC")
    List<Post> findLikedPostsByMember(@Param("member") Member member);

    /**
     * 특정 게시물들에 대한 사용자의 좋아요 여부를 일괄 조회 (N+1 문제 해결)
     */
    @Query("SELECT pl FROM PostLike pl " +
           "JOIN FETCH pl.post " +
           "JOIN FETCH pl.member " +
           "WHERE pl.member = :member AND pl.post IN :posts")
    List<PostLike> findByMemberAndPostIn(@Param("member") Member member, @Param("posts") List<Post> posts);

    /**
     * 특정 사용자의 좋아요 삭제 (좋아요 취소)
     */
    void deleteByMemberAndPost(Member member, Post post);
}