package com.example.libraryapi.category.service;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.dto.CategoryRequestDto;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.DuplicateResourceException;
import com.example.libraryapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 카테고리 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    /**
     * 새로운 카테고리를 생성합니다.
     */
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        // 중복 검사
        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("카테고리 이름이 이미 존재합니다: " + request.name());
        }
        
        // 카테고리 생성
        Category category = Category.builder()
                .name(request.name())
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDto.from(savedCategory);
    }

    /**
     * 모든 카테고리 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryResponseDto.listFrom(categories);
    }

    /**
     * ID로 특정 카테고리를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Integer id) {
        Category category = findCategoryById(id);
        return CategoryResponseDto.from(category);
    }

    /**
     * 특정 카테고리에 속한 도서 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<BookResponseDto> getBooksByCategory(Integer categoryId) {
        Category category = findCategoryById(categoryId);
        
        // 카테고리에 속한 도서 조회
        List<com.example.libraryapi.book.entity.Book> books = bookRepository.findByCategories(category);
        return BookResponseDto.listFrom(books);
    }
    
    /**
     * ID로 카테고리를 조회하는 내부 메소드
     */
    private Category findCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
    }
} 