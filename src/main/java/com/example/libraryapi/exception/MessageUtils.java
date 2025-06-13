package com.example.libraryapi.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 국제화(i18n) 메시지 처리를 위한 유틸리티 클래스입니다.
 * MessageSource를 활용하여 다국어 메시지를 제공합니다.
 */
@Component
public class MessageUtils {
    
    private final MessageSource messageSource;
    
    @Autowired
    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    /**
     * 현재 로케일에 맞는 메시지를 반환합니다.
     * 
     * @param code 메시지 코드
     * @param args 메시지 파라미터
     * @return 로케일에 맞는 메시지
     */
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
    
    /**
     * 지정된 로케일에 맞는 메시지를 반환합니다.
     * 
     * @param code 메시지 코드
     * @param locale 로케일
     * @param args 메시지 파라미터
     * @return 지정된 로케일에 맞는 메시지
     */
    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }
    
    /**
     * 메시지를 찾을 수 없을 때 기본 메시지를 반환합니다.
     * 
     * @param code 메시지 코드
     * @param defaultMessage 기본 메시지
     * @param args 메시지 파라미터
     * @return 메시지 또는 기본 메시지
     */
    public String getMessageWithDefault(String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }
} 