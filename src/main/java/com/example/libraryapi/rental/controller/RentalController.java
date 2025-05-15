package com.example.libraryapi.rental.controller;

import com.example.libraryapi.rental.dto.RentalRequestDto;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental API", description = "도서 대여 관련 API")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/borrow")
    @Operation(summary = "도서 대여", description = "도서를 대여합니다. 대여 가능한 도서에 한해 대여가 가능합니다.")
    public ResponseEntity<RentalResponseDto> borrowBook(@Valid @RequestBody RentalRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.borrowBook(request));
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "도서 반납", description = "대여한 도서를 반납합니다.")
    public ResponseEntity<RentalResponseDto> returnBook(
            @Parameter(description = "반납할 대여 ID", example = "1") 
            @PathVariable Integer id) {
        return ResponseEntity.ok(rentalService.returnBook(id));
    }

    @GetMapping
    @Operation(summary = "대여 목록 조회", description = "전체 대여 목록을 조회합니다.")
    public ResponseEntity<List<RentalResponseDto>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @GetMapping("/{id}")
    @Operation(summary = "대여 상세 조회", description = "단일 대여 정보를 조회합니다.")
    public ResponseEntity<RentalResponseDto> getRentalById(
            @Parameter(description = "조회할 대여 ID", example = "1") 
            @PathVariable Integer id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @GetMapping("/overdue")
    @Operation(summary = "연체 목록 조회", description = "반납 기한이 지난 연체 도서 목록을 조회합니다.")
    public ResponseEntity<List<RentalResponseDto>> getOverdueRentals() {
        return ResponseEntity.ok(rentalService.getOverdueRentals());
    }
} 