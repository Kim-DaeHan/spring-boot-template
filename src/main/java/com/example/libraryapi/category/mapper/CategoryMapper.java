package com.example.libraryapi.category.mapper;

import com.example.libraryapi.category.dto.CategoryDto;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
    
    public List<CategoryDto> toDtoList(List<Category> categories) {
        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public CategoryResponseDto toResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryResponseDto(
                category.getId(),
                category.getName()
        );
    }
    
    public List<CategoryResponseDto> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
} 