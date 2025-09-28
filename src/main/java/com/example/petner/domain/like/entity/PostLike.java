package com.example.petner.domain.like.entity;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 게시물 좋아요 엔티티
 * Member와 Post 간의 다대다 관계를 표현하는 중간 엔티티
 */
@Entity
@Table(name = "post_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "post_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public PostLike(Member member, Post post) {
        this.member = member;
        this.post = post;
    }
}