package com.example.libraryapi.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BookRequestDto(
    @NotBlank(message = "도서 제목은 필수입니다")
    String title,
    
    @NotBlank(message = "지은이는 필수입니다")
    String author,
    
    @NotEmpty(message = "최소 1개 이상의 카테고리가 필요합니다")
    @Size(min = 1, message = "최소 1개 이상의 카테고리가 필요합니다")
    Set<Long> categoryIds
) {} 