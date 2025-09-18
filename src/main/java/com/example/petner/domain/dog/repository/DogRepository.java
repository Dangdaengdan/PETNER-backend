package com.example.petner.domain.dog.repository;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.shelter.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {

    List<Dog> findByMember(Member member);

    List<Dog> findByShelter(Shelter shelter);

    List<Dog> findByAdoptionStatus(AdoptionStatus adoptionStatus);

    @Query("SELECT d FROM Dog d WHERE d.adoptionStatus = :status ORDER BY d.createdAt DESC")
    List<Dog> findByAdoptionStatusOrderByCreatedAtDesc(@Param("status") AdoptionStatus status);

    @Query("SELECT d FROM Dog d WHERE d.member.location.locationId = :locationId AND d.adoptionStatus = :status")
    List<Dog> findByLocationAndAdoptionStatus(@Param("locationId") Long locationId, @Param("status") AdoptionStatus status);
}