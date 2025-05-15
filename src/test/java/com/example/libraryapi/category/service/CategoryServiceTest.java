package com.example.libraryapi.category.service;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.mapper.BookMapper;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.dto.CategoryDto;
import com.example.libraryapi.category.dto.CategoryRequestDto;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.category.mapper.CategoryMapper;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.DuplicateResourceException;
import com.example.libraryapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category mockCategory;
    private Book mockBook;
    private CategoryResponseDto mockCategoryResponse;
    private CategoryRequestDto mockCategoryRequest;
    private BookResponseDto mockBookResponse;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id(1)
                .name("소설")
                .books(new HashSet<>())
                .build();

        mockBook = Book.builder()
                .id(1)
                .title("해리포터와 비밀의 방")
                .author("J.K. 롤링")
                .status(BookStatus.AVAILABLE)
                .categories(Set.of(mockCategory))
                .build();
        
        mockCategory.getBooks().add(mockBook);

        mockCategoryResponse = new CategoryResponseDto(1, "소설");
        mockCategoryRequest = new CategoryRequestDto("소설");

        CategoryDto categoryDto = new CategoryDto(1, "소설");
        mockBookResponse = new BookResponseDto(
                1,
                "해리포터와 비밀의 방",
                "J.K. 롤링",
                BookStatus.AVAILABLE,
                Set.of(categoryDto)
        );
    }

    @Test
    @DisplayName("카테고리 등록 테스트")
    void createCategory() {
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(mockCategory);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(mockCategoryResponse);

        CategoryResponseDto result = categoryService.createCategory(mockCategoryRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("소설");

        verify(categoryRepository, times(1)).existsByName(anyString());
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toResponse(any(Category.class));
    }

    @Test
    @DisplayName("중복된 카테고리 이름으로 등록 시 예외 발생 테스트")
    void createCategoryDuplicate() {
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(mockCategoryRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(categoryRepository, times(1)).existsByName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 테스트")
    void getAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));
        when(categoryMapper.toResponseList(anyList())).thenReturn(List.of(mockCategoryResponse));

        List<CategoryResponseDto> results = categoryService.getAllCategories();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("소설");

        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toResponseList(anyList());
    }

    @Test
    @DisplayName("ID로 카테고리 조회 테스트")
    void getCategoryById() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(mockCategory));
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(mockCategoryResponse);

        CategoryResponseDto result = categoryService.getCategoryById(1);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("소설");

        verify(categoryRepository, times(1)).findById(anyInt());
        verify(categoryMapper, times(1)).toResponse(any(Category.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID로 조회 시 예외 발생 테스트")
    void getCategoryByIdNotFound() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categoryRepository, times(1)).findById(anyInt());
        verify(categoryMapper, never()).toResponse(any(Category.class));
    }

    @Test
    @DisplayName("카테고리별 도서 조회 테스트")
    void getBooksByCategory() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(mockCategory));
        when(bookRepository.findByCategories(any(Category.class))).thenReturn(List.of(mockBook));
        when(bookMapper.toResponseList(anyList())).thenReturn(List.of(mockBookResponse));

        List<BookResponseDto> results = categoryService.getBooksByCategory(1);

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("해리포터와 비밀의 방");

        verify(categoryRepository, times(1)).findById(anyInt());
        verify(bookRepository, times(1)).findByCategories(any(Category.class));
        verify(bookMapper, times(1)).toResponseList(anyList());
    }
} 