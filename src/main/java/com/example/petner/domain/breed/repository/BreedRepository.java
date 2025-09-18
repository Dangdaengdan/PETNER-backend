package com.example.petner.domain.breed.repository;

import com.example.petner.domain.breed.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {

    Optional<Breed> findByName(String name);
}