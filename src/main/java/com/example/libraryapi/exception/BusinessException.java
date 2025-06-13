package com.example.libraryapi.exception;

import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 예외의 기본 클래스입니다.
 * 모든 커스텀 비즈니스 예외는 이 클래스를 상속받아야 합니다.
 */
public abstract class BusinessException extends RuntimeException {
    
    private final HttpStatus status;
    private final ErrorCode errorCode;
    
    protected BusinessException(String message, HttpStatus status, ErrorCode errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    
    protected BusinessException(String message, Throwable cause, HttpStatus status, ErrorCode errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
    
    // 기존 생성자 호환성을 위해 유지 (deprecated)
    @Deprecated
    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = ErrorCode.BUSINESS_RULE_VIOLATION; // 기본값
    }
    
    @Deprecated
    protected BusinessException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
        this.errorCode = ErrorCode.BUSINESS_RULE_VIOLATION; // 기본값
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
} 