package com.example.libraryapi.book.dto;

import com.example.libraryapi.category.dto.CategoryDto;
import com.example.libraryapi.book.entity.BookStatus;

import java.util.Set;

public record BookResponseDto(
    Long id,
    String title,
    String author,
    BookStatus status,
    Set<CategoryDto> categories
) {} 