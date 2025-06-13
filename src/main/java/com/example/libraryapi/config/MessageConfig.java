package com.example.libraryapi.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

/**
 * 국제화(i18n) 설정을 위한 Configuration 클래스입니다.
 * 다국어 메시지 처리와 로케일 설정을 담당합니다.
 */
@Configuration
public class MessageConfig {

    /**
     * 메시지 소스 설정
     * messages.properties, messages_en.properties 등의 파일을 읽어옵니다.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false); // 키를 찾지 못하면 예외 발생
        messageSource.setFallbackToSystemLocale(false); // 시스템 로케일 사용 안함
        return messageSource;
    }

    /**
     * 로케일 리졸버 설정
     * Accept-Language 헤더를 기반으로 로케일을 결정합니다.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("ko")); // 기본값: 한국어
        localeResolver.setSupportedLocales(Arrays.asList(
            new Locale("ko"), // 한국어
            new Locale("en")  // 영어
        ));
        return localeResolver;
    }


} 