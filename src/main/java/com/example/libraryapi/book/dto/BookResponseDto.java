package com.example.libraryapi.book.dto;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    Set<CategoryResponseDto> categories
) {
    /**
     * Book 엔티티로부터 BookResponseDto를 생성합니다.
     */
    public static BookResponseDto from(Book book) {
        if (book == null) {
            return null;
        }
        
        Set<CategoryResponseDto> categoryDTOs = CategoryResponseDto.setFrom(book.getCategories());
        
        return new BookResponseDto(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getStatus(),
            categoryDTOs
        );
    }
    
    /**
     * Book 엔티티 리스트로부터 BookResponseDto 리스트를 생성합니다.
     */
    public static List<BookResponseDto> listFrom(List<Book> books) {
        if (books == null) {
            return List.of();
        }
        
        return books.stream()
            .map(BookResponseDto::from)
            .collect(Collectors.toList());
    }
} 