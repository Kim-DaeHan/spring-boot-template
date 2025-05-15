package com.example.libraryapi.book.dto;

import com.example.libraryapi.category.dto.CategoryDto;
import com.example.libraryapi.book.entity.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "도서 응답 DTO")
public record BookResponseDto(
    @Schema(description = "도서 ID", example = "1")
    Integer id,
    
    @Schema(description = "도서 제목", example = "해리포터와 비밀의 방")
    String title,
    
    @Schema(description = "저자", example = "J.K. 롤링")
    String author,
    
    @Schema(description = "도서 상태", example = "AVAILABLE")
    BookStatus status,
    
    @Schema(description = "카테고리 목록")
    Set<CategoryDto> categories
) {} 