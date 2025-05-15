package com.example.libraryapi.book.controller;

import com.example.libraryapi.book.dto.BookCategoryUpdateDto;
import com.example.libraryapi.book.dto.BookRequestDto;
import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.dto.BookStatusUpdateDto;
import com.example.libraryapi.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book API", description = "도서 관련 API")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "도서 등록", description = "신규 도서를 등록합니다. 최소 1개 이상의 카테고리가 필요합니다.")
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @GetMapping
    @Operation(summary = "도서 목록 조회", description = "전체 도서 목록을 조회합니다.")
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "도서 상세 조회", description = "단일 도서의 상세 정보를 조회합니다.")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "도서 검색", description = "조건에 맞는 도서를 검색합니다.")
    public ResponseEntity<List<BookResponseDto>> searchBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(bookService.searchBooks(author, title, category));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "도서 상태 변경", description = "도서의 상태를 변경합니다 (available/unavailable).")
    public ResponseEntity<BookResponseDto> updateBookStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookStatusUpdateDto request) {
        return ResponseEntity.ok(bookService.updateBookStatus(id, request));
    }

    @PutMapping("/{id}/categories")
    @Operation(summary = "도서 카테고리 수정", description = "도서의 카테고리 목록을 교체합니다.")
    public ResponseEntity<BookResponseDto> updateBookCategories(
            @PathVariable Long id,
            @Valid @RequestBody BookCategoryUpdateDto request) {
        return ResponseEntity.ok(bookService.updateBookCategories(id, request));
    }
} 