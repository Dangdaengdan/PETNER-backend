package com.example.petner.post;

import com.example.petner.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Long postId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "viewCount", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "thumbImageUrl", length = 500)
    private String thumbImageUrl;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId", nullable = false)
    private Member author;

    @Builder
    public Post(String title, String content, String thumbImageUrl, Member author) {
        this.title = title;
        this.content = content;
        this.thumbImageUrl = thumbImageUrl;
        this.author = author;
        this.viewCount = 0;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}