package com.example.libraryapi.book.service;

import com.example.libraryapi.book.dto.BookCategoryUpdateDto;
import com.example.libraryapi.book.dto.BookRequestDto;
import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.dto.BookStatusUpdateDto;
import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.mapper.BookMapper;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookResponseDto createBook(BookRequestDto request) {
        // 카테고리 조회
        Set<Category> categories = request.categoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)))
                .collect(Collectors.toSet());

        // 도서 생성
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .build();
        
        // 저장
        Book savedBook = bookRepository.save(book);
        
        // 카테고리 연결
        savedBook.updateCategories(categories);
        
        return bookMapper.toResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookResponseDto> getAllBooks() {
        return bookMapper.toResponseList(bookRepository.findAll());
    }

    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return bookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponseDto> searchBooks(String author, String title, String category) {
        if (author != null && !author.isBlank()) {
            return bookMapper.toResponseList(bookRepository.findByAuthorContaining(author));
        } else if (title != null && !title.isBlank()) {
            return bookMapper.toResponseList(bookRepository.findByTitleContaining(title));
        } else if (category != null && !category.isBlank()) {
            return bookMapper.toResponseList(bookRepository.findByCategoryName(category));
        }
        
        // 검색 조건이 없으면 전체 도서 반환
        return getAllBooks();
    }

    @Transactional
    public BookResponseDto updateBookStatus(Long id, BookStatusUpdateDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        book.setStatus(request.status());
        
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional
    public BookResponseDto updateBookCategories(Long id, BookCategoryUpdateDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        // 카테고리 조회
        Set<Category> categories = request.categoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId)))
                .collect(Collectors.toSet());
        
        // 카테고리 업데이트
        book.updateCategories(categories);
        
        return bookMapper.toResponse(bookRepository.save(book));
    }
} 