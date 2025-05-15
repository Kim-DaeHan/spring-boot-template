package com.example.libraryapi.book.controller;

import com.example.libraryapi.book.dto.BookCategoryUpdateDto;
import com.example.libraryapi.book.dto.BookRequestDto;
import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.dto.BookStatusUpdateDto;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.service.BookService;
import com.example.libraryapi.category.dto.CategoryDto;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private BookResponseDto sampleBookResponse;
    private BookRequestDto sampleBookRequest;

    @BeforeEach
    void setUp() {
        CategoryDto category = new CategoryDto(1, "소설");
        
        sampleBookResponse = new BookResponseDto(
                1,
                "해리포터와 비밀의 방",
                "J.K. 롤링",
                BookStatus.AVAILABLE,
                Set.of(category)
        );
        
        sampleBookRequest = new BookRequestDto(
                "해리포터와 비밀의 방",
                "J.K. 롤링",
                Set.of(1)
        );
    }

    @Test
    @DisplayName("도서 등록 API 테스트")
    void createBook() throws Exception {
        when(bookService.createBook(any(BookRequestDto.class))).thenReturn(sampleBookResponse);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleBookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("해리포터와 비밀의 방"))
                .andExpect(jsonPath("$.author").value("J.K. 롤링"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.categories[0].id").value(1));
    }

    @Test
    @DisplayName("전체 도서 목록 조회 API 테스트")
    void getAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(sampleBookResponse));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("해리포터와 비밀의 방"));
    }

    @Test
    @DisplayName("도서 ID로 도서 조회 API 테스트")
    void getBookById() throws Exception {
        when(bookService.getBookById(anyInt())).thenReturn(sampleBookResponse);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("해리포터와 비밀의 방"));
    }

    @Test
    @DisplayName("도서 검색 API 테스트")
    void searchBooks() throws Exception {
        when(bookService.searchBooks(anyString(), anyString(), anyString()))
                .thenReturn(List.of(sampleBookResponse));

        mockMvc.perform(get("/api/books/search")
                .param("author", "롤링")
                .param("title", "해리포터")
                .param("category", "소설"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("해리포터와 비밀의 방"));
    }

    @Test
    @DisplayName("도서 상태 변경 API 테스트")
    void updateBookStatus() throws Exception {
        BookStatusUpdateDto statusUpdateDto = new BookStatusUpdateDto(BookStatus.UNAVAILABLE);
        BookResponseDto updatedBook = new BookResponseDto(
                1, "해리포터와 비밀의 방", "J.K. 롤링", BookStatus.UNAVAILABLE, 
                Set.of(new CategoryDto(1, "소설"))
        );
        
        when(bookService.updateBookStatus(anyInt(), any(BookStatusUpdateDto.class)))
                .thenReturn(updatedBook);

        mockMvc.perform(patch("/api/books/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNAVAILABLE"));
    }

    @Test
    @DisplayName("도서 카테고리 수정 API 테스트")
    void updateBookCategories() throws Exception {
        BookCategoryUpdateDto categoryUpdateDto = new BookCategoryUpdateDto(Set.of(1, 2));
        BookResponseDto updatedBook = new BookResponseDto(
                1, "해리포터와 비밀의 방", "J.K. 롤링", BookStatus.AVAILABLE, 
                Set.of(new CategoryDto(1, "소설"), new CategoryDto(2, "판타지"))
        );
        
        when(bookService.updateBookCategories(anyInt(), any(BookCategoryUpdateDto.class)))
                .thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(2));
    }
} 