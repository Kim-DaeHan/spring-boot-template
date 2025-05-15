package com.example.libraryapi.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "도서 대여 요청 DTO")
public record RentalRequestDto(
    @Schema(description = "도서 ID", example = "1")
    @NotNull(message = "도서 ID는 필수입니다")
    Integer bookId,
    
    @Schema(description = "반납 예정일", example = "2025-12-31")
    @NotNull(message = "반납 예정일은 필수입니다")
    LocalDate dueDate
) {} 