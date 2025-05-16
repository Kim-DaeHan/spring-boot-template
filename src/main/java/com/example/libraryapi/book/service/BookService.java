package com.example.libraryapi.book.service;

import com.example.libraryapi.book.dto.BookCategoryUpdateDto;
import com.example.libraryapi.book.dto.BookRequestDto;
import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.dto.BookStatusUpdateDto;
import com.example.libraryapi.book.entity.Book;
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

/**
 * 도서 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final RentalRepository rentalRepository;

    /**
     * 새로운 도서를 생성합니다.
     */
    @Transactional
    public BookResponseDto createBook(BookRequestDto request) {
        // 카테고리 조회
        Set<Category> categories = findCategoriesByIds(request.categoryIds());

        // 도서 생성
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .build();
        
        // 저장
        Book savedBook = bookRepository.save(book);
        
        // 카테고리 연결
        savedBook.updateCategories(categories);
        
        return BookResponseDto.from(savedBook);
    }

    /**
     * 모든 도서 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<BookResponseDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return BookResponseDto.listFrom(books);
    }

    /**
     * ID로 특정 도서를 조회합니다.
     */
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Integer id) {
        Book book = findBookById(id);
        return BookResponseDto.from(book);
    }

    /**
     * 도서를 검색합니다.
     */
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchBooks(String author, String title, String category) {
        // 빈 문자열인 경우 null로 변환하여 처리
        String authorParam = (author != null && !author.isBlank()) ? author : null;
        String titleParam = (title != null && !title.isBlank()) ? title : null;
        String categoryParam = (category != null && !category.isBlank()) ? category : null;
        
        // 하나의 쿼리로 모든 필터 적용
        List<Book> results = bookRepository.findByFilters(categoryParam, titleParam, authorParam);
        return BookResponseDto.listFrom(results);
    }

    /**
     * 도서 상태를 변경합니다.
     */
    @Transactional
    public BookResponseDto updateBookStatus(Integer id, BookStatusUpdateDto request) {
        Book book = findBookById(id);
        
        // 대여 중인 책인지 확인
        rentalRepository.findActiveRentalByBookId(id).ifPresent(rental -> {
            throw new ResourceInUseException("대여 중인 책의 상태는 변경할 수 없습니다. 책 ID: " + id);
        });
        
        book.setStatus(request.status());
        Book savedBook = bookRepository.save(book);
        
        return BookResponseDto.from(savedBook);
    }

    /**
     * 도서의 카테고리를 변경합니다.
     */
    @Transactional
    public BookResponseDto updateBookCategories(Integer id, BookCategoryUpdateDto request) {
        Book book = findBookById(id);
        
        // 카테고리 조회
        Set<Category> categories = findCategoriesByIds(request.categoryIds());
        
        // 카테고리 업데이트
        book.updateCategories(categories);
        Book savedBook = bookRepository.save(book);
        
        return BookResponseDto.from(savedBook);
    }
    
    /**
     * ID로 도서를 조회하는 내부 메소드
     */
    private Book findBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다. ID: " + id));
    }
    
    /**
     * ID 목록으로 카테고리를 조회하는 내부 메소드
     */
    private Set<Category> findCategoriesByIds(Set<Integer> categoryIds) {
        return categoryIds.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다. ID: " + id)))
                .collect(Collectors.toSet());
    }
} 