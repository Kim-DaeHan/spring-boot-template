package com.example.libraryapi.rental.controller;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.category.dto.CategoryDto;
import com.example.libraryapi.rental.dto.RentalRequestDto;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.entity.RentalStatus;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RentalController.class)
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalService rentalService;

    private RentalResponseDto sampleRentalResponse;
    private RentalRequestDto sampleRentalRequest;

    @BeforeEach
    void setUp() {
        BookResponseDto bookResponseDto = new BookResponseDto(
                1, 
                "해리포터와 비밀의 방", 
                "J.K. 롤링", 
                BookStatus.UNAVAILABLE,
                Set.of(new CategoryDto(1, "소설"))
        );
        
        sampleRentalResponse = new RentalResponseDto(
                1,
                bookResponseDto,
                LocalDate.now().plusDays(14),
                null,
                RentalStatus.BORROWED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        sampleRentalRequest = new RentalRequestDto(
                1,
                LocalDate.now().plusDays(14)
        );
    }

    @Test
    @DisplayName("도서 대여 API 테스트")
    void borrowBook() throws Exception {
        when(rentalService.borrowBook(any(RentalRequestDto.class))).thenReturn(sampleRentalResponse);

        mockMvc.perform(post("/api/rentals/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRentalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(jsonPath("$.status").value("BORROWED"));
    }

    @Test
    @DisplayName("도서 반납 API 테스트")
    void returnBook() throws Exception {
        RentalResponseDto returnedRental = new RentalResponseDto(
                1,
                sampleRentalResponse.book(),
                sampleRentalResponse.dueDate(),
                LocalDate.now(),
                RentalStatus.RETURNED,
                sampleRentalResponse.createdAt(),
                LocalDateTime.now()
        );

        when(rentalService.returnBook(anyInt())).thenReturn(returnedRental);

        mockMvc.perform(put("/api/rentals/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.returnedDate").isNotEmpty());
    }

    @Test
    @DisplayName("전체 대여 목록 조회 API 테스트")
    void getAllRentals() throws Exception {
        when(rentalService.getAllRentals()).thenReturn(List.of(sampleRentalResponse));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].book.title").value("해리포터와 비밀의 방"));
    }

    @Test
    @DisplayName("대여 ID로 대여 조회 API 테스트")
    void getRentalById() throws Exception {
        when(rentalService.getRentalById(anyInt())).thenReturn(sampleRentalResponse);

        mockMvc.perform(get("/api/rentals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.book.id").value(1));
    }

    @Test
    @DisplayName("연체 목록 조회 API 테스트")
    void getOverdueRentals() throws Exception {
        RentalResponseDto overdueRental = new RentalResponseDto(
                1,
                sampleRentalResponse.book(),
                LocalDate.now().minusDays(1),
                null,
                RentalStatus.OVERDUE,
                sampleRentalResponse.createdAt(),
                LocalDateTime.now()
        );

        when(rentalService.getOverdueRentals()).thenReturn(List.of(overdueRental));

        mockMvc.perform(get("/api/rentals/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("OVERDUE"));
    }
} 