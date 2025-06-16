package com.example.libraryapi.rental.controller;

import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.MessageUtils;
import com.example.libraryapi.rental.dto.RentalRequestDto;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.entity.RentalStatus;
import com.example.libraryapi.rental.facade.RentalFacade;
import com.example.libraryapi.rental.repository.RentalRepository;
import com.example.libraryapi.rental.service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private MessageUtils messageUtils;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private RentalRepository rentalRepository;

    @MockBean
    private RentalFacade rentalFacade;

    private RentalRequestDto sampleRentalRequest;
    private RentalResponseDto sampleRentalResponse;

    @BeforeEach
    void setUp() {
        sampleRentalRequest = new RentalRequestDto(1, LocalDate.now().plusDays(14));
        
        sampleRentalResponse = new RentalResponseDto(
                1,                              // id
                1,                              // bookId
                "해리포터와 비밀의 방",               // bookTitle
                LocalDate.now().plusDays(14),   // dueDate
                LocalDateTime.now(),            // borrowedAt
                null,                           // returnedAt
                RentalStatus.BORROWED,          // status
                LocalDateTime.now(),            // createdAt
                LocalDateTime.now()             // updatedAt
        );
    }

    @Test
    @DisplayName("도서 대여 API 성공")
    void borrowBook_Success() throws Exception {
        // Given
        when(rentalService.borrowBook(any(RentalRequestDto.class)))
                .thenReturn(sampleRentalResponse);

        // When & Then
        mockMvc.perform(post("/api/rentals/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRentalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleRentalResponse.id()))
                .andExpect(jsonPath("$.bookTitle").value(sampleRentalResponse.bookTitle()))
                .andExpect(jsonPath("$.status").value("BORROWED"));
    }

    @Test
    @DisplayName("도서 반납 API 성공")
    void returnBook_Success() throws Exception {
        // Given
        RentalResponseDto returnedResponse = new RentalResponseDto(
                1,                              // id
                1,                              // bookId
                "해리포터와 비밀의 방",               // bookTitle
                LocalDate.now().plusDays(14),   // dueDate
                LocalDateTime.now(),            // borrowedAt
                LocalDateTime.now(),            // returnedAt
                RentalStatus.RETURNED,          // status
                LocalDateTime.now(),            // createdAt
                LocalDateTime.now()             // updatedAt
        );
        
        when(rentalService.returnBook(1)).thenReturn(returnedResponse);

        // When & Then
        mockMvc.perform(put("/api/rentals/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedResponse.id()))
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    @DisplayName("전체 대여 목록 조회 API 테스트")
    void getAllRentals() throws Exception {
        when(rentalService.getAllRentals()).thenReturn(List.of(sampleRentalResponse));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookTitle").value("해리포터와 비밀의 방"));
    }

    @Test
    @DisplayName("대여 ID로 대여 조회 API 테스트")
    void getRentalById() throws Exception {
        when(rentalService.getRentalById(anyInt())).thenReturn(sampleRentalResponse);

        mockMvc.perform(get("/api/rentals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookTitle").value("해리포터와 비밀의 방"));
    }
} 