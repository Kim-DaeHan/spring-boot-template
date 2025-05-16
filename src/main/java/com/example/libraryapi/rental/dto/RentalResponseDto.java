package com.example.libraryapi.rental.dto;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "대여 응답 DTO")
public record RentalResponseDto(
    @Schema(description = "대여 ID", example = "1")
    Integer id,
    
    @Schema(description = "도서 ID", example = "1")
    Integer bookId,
    
    @Schema(description = "도서 제목", example = "클린 코드")
    String bookTitle,
    
    @Schema(description = "반납 예정일", example = "2025-12-01")
    LocalDate dueDate,
    
    @Schema(description = "대여일시", example = "2025-12-01T09:00:00")
    LocalDateTime borrowedAt,
    
    @Schema(description = "반납일시", example = "2025-12-10T09:00:00")
    LocalDateTime returnedAt,
    
    @Schema(description = "대여 상태", example = "BORROWED")
    RentalStatus status,
    
    @Schema(description = "생성일시", example = "2025-12-01T09:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "수정일시", example = "2025-12-01T09:00:00")
    LocalDateTime updatedAt
) {
    /**
     * Rental 엔티티로부터 RentalResponseDto를 생성합니다.
     */
    public static RentalResponseDto from(Rental rental) {
        return new RentalResponseDto(
            rental.getId(),
            rental.getBook().getId(),
            rental.getBook().getTitle(),
            rental.getDueDate(),
            rental.getCreatedAt(),   // 대여일시는 생성일시와 동일하게 처리
            null,                   // 반납일시는 현재 엔티티에 없으므로 null로 설정
            rental.getStatus(),
            rental.getCreatedAt(),
            rental.getUpdatedAt()
        );
    }
    
    /**
     * Rental 엔티티 리스트로부터 RentalResponseDto 리스트를 생성합니다.
     */
    public static List<RentalResponseDto> listFrom(List<Rental> rentals) {
        return rentals.stream()
            .map(RentalResponseDto::from)
            .collect(Collectors.toList());
    }
} 