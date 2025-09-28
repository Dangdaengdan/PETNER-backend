package com.example.petner.global.exception;

import com.example.petner.global.exception.customException.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.example.petner.global.exception.dto.ErrorPayload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MemberException.class,
            DogException.class,
            DogApplyException.class,
            PostException.class,
            ChatException.class,
            CommentException.class,
            ShelterException.class,
            LocationException.class,
            FavoriteException.class,
            UploadException.class
    })
    public ResponseEntity<ErrorPayload> handleCustomException(RuntimeException ex, HttpServletRequest request) {
        ErrorCode errorCode = extractErrorCode(ex);
        
        log.warn("{} - Code: {}, Message: {}, URI: {}", 
                ex.getClass().getSimpleName(), errorCode.getErrorCode(), errorCode.getMessage(), request.getRequestURI());
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorPayload(errorCode.getErrorCode(), errorCode.getMessage()));
    }
    
    private ErrorCode extractErrorCode(RuntimeException ex) {
        try {
            Method getErrorCodeMethod = ex.getClass().getMethod("getErrorCode");
            return (ErrorCode) getErrorCodeMethod.invoke(ex);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to extract ErrorCode from exception: {}", ex.getClass().getSimpleName());
            return ErrorCode.GLOBAL_ERROR;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorPayload> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed - URI: {}, Errors: {}", request.getRequestURI(), errors);
        
        return ResponseEntity
                .badRequest()
                .body(new ErrorPayload("400-VALIDATION", "입력값 검증에 실패했습니다"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorPayload> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Invalid JSON format - URI: {}", request.getRequestURI());
        
        return ResponseEntity
                .badRequest()
                .body(new ErrorPayload("400-JSON", "잘못된 JSON 형식입니다"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorPayload> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Type mismatch - Parameter: {}, URI: {}", ex.getName(), request.getRequestURI());
        
        return ResponseEntity
                .badRequest()
                .body(new ErrorPayload("400-TYPE", "잘못된 매개변수 타입입니다"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorPayload> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing parameter - Parameter: {}, URI: {}", ex.getParameterName(), request.getRequestURI());
        
        return ResponseEntity
                .badRequest()
                .body(new ErrorPayload("400-PARAM", "필수 매개변수가 누락되었습니다"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorPayload> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not supported - Method: {}, URI: {}", ex.getMethod(), request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorPayload("405-METHOD", "지원하지 않는 HTTP 메서드입니다"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorPayload> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found - URI: {}", request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorPayload("404-RESOURCE", "요청한 리소스를 찾을 수 없습니다"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorPayload> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed - URI: {}", request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorPayload("401-AUTH", "인증에 실패했습니다"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorPayload> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied - URI: {}", request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorPayload("403-ACCESS", "접근 권한이 없습니다"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorPayload> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Database constraint violation - URI: {}", request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorPayload("409-DATA", "데이터 무결성 제약 조건 위반"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred - URI: {}, Exception: {}", 
                request.getRequestURI(), ex.getClass().getSimpleName(), ex);
        
        return ResponseEntity
                .status(ErrorCode.GLOBAL_ERROR.getHttpStatus())
                .body(new ErrorPayload(ErrorCode.GLOBAL_ERROR.getErrorCode(), ErrorCode.GLOBAL_ERROR.getMessage()));
    }
}