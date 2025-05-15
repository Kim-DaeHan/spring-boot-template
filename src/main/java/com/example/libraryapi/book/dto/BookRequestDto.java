package com.example.libraryapi.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "도서 등록 요청 DTO")
public record BookRequestDto(
    @Schema(description = "도서 제목", example = "해리포터와 비밀의 방")
    @NotBlank(message = "도서 제목은 필수입니다")
    String title,
    
    @Schema(description = "저자", example = "J.K. 롤링")
    @NotBlank(message = "지은이는 필수입니다")
    String author,
    
    @Schema(description = "카테고리 ID 목록", example = "[1]")
    @NotEmpty(message = "최소 1개 이상의 카테고리가 필요합니다")
    @Size(min = 1, message = "최소 1개 이상의 카테고리가 필요합니다")
    Set<Integer> categoryIds
) {} 