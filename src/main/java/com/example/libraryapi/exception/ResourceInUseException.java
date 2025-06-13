package com.example.libraryapi.exception;

import org.springframework.http.HttpStatus;

/**
 * 이미 사용 중인 리소스에 대한 작업을 시도할 때 발생하는 예외입니다.
 * 예: 대여 중인 책의 상태 변경 시도
 */
public class ResourceInUseException extends BusinessException {

    public ResourceInUseException(String message) {
        super(message, HttpStatus.CONFLICT, ErrorCode.RESOURCE_IN_USE);
    }

    public ResourceInUseException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT, ErrorCode.RESOURCE_IN_USE);
    }
} 