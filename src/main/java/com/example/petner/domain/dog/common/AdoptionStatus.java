package com.example.petner.domain.dog.common;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AdoptionStatus {
    입양_가능("입양 가능"),
    입양_절차_중("입양 절차 중"),
    입양_완료("입양 완료");

    private final String displayName;

    AdoptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static AdoptionStatus fromValue(String value) {
        if (value == null) {
            return null;
        }

        for (AdoptionStatus status : AdoptionStatus.values()) {
            if (status.name().equalsIgnoreCase(value) ||
                status.displayName.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AdoptionStatus value: " + value);
    }
}