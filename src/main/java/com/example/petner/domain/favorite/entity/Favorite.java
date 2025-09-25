package com.example.petner.domain.favorite.entity;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 즐겨찾기 엔티티
 *
 * 사용자가 관심있는 강아지를 즐겨찾기에 추가/제거할 수 있는 기능을 제공합니다.
 * Member와 Dog 간의 다대다 관계를 중간 테이블로 표현합니다.
 */
@Entity
@Table(name = "favorites",
       uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "dog_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Favorite(Member member, Dog dog) {
        this.member = member;
        this.dog = dog;
    }
}