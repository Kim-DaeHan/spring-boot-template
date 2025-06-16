package com.example.libraryapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * GlobalExceptionHandler의 동작을 테스트하는 클래스입니다.
 */
@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private MessageUtils messageUtils;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test/path");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void testResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("테스트 리소스를 찾을 수 없습니다");
        
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getErrorCode()).isEqualTo("E001");
        assertThat(response.getBody().getMessage()).isEqualTo("테스트 리소스를 찾을 수 없습니다");
        assertThat(response.getBody().getPath()).isEqualTo("/test/path");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testDuplicateResourceException() {
        // Given
        DuplicateResourceException exception = new DuplicateResourceException("중복된 테스트 리소스입니다");
        
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getErrorCode()).isEqualTo("E002");
        assertThat(response.getBody().getMessage()).isEqualTo("중복된 테스트 리소스입니다");
        assertThat(response.getBody().getPath()).isEqualTo("/test/path");
    }

    @Test
    void testInvalidRequestException() {
        // Given
        InvalidRequestException exception = new InvalidRequestException("잘못된 테스트 요청입니다");
        
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("E004");
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 테스트 요청입니다");
    }

    @Test
    void testValidationException() {
        // Given
        when(messageUtils.getMessageWithDefault(eq("error.E005"), anyString())).thenReturn("입력값 검증에 실패했습니다");
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.getExecutable()).thenReturn(this.getClass().getDeclaredMethods()[0]); // 임시 메서드 사용
        
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("testObject", "testField", "테스트 필드는 필수입니다");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);
        
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("E005");
        assertThat(response.getBody().getMessage()).isEqualTo("입력값 검증에 실패했습니다");
        assertThat(response.getBody().getErrors()).containsEntry("testField", "테스트 필드는 필수입니다");
    }

    @Test
    void testGenericException() {
        // Given
        when(messageUtils.getMessageWithDefault(eq("error.E999"), anyString())).thenReturn("서버 내부 오류가 발생했습니다");
        
        Exception exception = new RuntimeException("예상치 못한 오류");
        
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getErrorCode()).isEqualTo("E999");
        assertThat(response.getBody().getMessage()).isEqualTo("서버 내부 오류가 발생했습니다");
    }
} 