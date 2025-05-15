package com.example.libraryapi.category.controller;

import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.category.dto.CategoryRequestDto;
import com.example.libraryapi.category.dto.CategoryResponseDto;
import com.example.libraryapi.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category API", description = "카테고리 관련 API")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "카테고리 등록", description = "새로운 카테고리를 생성합니다.")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @GetMapping
    @Operation(summary = "카테고리 목록 조회", description = "전체 카테고리 목록을 조회합니다.")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "카테고리 조회", description = "단일 카테고리 정보를 조회합니다.")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "카테고리별 도서 조회", description = "특정 카테고리에 속한 도서 목록을 조회합니다.")
    public ResponseEntity<List<BookResponseDto>> getBooksByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getBooksByCategory(id));
    }
} 