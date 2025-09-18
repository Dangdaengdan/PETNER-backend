package com.example.petner.domain.shelter.repository;

import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.shelter.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    Optional<Shelter> findByName(String name);

    List<Shelter> findByLocation(Location location);
}