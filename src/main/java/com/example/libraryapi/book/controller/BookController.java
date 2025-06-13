package com.example.libraryapi.book.controller;

import com.example.libraryapi.book.dto.BookCategoryUpdateDto;
import com.example.libraryapi.book.dto.BookRequestDto;
import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.dto.BookStatusUpdateDto;
import com.example.libraryapi.book.service.BookService;
import com.example.libraryapi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "도서 등록 성공"),
        @ApiResponse(responseCode = "400", 
                     description = "잘못된 요청 (유효성 검증 실패)", 
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", 
                     description = "중복된 도서", 
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @GetMapping
    @Operation(summary = "도서 목록 조회", description = "전체 도서 목록을 조회합니다.")
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "도서 조회", description = "단일 도서 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "도서 조회 성공"),
        @ApiResponse(responseCode = "404", 
                     description = "도서를 찾을 수 없음", 
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponseDto> getBookById(
            @Parameter(description = "조회할 도서 ID", example = "1") 
            @PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "도서 검색", description = "제목, 저자, 카테고리 등 조건에 맞는 도서를 검색합니다. 여러 조건을 동시에 적용할 수 있습니다.")
    public ResponseEntity<List<BookResponseDto>> searchBooks(
            @Parameter(description = "저자 이름으로 검색 (부분 일치)", example = "J.K. 롤링") 
            @RequestParam(required = false) String author,
            
            @Parameter(description = "도서 제목으로 검색 (부분 일치)", example = "해리포터") 
            @RequestParam(required = false) String title,
            
            @Parameter(description = "카테고리 이름으로 검색 (정확히 일치)", example = "소설") 
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(bookService.searchBooks(author, title, category));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "도서 상태 변경", description = "도서의 상태(대여 가능/불가능)를 변경합니다.")
    public ResponseEntity<BookResponseDto> updateBookStatus(
            @Parameter(description = "상태를 변경할 도서 ID", example = "1") 
            @PathVariable Integer id,
            @Valid @RequestBody BookStatusUpdateDto request) {
        return ResponseEntity.ok(bookService.updateBookStatus(id, request));
    }

    @PutMapping("/{id}/categories")
    @Operation(summary = "도서 카테고리 수정", description = "도서의 카테고리 목록을 교체합니다.")
    public ResponseEntity<BookResponseDto> updateBookCategories(
            @Parameter(description = "카테고리를 수정할 도서 ID", example = "1") 
            @PathVariable Integer id,
            @Valid @RequestBody BookCategoryUpdateDto request) {
        return ResponseEntity.ok(bookService.updateBookCategories(id, request));
    }
} 