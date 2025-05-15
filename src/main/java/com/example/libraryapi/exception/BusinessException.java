package com.example.libraryapi.exception;

import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 예외의 기본 클래스입니다.
 * 모든 커스텀 비즈니스 예외는 이 클래스를 상속받아야 합니다.
 */
public abstract class BusinessException extends RuntimeException {
    
    private final HttpStatus status;
    
    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    protected BusinessException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
} 