package com.example.libraryapi.book.mapper;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.category.dto.CategoryDto;
import com.example.libraryapi.category.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public BookResponseDto toResponse(Book book) {
        if (book == null) {
            return null;
        }
        
        Set<CategoryDto> categoryDTOs = book.getCategories().stream()
                .map(this::categoryToDTO)
                .collect(Collectors.toSet());
        
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getStatus(),
                categoryDTOs
        );
    }
    
    public List<BookResponseDto> toResponseList(List<Book> books) {
        return books.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    private CategoryDto categoryToDTO(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
} 