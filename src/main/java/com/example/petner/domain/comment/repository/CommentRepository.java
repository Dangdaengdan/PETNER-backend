package com.example.petner.domain.comment.repository;

import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findByMember(Member member);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post = :post AND c.parentComment IS NOT NULL")
    void deleteRepliesByPost(@Param("post") Post post);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post = :post AND c.parentComment IS NULL")
    void deleteParentCommentsByPost(@Param("post") Post post);

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.post = :post AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findParentCommentsByPost(@Param("post") Post post);

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.parentComment = :parentComment " +
           "AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentComment(@Param("parentComment") Comment parentComment);

    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member " +
           "LEFT JOIN FETCH c.parentComment " +
           "WHERE c.post = :post " +
           "AND (c.deletedAt IS NULL OR (c.deletedAt IS NOT NULL AND EXISTS " +
           "(SELECT 1 FROM Comment r WHERE r.parentComment = c AND r.deletedAt IS NULL))) " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findByPostOrderByCreatedAtAsc(@Param("post") Post post);

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.commentId = :commentId")
    Optional<Comment> findByIdWithMember(@Param("commentId") Long commentId);


    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.parentComment = :parentComment AND c.deletedAt IS NULL")
    boolean existsActiveReplies(@Param("parentComment") Comment parentComment);

    /**
     * 특정 게시물의 부모 댓글만 페이징하여 조회 (계층 구조 유지, N+1 문제 해결)
     * 삭제된 댓글도 포함하되, 답글이 있는 경우만 포함
     */
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member " +
           "WHERE c.post = :post " +
           "AND c.parentComment IS NULL " +
           "AND (c.deletedAt IS NULL OR (c.deletedAt IS NOT NULL AND EXISTS " +
           "(SELECT 1 FROM Comment r WHERE r.parentComment = c AND r.deletedAt IS NULL))) " +
           "ORDER BY c.createdAt ASC")
    Page<Comment> findByPostWithPaging(@Param("post") Post post, Pageable pageable);

    /**
     * 특정 부모 댓글들의 답글만 조회 (N+1 문제 해결)
     */
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member " +
           "JOIN FETCH c.parentComment " +
           "WHERE c.parentComment.commentId IN :parentCommentIds " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentCommentIds(@Param("parentCommentIds") List<Long> parentCommentIds);
}