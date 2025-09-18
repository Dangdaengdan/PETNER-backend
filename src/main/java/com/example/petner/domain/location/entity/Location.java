package com.example.petner.domain.location.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "district", nullable = false, length = 50)
    private String district;

    @Builder
    public Location(String state, String district) {
        this.state = state;
        this.district = district;
    }
}