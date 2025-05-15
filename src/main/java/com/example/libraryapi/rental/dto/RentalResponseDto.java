package com.example.libraryapi.rental.dto;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.rental.entity.RentalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "대여 응답 DTO")
public record RentalResponseDto(
    @Schema(description = "대여 ID", example = "1")
    Integer id,
    
    @Schema(description = "대여한 도서 정보")
    BookResponseDto book,
    
    @Schema(description = "반납 예정일", example = "2025-12-31")
    LocalDate dueDate,
    
    @Schema(description = "실제 반납일", example = "2025-12-25")
    LocalDate returnedDate,
    
    @Schema(description = "대여 상태", example = "BORROWED")
    RentalStatus status,
    
    @Schema(description = "생성일시", example = "2025-12-01T09:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "수정일시", example = "2025-12-01T09:00:00")
    LocalDateTime updatedAt
) {} 