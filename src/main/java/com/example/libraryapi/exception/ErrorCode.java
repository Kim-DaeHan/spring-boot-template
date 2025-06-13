package com.example.libraryapi.exception;

/**
 * 애플리케이션에서 사용하는 에러 코드를 정의하는 enum입니다.
 * 각 에러 코드는 고유한 코드와 기본 메시지를 가집니다.
 */
public enum ErrorCode {
    
    // 리소스 관련 에러
    RESOURCE_NOT_FOUND("E001", "리소스를 찾을 수 없습니다"),
    DUPLICATE_RESOURCE("E002", "중복된 리소스입니다"),
    RESOURCE_IN_USE("E003", "사용 중인 리소스입니다"),
    
    // 요청 관련 에러
    INVALID_REQUEST("E004", "잘못된 요청입니다"),
    VALIDATION_FAILED("E005", "입력값 검증에 실패했습니다"),
    INVALID_JSON_FORMAT("E006", "잘못된 JSON 형식입니다"),
    
    // 비즈니스 로직 에러
    BUSINESS_RULE_VIOLATION("E007", "비즈니스 규칙 위반입니다"),
    
    // 시스템 에러
    INTERNAL_SERVER_ERROR("E999", "서버 내부 오류가 발생했습니다");
    
    private final String code;
    private final String defaultMessage;
    
    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
} 