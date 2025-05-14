package com.example.libraryapi.rental.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RentalRequestDto(
    @NotNull(message = "도서 ID는 필수입니다")
    Long bookId,
    
    @NotNull(message = "반납 예정일은 필수입니다")
    LocalDate dueDate
) {} 