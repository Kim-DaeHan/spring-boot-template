package com.example.libraryapi.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리 등록 요청 DTO")
public record CategoryRequestDto(
    @Schema(description = "카테고리 이름", example = "소설")
    @NotBlank(message = "카테고리 이름은 필수입니다")
    String name
) {} 