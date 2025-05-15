package com.example.libraryapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class BusinessExceptionTest {

    @Test
    @DisplayName("BusinessException 기본 동작 테스트")
    void testBusinessExceptionBaseClass() {
        // given
        String errorMessage = "비즈니스 예외가 발생했습니다.";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        // when - BusinessException의 구체적인 구현 클래스 생성
        ConcreteBusinessException exception = new ConcreteBusinessException(errorMessage, status);
        
        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getStatus()).isEqualTo(status);
        
        // with cause
        Throwable cause = new RuntimeException("원인 예외");
        ConcreteBusinessException exceptionWithCause = new ConcreteBusinessException(errorMessage, cause, status);
        
        assertThat(exceptionWithCause.getMessage()).isEqualTo(errorMessage);
        assertThat(exceptionWithCause.getCause()).isEqualTo(cause);
        assertThat(exceptionWithCause.getStatus()).isEqualTo(status);
    }
    
    // BusinessException 테스트를 위한 구체적인 구현 클래스
    private static class ConcreteBusinessException extends BusinessException {
        public ConcreteBusinessException(String message, HttpStatus status) {
            super(message, status);
        }
        
        public ConcreteBusinessException(String message, Throwable cause, HttpStatus status) {
            super(message, cause, status);
        }
    }

    @Test
    @DisplayName("ResourceNotFoundException 속성 검증")
    void testResourceNotFoundException() {
        // given
        String errorMessage = "도서를 찾을 수 없습니다. ID: 1";
        
        // when
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);
        
        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        
        // with cause
        Throwable cause = new RuntimeException("원인 예외");
        ResourceNotFoundException exceptionWithCause = new ResourceNotFoundException(errorMessage, cause);
        
        assertThat(exceptionWithCause.getMessage()).isEqualTo(errorMessage);
        assertThat(exceptionWithCause.getCause()).isEqualTo(cause);
        assertThat(exceptionWithCause.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    @DisplayName("ResourceInUseException 속성 검증")
    void testResourceInUseException() {
        // given
        String errorMessage = "대여 중인 책의 상태는 변경할 수 없습니다.";
        
        // when
        ResourceInUseException exception = new ResourceInUseException(errorMessage);
        
        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        
        // with cause
        Throwable cause = new RuntimeException("원인 예외");
        ResourceInUseException exceptionWithCause = new ResourceInUseException(errorMessage, cause);
        
        assertThat(exceptionWithCause.getMessage()).isEqualTo(errorMessage);
        assertThat(exceptionWithCause.getCause()).isEqualTo(cause);
        assertThat(exceptionWithCause.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
    
    @Test
    @DisplayName("InvalidRequestException 속성 검증")
    void testInvalidRequestException() {
        // given
        String errorMessage = "잘못된 요청 파라미터입니다.";
        
        // when
        InvalidRequestException exception = new InvalidRequestException(errorMessage);
        
        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // with cause
        Throwable cause = new RuntimeException("원인 예외");
        InvalidRequestException exceptionWithCause = new InvalidRequestException(errorMessage, cause);
        
        assertThat(exceptionWithCause.getMessage()).isEqualTo(errorMessage);
        assertThat(exceptionWithCause.getCause()).isEqualTo(cause);
        assertThat(exceptionWithCause.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    @DisplayName("DuplicateResourceException 속성 검증")
    void testDuplicateResourceException() {
        // given
        String errorMessage = "카테고리 이름이 이미 존재합니다: 소설";
        
        // when
        DuplicateResourceException exception = new DuplicateResourceException(errorMessage);
        
        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        
        // with cause
        Throwable cause = new RuntimeException("원인 예외");
        DuplicateResourceException exceptionWithCause = new DuplicateResourceException(errorMessage, cause);
        
        assertThat(exceptionWithCause.getMessage()).isEqualTo(errorMessage);
        assertThat(exceptionWithCause.getCause()).isEqualTo(cause);
        assertThat(exceptionWithCause.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
} 