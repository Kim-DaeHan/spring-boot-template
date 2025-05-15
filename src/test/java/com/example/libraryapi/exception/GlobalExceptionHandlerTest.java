package com.example.libraryapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("ResourceNotFoundException 처리 테스트")
    void testHandleResourceNotFoundException() {
        // given
        ResourceNotFoundException exception = new ResourceNotFoundException("리소스를 찾을 수 없습니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleBusinessException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getMessage()).isEqualTo("리소스를 찾을 수 없습니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("ResourceInUseException 처리 테스트")
    void testHandleResourceInUseException() {
        // given
        ResourceInUseException exception = new ResourceInUseException("리소스가 이미 사용 중입니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleBusinessException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorResponse.getMessage()).isEqualTo("리소스가 이미 사용 중입니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("InvalidRequestException 처리 테스트")
    void testHandleInvalidRequestException() {
        // given
        InvalidRequestException exception = new InvalidRequestException("잘못된 요청입니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleBusinessException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("잘못된 요청입니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("DuplicateResourceException 처리 테스트")
    void testHandleDuplicateResourceException() {
        // given
        DuplicateResourceException exception = new DuplicateResourceException("중복된 리소스가 존재합니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleBusinessException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorResponse.getMessage()).isEqualTo("중복된 리소스가 존재합니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 테스트")
    void testHandleMethodArgumentNotValidException() {
        // MethodArgumentNotValidException 모킹
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("testObject", "testField", "필드 검증 오류");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleValidationExceptions(ex);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("입력값 검증에 실패했습니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
        
        Map<String, String> errors = errorResponse.getErrors();
        assertThat(errors).isNotNull();
        assertThat(errors).containsEntry("testField", "필드 검증 오류");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 처리 테스트")
    void testHandleHttpMessageNotReadableException() {
        // HttpMessageNotReadableException 모킹
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Required request body is missing");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleHttpMessageNotReadable(ex);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("잘못된 요청 형식입니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Enum 값 오류 HttpMessageNotReadableException 처리 테스트")
    void testHandleEnumHttpMessageNotReadableException() {
        // Enum 관련 HttpMessageNotReadableException 모킹
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        String errorMsg = "Cannot deserialize value of type `com.example.libraryapi.book.entity.BookStatus` from String \"INVALID\": not one of the values accepted for Enum class: [AVAILABLE, UNAVAILABLE]";
        when(ex.getMessage()).thenReturn(errorMsg);
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleHttpMessageNotReadable(ex);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("유효하지 않은 BookStatus 값입니다: 'INVALID'. 허용된 값: AVAILABLE, UNAVAILABLE");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("JSON 구문 오류 HttpMessageNotReadableException 처리 테스트")
    void testHandleJsonSyntaxErrorException() {
        // JSON 구문 오류 HttpMessageNotReadableException 모킹
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        String errorMsg = "JSON parse error: Unexpected character ('}' (code 125)): was expecting double-quote to start field name";
        when(ex.getMessage()).thenReturn(errorMsg);
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleHttpMessageNotReadable(ex);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("잘못된 요청 형식입니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("NULL 값 역직렬화 오류 HttpMessageNotReadableException 처리 테스트")
    void testHandleNullValueDeserializationException() {
        // NULL 값 역직렬화 오류 HttpMessageNotReadableException 모킹
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        String errorMsg = "Cannot deserialize value of type `java.time.LocalDate` from String \"null\": expected format yyyy-MM-dd";
        when(ex.getMessage()).thenReturn(errorMsg);
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleHttpMessageNotReadable(ex);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("잘못된 요청 형식입니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("메시지가 null인 HttpMessageNotReadableException 처리 테스트")
    void testHandleNullMessageHttpMessageNotReadableException() {
        // 메시지가 null인 HttpMessageNotReadableException 모킹
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn(null);
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleHttpMessageNotReadable(ex);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("잘못된 요청 형식입니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 테스트")
    void testHandleIllegalArgumentException() {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("잘못된 인자가 전달되었습니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleRuntimeException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("잘못된 인자가 전달되었습니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("IllegalStateException 처리 테스트")
    void testHandleIllegalStateException() {
        // given
        IllegalStateException exception = new IllegalStateException("잘못된 상태입니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleRuntimeException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorResponse.getMessage()).isEqualTo("잘못된 상태입니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("기타 RuntimeException 처리 테스트")
    void testHandleRuntimeException() {
        // given
        RuntimeException exception = new RuntimeException("런타임 예외가 발생했습니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleRuntimeException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getMessage()).isEqualTo("런타임 예외가 발생했습니다.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("일반 Exception 처리 테스트")
    void testHandleGenericException() {
        // given
        Exception exception = new Exception("일반 예외가 발생했습니다.");
        
        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleGenericException(exception);
        
        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getMessage()).isEqualTo("서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }
} 