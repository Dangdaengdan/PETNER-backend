package com.example.petner.global.exception.customException;

import com.example.petner.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 유기견 분양 신청 관련 예외
 */
@Getter
@RequiredArgsConstructor
public class DogApplyException extends RuntimeException {
    private final ErrorCode errorCode;
}