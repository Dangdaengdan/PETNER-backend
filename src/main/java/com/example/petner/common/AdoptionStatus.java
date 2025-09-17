package com.example.petner.common;

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
}