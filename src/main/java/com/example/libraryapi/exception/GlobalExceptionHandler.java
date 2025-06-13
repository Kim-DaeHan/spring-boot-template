package com.example.libraryapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtils messageUtils;

    @Autowired
    public GlobalExceptionHandler(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    /**
     * BusinessException 및 하위 클래스의 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        log.error("Business exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ex.getStatus().value())
                .errorCode(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    /**
     * 유효성 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
                .message(messageUtils.getMessageWithDefault("error.E005", "Validation failed"))
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .errors(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 요청 바디 파싱 오류 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("Message not readable: {}", ex.getMessage());
        
        String message = messageUtils.getMessageWithDefault("error.E006", "Invalid JSON format");
        
        // Enum 값 오류 처리
        String errorMessage = ex.getMessage();
        if (errorMessage != null && errorMessage.contains("Enum class")) {
            Pattern pattern = Pattern.compile("Cannot deserialize value of type `(.+?)` from String \"(.+?)\": not one of the values accepted for Enum class: \\[(.+?)\\]");
            Matcher matcher = pattern.matcher(errorMessage);
            
            if (matcher.find()) {
                String enumType = matcher.group(1);
                String invalidValue = matcher.group(2);
                String acceptedValues = matcher.group(3);
                
                // 클래스 이름에서 짧은 이름만 추출 (패키지명 제거)
                String shortClassName = enumType.substring(enumType.lastIndexOf('.') + 1);
                
                message = String.format("유효하지 않은 %s 값입니다: '%s'. 허용된 값: %s", 
                                      shortClassName, invalidValue, acceptedValues);
            }
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.INVALID_JSON_FORMAT.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 일반적인 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();
        
        if (ex instanceof IllegalArgumentException) {
            // 잘못된 인자 예외 (점차 InvalidRequestException으로 대체될 예정)
            status = HttpStatus.BAD_REQUEST;
            errorCode = ErrorCode.INVALID_REQUEST;
            log.error("Invalid argument: {}", message);
        } else if (ex instanceof IllegalStateException) {
            // 상태 충돌 예외 (점차 ResourceInUseException으로 대체될 예정)
            status = HttpStatus.CONFLICT;
            errorCode = ErrorCode.RESOURCE_IN_USE;
            log.error("State conflict: {}", message);
        } else {
            // 기타 런타임 예외
            log.error("Runtime exception", ex);
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .errorCode(errorCode.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 예상치 못한 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected internal server error", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(messageUtils.getMessageWithDefault("error.E999", "Internal server error"))
                .timestamp(LocalDateTime.now())
                .path(extractPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * WebRequest에서 요청 경로를 추출하는 헬퍼 메서드
     */
    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        return description.replace("uri=", "");
    }
} 