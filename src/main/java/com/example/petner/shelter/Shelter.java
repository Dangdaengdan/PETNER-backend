package com.example.petner.shelter;

import com.example.petner.location.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shelters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelter_id")
    private Long shelterId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "detail_address", nullable = false, length = 255)
    private String detailAddress;

    @Column(name = "shelter_contact", nullable = false, length = 50)
    private String shelterContact;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Builder
    public Shelter(String name, String detailAddress, String shelterContact, Location location) {
        this.name = name;
        this.detailAddress = detailAddress;
        this.shelterContact = shelterContact;
        this.location = location;
    }
}