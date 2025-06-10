package com.example.libraryapi.book.dto;

import com.example.libraryapi.book.entity.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "도서 상태 변경 요청 DTO")
public record BookStatusUpdateDto(
    @Schema(description = "도서 상태", example = "UNAVAILABLE")
    @NotNull(message = "상태 값은 필수입니다.")
    BookStatus status
) {} 