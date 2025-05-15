package com.example.libraryapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseTest {

    @Test
    @DisplayName("ErrorResponse 생성자 및 빌더 테스트")
    void testErrorResponseCreation() {
        // given
        int status = 404;
        String message = "리소스를 찾을 수 없습니다";
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("id", "존재하지 않는 ID입니다");
        
        // when - 기본 생성자 테스트
        ErrorResponse emptyResponse = new ErrorResponse();
        
        // then
        assertThat(emptyResponse).isNotNull();
        
        // when - 전체 생성자 테스트
        ErrorResponse fullResponse = new ErrorResponse(status, message, timestamp, errors);
        
        // then
        assertThat(fullResponse.getStatus()).isEqualTo(status);
        assertThat(fullResponse.getMessage()).isEqualTo(message);
        assertThat(fullResponse.getTimestamp()).isEqualTo(timestamp);
        assertThat(fullResponse.getErrors()).isEqualTo(errors);
        
        // when - 빌더 테스트
        ErrorResponse builderResponse = ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(timestamp)
                .errors(errors)
                .build();
                
        // then
        assertThat(builderResponse.getStatus()).isEqualTo(status);
        assertThat(builderResponse.getMessage()).isEqualTo(message);
        assertThat(builderResponse.getTimestamp()).isEqualTo(timestamp);
        assertThat(builderResponse.getErrors()).isEqualTo(errors);
    }
    
    @Test
    @DisplayName("errors가 null인 ErrorResponse 생성 테스트")
    void testErrorResponseWithNullErrors() {
        // given
        int status = 400;
        String message = "잘못된 요청입니다";
        LocalDateTime timestamp = LocalDateTime.now();
        
        // when - errors가 null인 ErrorResponse 생성
        ErrorResponse response = new ErrorResponse(status, message, timestamp, null);
        
        // then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getErrors()).isNull();
        
        // when - 빌더로 errors를 null로 설정
        ErrorResponse builderResponse = ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(timestamp)
                .errors(null)
                .build();
                
        // then
        assertThat(builderResponse.getErrors()).isNull();
    }
    
    @Test
    @DisplayName("errors가 비어있는 ErrorResponse 생성 테스트")
    void testErrorResponseWithEmptyErrors() {
        // given
        int status = 403;
        String message = "접근 권한이 없습니다";
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> emptyErrors = new HashMap<>();
        
        // when
        ErrorResponse response = new ErrorResponse(status, message, timestamp, emptyErrors);
        
        // then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getErrors()).isEmpty();
    }
    
    @Test
    @DisplayName("다양한 HTTP 상태 코드로 ErrorResponse 생성 테스트")
    void testErrorResponseWithVariousStatusCodes() {
        // given
        String message = "오류 메시지";
        LocalDateTime timestamp = LocalDateTime.now();
        
        int badRequestStatus = 400;
        int unauthorizedStatus = 401;
        int forbiddenStatus = 403;
        int notFoundStatus = 404;
        int internalServerErrorStatus = 500;
        
        // when - 다양한 상태 코드로 ErrorResponse 객체 생성
        ErrorResponse badRequest = ErrorResponse.builder()
                .status(badRequestStatus)
                .message(message)
                .timestamp(timestamp)
                .build();
                
        ErrorResponse unauthorized = ErrorResponse.builder()
                .status(unauthorizedStatus)
                .message(message)
                .timestamp(timestamp)
                .build();
                
        ErrorResponse forbidden = ErrorResponse.builder()
                .status(forbiddenStatus)
                .message(message)
                .timestamp(timestamp)
                .build();
                
        ErrorResponse notFound = ErrorResponse.builder()
                .status(notFoundStatus)
                .message(message)
                .timestamp(timestamp)
                .build();
                
        ErrorResponse internalServerError = ErrorResponse.builder()
                .status(internalServerErrorStatus)
                .message(message)
                .timestamp(timestamp)
                .build();
                
        // then
        assertThat(badRequest.getStatus()).isEqualTo(badRequestStatus);
        assertThat(unauthorized.getStatus()).isEqualTo(unauthorizedStatus);
        assertThat(forbidden.getStatus()).isEqualTo(forbiddenStatus);
        assertThat(notFound.getStatus()).isEqualTo(notFoundStatus);
        assertThat(internalServerError.getStatus()).isEqualTo(internalServerErrorStatus);
    }
    
    @Test
    @DisplayName("ErrorResponse 게터/세터 테스트")
    void testErrorResponseGetterSetter() {
        // given
        ErrorResponse response = new ErrorResponse();
        int status = 400;
        String message = "잘못된 요청입니다";
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("title", "제목은 필수입니다");
        
        // when
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(timestamp);
        response.setErrors(errors);
        
        // then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getErrors()).isEqualTo(errors);
    }
    
    @Test
    @DisplayName("ErrorResponse 동등성 및 해시코드 테스트")
    void testErrorResponseEqualsAndHashCode() {
        // given
        int status = 500;
        String message = "서버 내부 오류";
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        
        ErrorResponse response1 = new ErrorResponse(status, message, timestamp, errors);
        ErrorResponse response2 = new ErrorResponse(status, message, timestamp, errors);
        ErrorResponse response3 = new ErrorResponse(404, "다른 오류", timestamp, errors);
        
        // then
        assertThat(response1).isEqualTo(response1); // 자기 자신과 비교
        assertThat(response1).isEqualTo(response2); // 동일 값 객체와 비교
        assertThat(response1).isNotEqualTo(response3); // 다른 값 객체와 비교
        assertThat(response1).isNotEqualTo(null); // null과 비교
        assertThat(response1).isNotEqualTo(new Object()); // 다른 타입과 비교
        
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        assertThat(response1.hashCode()).isNotEqualTo(response3.hashCode());
    }
    
    @Test
    @DisplayName("ErrorResponse toString 테스트")
    void testErrorResponseToString() {
        // given
        int status = 403;
        String message = "접근 권한이 없습니다";
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("permission", "권한 없음");
        
        ErrorResponse response = new ErrorResponse(status, message, timestamp, errors);
        
        // when
        String toString = response.toString();
        
        // then
        assertThat(toString).contains("status=" + status);
        assertThat(toString).contains("message=" + message);
        assertThat(toString).contains("timestamp=" + timestamp);
        assertThat(toString).contains("errors=" + errors);
    }
} 