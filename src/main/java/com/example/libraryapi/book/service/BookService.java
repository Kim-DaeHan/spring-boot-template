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
import com.example.libraryapi.exception.ResourceInUseException;
import com.example.libraryapi.exception.ResourceNotFoundException;
import com.example.libraryapi.rental.repository.RentalRepository;
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
    private final RentalRepository rentalRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookResponseDto createBook(BookRequestDto request) {
        // 카테고리 조회
        Set<Category> categories = request.categoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id)))
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
    public BookResponseDto getBookById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다. ID: " + id));
        return bookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponseDto> searchBooks(String author, String title, String category) {
        // 빈 문자열인 경우 null로 변환하여 처리
        String authorParam = (author != null && !author.isBlank()) ? author : null;
        String titleParam = (title != null && !title.isBlank()) ? title : null;
        String categoryParam = (category != null && !category.isBlank()) ? category : null;
        
        // 하나의 쿼리로 모든 필터 적용
        List<Book> results = bookRepository.findByFilters(categoryParam, titleParam, authorParam);
        return bookMapper.toResponseList(results);
    }

    @Transactional
    public BookResponseDto updateBookStatus(Integer id, BookStatusUpdateDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다. ID: " + id));
        
        // 대여 중인 책인지 확인
        rentalRepository.findActiveRentalByBookId(id).ifPresent(rental -> {
            throw new ResourceInUseException("대여 중인 책의 상태는 변경할 수 없습니다. 책 ID: " + id);
        });
        
        book.setStatus(request.status());
        
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional
    public BookResponseDto updateBookCategories(Integer id, BookCategoryUpdateDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다. ID: " + id));
        
        // 카테고리 조회
        Set<Category> categories = request.categoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId)))
                .collect(Collectors.toSet());
        
        // 카테고리 업데이트
        book.updateCategories(categories);
        
        return bookMapper.toResponse(bookRepository.save(book));
    }
} 