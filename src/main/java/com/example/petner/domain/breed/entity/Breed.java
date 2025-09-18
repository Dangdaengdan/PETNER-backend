package com.example.petner.domain.breed.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "breeds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breed_id")
    private Long breedId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Builder
    public Breed(String name) {
        this.name = name;
    }
}