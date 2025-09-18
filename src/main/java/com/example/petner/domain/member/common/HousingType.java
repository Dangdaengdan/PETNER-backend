package com.example.petner.domain.member.common;

public enum HousingType {
    아파트("아파트"),
    단독_주택("단독 주택"),
    빌라("빌라"),
    기타("기타");

    private final String displayName;

    HousingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}