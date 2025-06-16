package com.example.libraryapi.book.service;

import com.example.libraryapi.book.dto.BookRequestDto;
import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.MessageUtils;
import com.example.libraryapi.exception.ResourceNotFoundException;
import com.example.libraryapi.rental.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private MessageUtils messageUtils;

    @InjectMocks
    private BookService bookService;

    private Book mockBook;
    private Category mockCategory;
    private BookRequestDto mockBookRequest;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setId(1);
        mockCategory.setName("소설");

        mockBook = new Book();
        mockBook.setId(1);
        mockBook.setTitle("해리포터와 비밀의 방");
        mockBook.setAuthor("J.K. 롤링");
        mockBook.setStatus(BookStatus.AVAILABLE);
        mockBook.getCategories().add(mockCategory);

        mockBookRequest = new BookRequestDto(
                "해리포터와 비밀의 방",
                "J.K. 롤링",
                Set.of(1)
        );
    }

    @Test
    @DisplayName("도서 생성 성공")
    void createBook_Success() {
        // Given
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        // When
        BookResponseDto result = bookService.createBook(mockBookRequest);

        // Then
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.title()).isEqualTo("해리포터와 비밀의 방");
        assertThat(result.author()).isEqualTo("J.K. 롤링");
        verify(categoryRepository).findById(1);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("모든 도서 조회 성공")
    void getAllBooks_Success() {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of(mockBook));

        // When
        List<BookResponseDto> result = bookService.getAllBooks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("해리포터와 비밀의 방");
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("도서 ID로 조회 성공")
    void getBookById_Success() {
        // Given
        when(bookRepository.findById(1)).thenReturn(Optional.of(mockBook));

        // When
        BookResponseDto result = bookService.getBookById(1);

        // Then
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.title()).isEqualTo("해리포터와 비밀의 방");
        verify(bookRepository).findById(1);
    }

    @Test
    @DisplayName("존재하지 않는 도서 ID로 조회 실패")
    void getBookById_NotFound() {
        // Given
        when(bookRepository.findById(999)).thenReturn(Optional.empty());
        when(messageUtils.getMessageWithDefault(anyString(), anyString(), any())).thenReturn("Book not found. ID: 999");

        // When & Then
        assertThatThrownBy(() -> bookService.getBookById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
        
        verify(bookRepository).findById(999);
    }
} 