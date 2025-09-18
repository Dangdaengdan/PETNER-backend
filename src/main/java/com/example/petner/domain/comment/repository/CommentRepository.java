package com.example.petner.domain.comment.repository;

import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findByMember(Member member);

    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findParentCommentsByPost(@Param("post") Post post);

    @Query("SELECT c FROM Comment c WHERE c.parentComment = :parentComment ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentComment(@Param("parentComment") Comment parentComment);

    @Query("SELECT c FROM Comment c WHERE c.post = :post ORDER BY c.createdAt ASC")
    List<Comment> findByPostOrderByCreatedAtAsc(@Param("post") Post post);
}