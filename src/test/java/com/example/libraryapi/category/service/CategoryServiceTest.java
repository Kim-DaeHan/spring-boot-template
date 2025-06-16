package com.example.libraryapi.category.service;

import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.dto.CategoryRequestDto;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.DuplicateResourceException;
import com.example.libraryapi.exception.MessageUtils;
import com.example.libraryapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MessageUtils messageUtils;

    @InjectMocks
    private CategoryService categoryService;

    private Category mockCategory;
    private CategoryRequestDto mockCategoryRequest;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setId(1);
        mockCategory.setName("소설");

        mockCategoryRequest = new CategoryRequestDto("소설");
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_Success() {
        // Given
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(mockCategory);

        // When
        CategoryResponseDto result = categoryService.createCategory(mockCategoryRequest);

        // Then
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("소설");
        verify(categoryRepository).existsByName(anyString());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("중복 카테고리명으로 생성 실패")
    void createCategory_DuplicateName() {
        // Given
        when(categoryRepository.existsByName(anyString())).thenReturn(true);
        when(messageUtils.getMessageWithDefault(anyString(), anyString(), any())).thenReturn("Category name already exists: 소설");

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(mockCategoryRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("소설");
        
        verify(categoryRepository).existsByName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("모든 카테고리 조회 성공")
    void getAllCategories_Success() {
        // Given
        when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));

        // When
        List<CategoryResponseDto> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("소설");
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("카테고리 ID로 조회 성공")
    void getCategoryById_Success() {
        // Given
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));

        // When
        CategoryResponseDto result = categoryService.getCategoryById(1);

        // Then
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("소설");
        verify(categoryRepository).findById(1);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID로 조회 실패")
    void getCategoryById_NotFound() {
        // Given
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());
        when(messageUtils.getMessageWithDefault(anyString(), anyString(), any())).thenReturn("Category not found. ID: 999");

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
        
        verify(categoryRepository).findById(999);
    }
} 