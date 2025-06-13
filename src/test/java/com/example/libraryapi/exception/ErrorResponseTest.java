package com.example.libraryapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseTest {

    @Test
    @DisplayName("ErrorResponse 빌더 패턴 테스트")
    void testErrorResponseBuilder() {
        // given
        int status = 404;
        String errorCode = "E001";
        String message = "리소스를 찾을 수 없습니다";
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/books/999";
        Map<String, String> errors = new HashMap<>();
        errors.put("id", "존재하지 않는 ID입니다");
        
        // when
        ErrorResponse response = ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .errors(errors)
                .build();
        
        // then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getErrors()).isEqualTo(errors);
    }

    @Test
    @DisplayName("ErrorResponse 기본 생성자 테스트")
    void testErrorResponseDefaultConstructor() {
        // when
        ErrorResponse response = new ErrorResponse();
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(0);
        assertThat(response.getErrorCode()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getTimestamp()).isNull();
        assertThat(response.getPath()).isNull();
        assertThat(response.getErrors()).isNull();
    }

    @Test
    @DisplayName("ErrorResponse errors가 null인 경우 테스트")
    void testErrorResponseWithNullErrors() {
        // given
        int status = 400;
        String errorCode = "E005";
        String message = "잘못된 요청입니다";
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/books";
        
        // when
        ErrorResponse response = ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .errors(null)
                .build();
        
        // then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getErrors()).isNull();
    }

    @Test
    @DisplayName("ErrorResponse errors가 비어있는 경우 테스트")
    void testErrorResponseWithEmptyErrors() {
        // given
        int status = 403;
        String errorCode = "E007";
        String message = "접근 권한이 없습니다";
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/admin";
        Map<String, String> emptyErrors = new HashMap<>();
        
        // when
        ErrorResponse response = ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .errors(emptyErrors)
                .build();
        
        // then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("다양한 에러 코드로 ErrorResponse 생성 테스트")
    void testErrorResponseWithVariousErrorCodes() {
        // given
        String message = "오류 메시지";
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/test";
        
        // when & then
        ErrorResponse notFoundResponse = ErrorResponse.builder()
                .status(404)
                .errorCode("E001")
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .build();
        assertThat(notFoundResponse.getErrorCode()).isEqualTo("E001");
        
        ErrorResponse duplicateResponse = ErrorResponse.builder()
                .status(409)
                .errorCode("E002")
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .build();
        assertThat(duplicateResponse.getErrorCode()).isEqualTo("E002");
        
        ErrorResponse validationResponse = ErrorResponse.builder()
                .status(400)
                .errorCode("E005")
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .build();
        assertThat(validationResponse.getErrorCode()).isEqualTo("E005");
    }

    @Test
    @DisplayName("ErrorResponse 게터/세터 테스트")
    void testErrorResponseGetterSetter() {
        // given
        ErrorResponse response = new ErrorResponse();
        int status = 500;
        String errorCode = "E999";
        String message = "서버 내부 오류";
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/error";
        Map<String, String> errors = new HashMap<>();
        errors.put("server", "내부 오류 발생");
        
        // when
        response.setStatus(status);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setTimestamp(timestamp);
        response.setPath(path);
        response.setErrors(errors);
        
        // then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getErrors()).isEqualTo(errors);
    }

    @Test
    @DisplayName("ErrorResponse 동등성 테스트")
    void testErrorResponseEquals() {
        // given
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error");
        
        ErrorResponse response1 = ErrorResponse.builder()
                .status(404)
                .errorCode("E001")
                .message("Not found")
                .timestamp(timestamp)
                .path("/api/test")
                .errors(errors)
                .build();
                
        ErrorResponse response2 = ErrorResponse.builder()
                .status(404)
                .errorCode("E001")
                .message("Not found")
                .timestamp(timestamp)
                .path("/api/test")
                .errors(errors)
                .build();
                
        ErrorResponse response3 = ErrorResponse.builder()
                .status(400)
                .errorCode("E005")
                .message("Bad request")
                .timestamp(timestamp)
                .path("/api/test")
                .errors(errors)
                .build();
        
        // then
        assertThat(response1).isEqualTo(response1); // 자기 자신과 비교
        assertThat(response1).isEqualTo(response2); // 동일 값 객체와 비교
        assertThat(response1).isNotEqualTo(response3); // 다른 값 객체와 비교
        assertThat(response1).isNotEqualTo(null); // null과 비교
        assertThat(response1).isNotEqualTo(new Object()); // 다른 타입과 비교
    }

    @Test
    @DisplayName("ErrorResponse toString 테스트")
    void testErrorResponseToString() {
        // given
        ErrorResponse response = ErrorResponse.builder()
                .status(404)
                .errorCode("E001")
                .message("리소스를 찾을 수 없습니다")
                .timestamp(LocalDateTime.now())
                .path("/api/books/999")
                .build();
        
        // when
        String toString = response.toString();
        
        // then
        assertThat(toString).isNotNull();
        assertThat(toString).contains("404");
        assertThat(toString).contains("E001");
        assertThat(toString).contains("리소스를 찾을 수 없습니다");
        assertThat(toString).contains("/api/books/999");
    }
} 