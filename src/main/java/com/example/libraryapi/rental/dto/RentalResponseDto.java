package com.example.libraryapi.rental.dto;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.rental.entity.RentalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RentalResponseDto(
    Long id,
    BookResponseDto book,
    LocalDate dueDate,
    LocalDate returnedDate,
    RentalStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 