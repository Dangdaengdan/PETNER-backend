package com.example.petner.domain.favorite.repository;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 즐겨찾기 Repository
 *
 * N+1 문제 방지를 위한 최적화된 쿼리 제공
 * Member와 Dog 간의 즐겨찾기 관계 관리
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 특정 멤버의 모든 즐겨찾기 조회
     * @param member 멤버 엔티티
     * @return 해당 멤버의 즐겨찾기 목록
     */
    List<Favorite> findByMember(Member member);

    /**
     * 특정 강아지를 즐겨찾기한 모든 멤버 조회
     * @param dog 강아지 엔티티
     * @return 해당 강아지를 즐겨찾기한 목록
     */
    List<Favorite> findByDog(Dog dog);

    /**
     * 특정 멤버와 강아지의 즐겨찾기 관계 조회
     * @param member 멤버 엔티티
     * @param dog 강아지 엔티티
     * @return 즐겨찾기 관계
     */
    Optional<Favorite> findByMemberAndDog(Member member, Dog dog);

    /**
     * 특정 멤버와 강아지의 즐겨찾기 관계 존재 여부 확인
     * @param member 멤버 엔티티
     * @param dog 강아지 엔티티
     * @return 즐겨찾기 관계 존재 여부
     */
    boolean existsByMemberAndDog(Member member, Dog dog);

    /**
     * 특정 멤버의 즐겨찾기를 생성일 기준 내림차순으로 조회 (N+1 방지)
     * @param memberId 멤버 ID
     * @return 즐겨찾기 목록 (강아지 정보 포함)
     */
    @Query("SELECT f FROM Favorite f " +
           "LEFT JOIN FETCH f.dog d " +
           "LEFT JOIN FETCH d.breed " +
           "LEFT JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter " +
           "WHERE f.member.memberId = :memberId " +
           "ORDER BY f.createdAt DESC")
    List<Favorite> findByMemberIdWithDogDetails(@Param("memberId") Long memberId);

    /**
     * 특정 멤버와 강아지 ID로 즐겨찾기 관계 존재 여부 확인
     * @param memberId 멤버 ID
     * @param dogId 강아지 ID
     * @return 즐겨찾기 관계 존재 여부
     */
    boolean existsByMemberMemberIdAndDogDogId(Long memberId, Long dogId);

    /**
     * 특정 멤버와 강아지의 즐겨찾기 관계 삭제
     * @param member 멤버 엔티티
     * @param dog 강아지 엔티티
     */
    void deleteByMemberAndDog(Member member, Dog dog);

    /**
     * 특정 멤버 ID와 강아지 ID로 즐겨찾기 관계 삭제
     * @param memberId 멤버 ID
     * @param dogId 강아지 ID
     */
    void deleteByMemberMemberIdAndDogDogId(Long memberId, Long dogId);
}