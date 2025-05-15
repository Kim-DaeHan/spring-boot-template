package com.example.libraryapi.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 응답 DTO")
public record CategoryResponseDto(
    @Schema(description = "카테고리 ID", example = "1")
    Integer id,
    
    @Schema(description = "카테고리 이름", example = "문학")
    String name
) {} 