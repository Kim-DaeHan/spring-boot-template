package com.example.libraryapi.book.service;

import com.example.libraryapi.book.dto.BookCategoryUpdateDto;
import com.example.libraryapi.book.dto.BookRequestDto;
import com.example.libraryapi.book.dto.BookResponseDto;
import com.example.libraryapi.book.dto.BookStatusUpdateDto;
import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.mapper.BookMapper;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.category.repository.CategoryRepository;
import com.example.libraryapi.exception.ResourceInUseException;
import com.example.libraryapi.exception.ResourceNotFoundException;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import com.example.libraryapi.rental.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book mockBook;
    private Category mockCategory;
    private BookResponseDto mockBookResponse;
    private BookRequestDto mockBookRequest;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id(1)
                .name("소설")
                .books(new HashSet<>())
                .build();

        mockBook = Book.builder()
                .id(1)
                .title("해리포터와 비밀의 방")
                .author("J.K. 롤링")
                .status(BookStatus.AVAILABLE)
                .categories(new HashSet<>(Set.of(mockCategory)))
                .build();

        mockBookResponse = new BookResponseDto(
                1,
                "해리포터와 비밀의 방",
                "J.K. 롤링",
                BookStatus.AVAILABLE,
                null // mapper에서 처리됨
        );

        mockBookRequest = new BookRequestDto(
                "해리포터와 비밀의 방",
                "J.K. 롤링",
                Set.of(1)
        );
    }

    @Test
    @DisplayName("도서 등록 테스트")
    void createBook() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(mockCategory));
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(mockBookResponse);

        BookResponseDto result = bookService.createBook(mockBookRequest);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("해리포터와 비밀의 방");
        assertThat(result.author()).isEqualTo("J.K. 롤링");

        verify(categoryRepository, times(1)).findById(anyInt());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("전체 도서 목록 조회 테스트")
    void getAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(mockBook));
        when(bookMapper.toResponseList(anyList())).thenReturn(List.of(mockBookResponse));

        List<BookResponseDto> results = bookService.getAllBooks();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("해리포터와 비밀의 방");

        verify(bookRepository, times(1)).findAll();
        verify(bookMapper, times(1)).toResponseList(anyList());
    }

    @Test
    @DisplayName("ID로 도서 조회 테스트")
    void getBookById() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(mockBook));
        when(bookMapper.toResponse(any(Book.class))).thenReturn(mockBookResponse);

        BookResponseDto result = bookService.getBookById(1);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.title()).isEqualTo("해리포터와 비밀의 방");

        verify(bookRepository, times(1)).findById(anyInt());
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("존재하지 않는 도서 ID로 조회 시 예외 발생 테스트")
    void getBookByIdNotFound() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookRepository, times(1)).findById(anyInt());
        verify(bookMapper, never()).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("도서 검색 테스트")
    void searchBooks() {
        when(bookRepository.findByFilters(anyString(), anyString(), anyString()))
                .thenReturn(List.of(mockBook));
        when(bookMapper.toResponseList(anyList())).thenReturn(List.of(mockBookResponse));

        List<BookResponseDto> results = bookService.searchBooks("롤링", "해리포터", "소설");

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).author()).isEqualTo("J.K. 롤링");

        verify(bookRepository, times(1)).findByFilters(anyString(), anyString(), anyString());
        verify(bookMapper, times(1)).toResponseList(anyList());
    }

    @Test
    @DisplayName("도서 상태 변경 테스트")
    void updateBookStatus() {
        BookStatusUpdateDto statusUpdateDto = new BookStatusUpdateDto(BookStatus.UNAVAILABLE);
        Book updatedBook = mockBook;
        updatedBook.setStatus(BookStatus.UNAVAILABLE);

        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(mockBook));
        when(rentalRepository.findActiveRentalByBookId(anyInt())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(new BookResponseDto(
                1, "해리포터와 비밀의 방", "J.K. 롤링", BookStatus.UNAVAILABLE, null
        ));

        BookResponseDto result = bookService.updateBookStatus(1, statusUpdateDto);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BookStatus.UNAVAILABLE);

        verify(bookRepository, times(1)).findById(anyInt());
        verify(rentalRepository, times(1)).findActiveRentalByBookId(anyInt());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("대여 중인 도서 상태 변경 시 예외 발생 테스트")
    void updateBookStatusWithActiveRental() {
        BookStatusUpdateDto statusUpdateDto = new BookStatusUpdateDto(BookStatus.UNAVAILABLE);
        Rental activeRental = Rental.builder()
                .id(1)
                .book(mockBook)
                .status(RentalStatus.BORROWED)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(mockBook));
        when(rentalRepository.findActiveRentalByBookId(anyInt())).thenReturn(Optional.of(activeRental));

        assertThatThrownBy(() -> bookService.updateBookStatus(1, statusUpdateDto))
                .isInstanceOf(ResourceInUseException.class);

        verify(bookRepository, times(1)).findById(anyInt());
        verify(rentalRepository, times(1)).findActiveRentalByBookId(anyInt());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("도서 카테고리 수정 테스트")
    void updateBookCategories() {
        Category mockCategory2 = Category.builder()
                .id(2)
                .name("판타지")
                .books(new HashSet<>())
                .build();

        BookCategoryUpdateDto categoryUpdateDto = new BookCategoryUpdateDto(Set.of(1, 2));
        Book updatedBook = mockBook;
        updatedBook.setCategories(new HashSet<>(Set.of(mockCategory, mockCategory2)));

        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(mockBook));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.findById(2)).thenReturn(Optional.of(mockCategory2));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(mockBookResponse);

        BookResponseDto result = bookService.updateBookCategories(1, categoryUpdateDto);

        assertThat(result).isNotNull();

        verify(bookRepository, times(1)).findById(anyInt());
        verify(categoryRepository, times(2)).findById(anyInt());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }
} 