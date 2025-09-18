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

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByMember(Member member);

    List<Favorite> findByDog(Dog dog);

    Optional<Favorite> findByMemberAndDog(Member member, Dog dog);

    boolean existsByMemberAndDog(Member member, Dog dog);

    @Query("SELECT f FROM Favorite f WHERE f.member = :member ORDER BY f.createdAt DESC")
    List<Favorite> findByMemberOrderByCreatedAtDesc(@Param("member") Member member);

    void deleteByMemberAndDog(Member member, Dog dog);
}