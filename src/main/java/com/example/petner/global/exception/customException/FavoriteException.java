package com.example.petner.global.exception.customException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.example.petner.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public class FavoriteException extends RuntimeException {
    private final ErrorCode errorCode;
}