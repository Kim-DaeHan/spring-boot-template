package com.example.libraryapi.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "도서 카테고리 수정 요청 DTO")
public record BookCategoryUpdateDto(
    @Schema(description = "카테고리 ID 목록", example = "[1, 2]")
    @NotEmpty(message = "최소 1개 이상의 카테고리가 필요합니다")
    @Size(min = 1, message = "최소 1개 이상의 카테고리가 필요합니다")
    Set<Integer> categoryIds
) {} 