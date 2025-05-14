package com.example.libraryapi.book.dto;

import com.example.libraryapi.book.entity.BookStatus;
import jakarta.validation.constraints.NotNull;

public record BookStatusUpdateDto(
    @NotNull(message = "상태 값은 필수입니다")
    BookStatus status
) {} 