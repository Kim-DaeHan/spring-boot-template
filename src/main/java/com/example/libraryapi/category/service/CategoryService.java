package com.example.libraryapi.category.service;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.mapper.BookMapper;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.dto.CategoryRequestDto;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.category.mapper.CategoryMapper;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        // 중복 검사
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category with name: " + request.name() + " already exists");
        }
        
        // 카테고리 생성
        Category category = Category.builder()
                .name(request.name())
                .build();
        
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<BookResponseDto> getBooksByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        return bookMapper.toResponseList(bookRepository.findByCategories(category));
    }
} 