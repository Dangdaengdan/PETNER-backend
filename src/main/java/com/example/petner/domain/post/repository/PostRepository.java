package com.example.petner.domain.post.repository;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthor(Member author);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    Page<Post> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.viewCount DESC")
    Page<Post> findAllOrderByViewCountDesc(Pageable pageable);

    /**
     * 전체 게시물 목록을 페이징하여 조회합니다.
     * N+1 문제 해결을 위해 Fetch Join을 사용합니다.
     * 페이징과 Fetch Join을 함께 사용할 때는 정확한 전체 개수 계산을 위해 countQuery를 분리하는 것이 좋습니다.
     * ORDER BY 절을 제거하여 Pageable의 Sort가 적용되도록 합니다.
     */
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.author",
            countQuery = "SELECT count(p) FROM Post p")
    Page<Post> findAllWithAuthor(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.postId = :postId")
    java.util.Optional<Post> findByIdWithAuthor(@Param("postId") Long postId);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.author WHERE p.author = :author",
            countQuery = "SELECT count(p) FROM Post p WHERE p.author = :author")
    Page<Post> findByAuthorWithPaging(@Param("author") Member author, Pageable pageable);

    /**
     * 게시물의 좋아요 개수 증가
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.postId = :postId")
    void increaseLikeCount(@Param("postId") Long postId);

    /**
     * 게시물의 좋아요 개수 감소
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.postId = :postId AND p.likeCount > 0")
    void decreaseLikeCount(@Param("postId") Long postId);
}