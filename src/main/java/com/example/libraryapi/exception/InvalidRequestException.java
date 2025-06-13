package com.example.libraryapi.exception;

import org.springframework.http.HttpStatus;

/**
 * 잘못된 요청에 대한 예외 클래스입니다.
 * 사용자 입력 오류나 잘못된 데이터 형식에 사용됩니다.
 */
public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST);
    }
} 