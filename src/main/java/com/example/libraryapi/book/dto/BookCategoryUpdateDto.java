package com.example.libraryapi.book.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BookCategoryUpdateDto(
    @NotEmpty(message = "최소 1개 이상의 카테고리가 필요합니다")
    @Size(min = 1, message = "최소 1개 이상의 카테고리가 필요합니다")
    Set<Long> categoryIds
) {} 