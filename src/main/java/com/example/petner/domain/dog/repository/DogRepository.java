package com.example.petner.domain.dog.repository;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.shelter.entity.Shelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {

    List<Dog> findByMemberAndDeletedFalse(Member member);

    List<Dog> findByShelterAndDeletedFalse(Shelter shelter);

    List<Dog> findByAdoptionStatusAndDeletedFalse(AdoptionStatus adoptionStatus);

    @Query("SELECT d FROM Dog d WHERE d.adoptionStatus = :status AND d.deleted = false ORDER BY d.createdAt DESC")
    List<Dog> findByAdoptionStatusOrderByCreatedAtDesc(@Param("status") AdoptionStatus status);

    @Query("SELECT d FROM Dog d WHERE d.member.location.locationId = :locationId AND d.adoptionStatus = :status AND d.deleted = false")
    List<Dog> findByLocationAndAdoptionStatus(@Param("locationId") Long locationId, @Param("status") AdoptionStatus status);

    /**
     * N+1 문제 해결을 위한 페치 조인 - 전체 목록 조회
     * Dog와 연관된 Breed, Member, Shelter를 한 번의 쿼리로 조회
     */
    @Query("SELECT d FROM Dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter " +
           "WHERE d.deleted = false " +
           "ORDER BY d.createdAt DESC")
    List<Dog> findAllWithAssociations();

    /**
     * N+1 문제 해결을 위한 페치 조인 - 개별 조회
     * Dog와 연관된 Breed, Member, Shelter를 한 번의 쿼리로 조회
     */
    @Query("SELECT d FROM Dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter " +
           "WHERE d.dogId = :dogId AND d.deleted = false")
    Optional<Dog> findByIdWithAssociations(@Param("dogId") Long dogId);

    /**
     * 검색 동기화를 위한 페치 조인 - 모든 연관관계 포함
     * Dog와 연관된 모든 엔티티(Breed, Member, Shelter, Location)를 한 번의 쿼리로 조회
     */
    @Query("SELECT d FROM Dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter s " +
           "LEFT JOIN FETCH s.location " +
           "WHERE d.dogId = :dogId AND d.deleted = false")
    Optional<Dog> findByIdWithAllAssociations(@Param("dogId") Long dogId);

    /**
     * N+1 문제 해결을 위한 페치 조인 - 페이징 지원 목록 조회
     * Dog와 연관된 Breed, Member, Shelter를 한 번의 쿼리로 조회하며 페이징을 지원
     * ChatMessageService 패턴을 따라 List로 반환
     */
    @Query(value = "SELECT d FROM Dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter " +
           "WHERE d.deleted = false")
    List<Dog> findAllWithAssociationsPaging(Pageable pageable);

    /**
     * N+1 문제 해결을 위한 페치 조인 - 특정 사용자가 등록한 유기견 목록 조회 (페이징)
     * Dog와 연관된 Breed, Member, Shelter를 한 번의 쿼리로 조회하며 페이징을 지원
     * 특정 멤버 ID로 필터링하여 해당 사용자가 등록한 유기견만 조회
     */
    @Query(value = "SELECT d FROM Dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter " +
           "WHERE d.member.memberId = :memberId AND d.deleted = false")
    List<Dog> findByMemberIdWithAssociationsPaging(@Param("memberId") Long memberId, Pageable pageable);

    /**
     * 삭제된 유기견도 포함하여 ID로 조회 (관리자 또는 삭제 작업용)
     * @param dogId 유기견 ID
     * @return 삭제 여부와 관계없이 조회된 유기견
     */
    @Query("SELECT d FROM Dog d " +
           "JOIN FETCH d.breed " +
           "JOIN FETCH d.member " +
           "LEFT JOIN FETCH d.shelter " +
           "WHERE d.dogId = :dogId")
    Optional<Dog> findByIdWithAssociationsIncludeDeleted(@Param("dogId") Long dogId);
}