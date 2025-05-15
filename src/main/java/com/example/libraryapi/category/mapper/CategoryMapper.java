package com.example.libraryapi.category.mapper;

import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    
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