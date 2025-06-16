package com.example.libraryapi.category.controller;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.dto.CategoryRequestDto;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.service.CategoryService;
import com.example.libraryapi.exception.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private MessageUtils messageUtils;

    @MockBean
    private BookRepository bookRepository;

    private CategoryResponseDto sampleCategoryResponse;
    private CategoryRequestDto sampleCategoryRequest;
    private BookResponseDto sampleBookResponse;

    @BeforeEach
    void setUp() {
        sampleCategoryResponse = new CategoryResponseDto(1, "소설");
        sampleCategoryRequest = new CategoryRequestDto("소설");
        
        CategoryResponseDto category = new CategoryResponseDto(1, "소설");
        sampleBookResponse = new BookResponseDto(
                1,
                "해리포터와 비밀의 방",
                "J.K. 롤링",
                BookStatus.AVAILABLE,
                Set.of(category)
        );
    }

    @Test
    @DisplayName("카테고리 등록 API 테스트")
    void createCategory() throws Exception {
        when(categoryService.createCategory(any(CategoryRequestDto.class)))
                .thenReturn(sampleCategoryResponse);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCategoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("소설"));
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 API 테스트")
    void getAllCategories() throws Exception {
        when(categoryService.getAllCategories())
                .thenReturn(List.of(sampleCategoryResponse));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("소설"));
    }

    @Test
    @DisplayName("카테고리 ID로 카테고리 조회 API 테스트")
    void getCategoryById() throws Exception {
        when(categoryService.getCategoryById(anyInt()))
                .thenReturn(sampleCategoryResponse);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("소설"));
    }

    @Test
    @DisplayName("카테고리별 도서 조회 API 테스트")
    void getBooksByCategory() throws Exception {
        when(categoryService.getBooksByCategory(anyInt()))
                .thenReturn(List.of(sampleBookResponse));

        mockMvc.perform(get("/api/categories/1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("해리포터와 비밀의 방"))
                .andExpect(jsonPath("$[0].author").value("J.K. 롤링"));
    }
} 