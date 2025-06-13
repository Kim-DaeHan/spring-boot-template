package com.example.libraryapi.exception;

import org.springframework.http.HttpStatus;

/**
 * 이미 존재하는 리소스를 생성하려 할 때 발생하는 예외입니다.
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT, ErrorCode.DUPLICATE_RESOURCE);
    }
} 