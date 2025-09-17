package com.example.petner.common;

public enum DogSize {
    소형("소형"),
    중형("중형"),
    대형("대형");

    private final String displayName;

    DogSize(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}