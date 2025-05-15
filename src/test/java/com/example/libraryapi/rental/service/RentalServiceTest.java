package com.example.libraryapi.rental.service;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.exception.InvalidRequestException;
import com.example.libraryapi.exception.ResourceInUseException;
import com.example.libraryapi.exception.ResourceNotFoundException;
import com.example.libraryapi.rental.dto.RentalRequestDto;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import com.example.libraryapi.rental.mapper.RentalMapper;
import com.example.libraryapi.rental.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private RentalService rentalService;

    private Book mockBook;
    private Rental mockRental;
    private RentalResponseDto mockRentalResponse;
    private RentalRequestDto mockRentalRequest;

    @BeforeEach
    void setUp() {
        Category mockCategory = Category.builder()
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

        mockRental = Rental.builder()
                .id(1)
                .book(mockBook)
                .dueDate(LocalDate.now().plusDays(14))
                .status(RentalStatus.BORROWED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockRentalResponse = new RentalResponseDto(
                1,
                null, // mapper에서 처리됨
                LocalDate.now().plusDays(14),
                null,
                RentalStatus.BORROWED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        mockRentalRequest = new RentalRequestDto(
                1,
                LocalDate.now().plusDays(14)
        );
    }

    @Test
    @DisplayName("도서 대여 테스트")
    void borrowBook() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(mockBook));
        when(rentalRepository.findActiveRentalByBookId(anyInt())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);
        when(rentalRepository.save(any(Rental.class))).thenReturn(mockRental);
        when(rentalMapper.toResponse(any(Rental.class))).thenReturn(mockRentalResponse);

        RentalResponseDto result = rentalService.borrowBook(mockRentalRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.status()).isEqualTo(RentalStatus.BORROWED);

        verify(bookRepository, times(1)).findById(anyInt());
        verify(rentalRepository, times(1)).findActiveRentalByBookId(anyInt());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(rentalMapper, times(1)).toResponse(any(Rental.class));
    }

    @Test
    @DisplayName("대여할 수 없는 도서 상태일 때 예외 발생 테스트")
    void borrowBookUnavailableStatus() {
        Book unavailableBook = mockBook;
        unavailableBook.setStatus(BookStatus.UNAVAILABLE);

        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(unavailableBook));
        when(rentalRepository.findActiveRentalByBookId(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.borrowBook(mockRentalRequest))
                .isInstanceOf(InvalidRequestException.class);

        verify(bookRepository, times(1)).findById(anyInt());
        verify(rentalRepository, times(1)).findActiveRentalByBookId(anyInt());
        verify(bookRepository, never()).save(any(Book.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    @DisplayName("이미 대여 중인 도서 대여 시 예외 발생 테스트")
    void borrowBookAlreadyBorrowed() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(mockBook));
        when(rentalRepository.findActiveRentalByBookId(anyInt())).thenReturn(Optional.of(mockRental));

        assertThatThrownBy(() -> rentalService.borrowBook(mockRentalRequest))
                .isInstanceOf(ResourceInUseException.class);

        verify(bookRepository, times(1)).findById(anyInt());
        verify(rentalRepository, times(1)).findActiveRentalByBookId(anyInt());
        verify(bookRepository, never()).save(any(Book.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    @DisplayName("도서 반납 테스트")
    void returnBook() {
        // 테스트용 대여 객체를 새로 생성 (기존 mockRental을 그대로 사용하지 않음)
        Rental activeRental = Rental.builder()
                .id(1)
                .book(mockBook)
                .dueDate(LocalDate.now().plusDays(14))
                .status(RentalStatus.BORROWED) // 여전히 대여 중인 상태
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(rentalRepository.findById(anyInt())).thenReturn(Optional.of(activeRental));
        
        // 반납 후 상태로 변경된 객체
        Rental returnedRental = Rental.builder()
                .id(1)
                .book(mockBook)
                .dueDate(LocalDate.now().plusDays(14))
                .returnedDate(LocalDate.now())
                .status(RentalStatus.RETURNED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);
        when(rentalRepository.save(any(Rental.class))).thenReturn(returnedRental);
        
        RentalResponseDto returnedResponse = new RentalResponseDto(
                1,
                null,
                LocalDate.now().plusDays(14),
                LocalDate.now(),
                RentalStatus.RETURNED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        when(rentalMapper.toResponse(any(Rental.class))).thenReturn(returnedResponse);

        RentalResponseDto result = rentalService.returnBook(1);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(RentalStatus.RETURNED);
        assertThat(result.returnedDate()).isNotNull();

        verify(rentalRepository, times(1)).findById(anyInt());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(rentalMapper, times(1)).toResponse(any(Rental.class));
    }

    @Test
    @DisplayName("이미 반납된 도서 반납 시 예외 발생 테스트")
    void returnBookAlreadyReturned() {
        Rental returnedRental = Rental.builder()
                .id(1)
                .book(mockBook)
                .dueDate(LocalDate.now().plusDays(14))
                .returnedDate(LocalDate.now().minusDays(1))
                .status(RentalStatus.RETURNED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(rentalRepository.findById(anyInt())).thenReturn(Optional.of(returnedRental));

        assertThatThrownBy(() -> rentalService.returnBook(1))
                .isInstanceOf(InvalidRequestException.class);

        verify(rentalRepository, times(1)).findById(anyInt());
        verify(bookRepository, never()).save(any(Book.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    @DisplayName("전체 대여 목록 조회 테스트")
    void getAllRentals() {
        when(rentalRepository.findAll()).thenReturn(List.of(mockRental));
        when(rentalMapper.toResponseList(anyList())).thenReturn(List.of(mockRentalResponse));

        List<RentalResponseDto> results = rentalService.getAllRentals();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(1);

        verify(rentalRepository, times(1)).findAll();
        verify(rentalMapper, times(1)).toResponseList(anyList());
    }

    @Test
    @DisplayName("ID로 대여 조회 테스트")
    void getRentalById() {
        when(rentalRepository.findById(anyInt())).thenReturn(Optional.of(mockRental));
        when(rentalMapper.toResponse(any(Rental.class))).thenReturn(mockRentalResponse);

        RentalResponseDto result = rentalService.getRentalById(1);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);

        verify(rentalRepository, times(1)).findById(anyInt());
        verify(rentalMapper, times(1)).toResponse(any(Rental.class));
    }

    @Test
    @DisplayName("존재하지 않는 대여 ID로 조회 시 예외 발생 테스트")
    void getRentalByIdNotFound() {
        when(rentalRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.getRentalById(999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(rentalRepository, times(1)).findById(anyInt());
        verify(rentalMapper, never()).toResponse(any(Rental.class));
    }

    @Test
    @DisplayName("연체 목록 조회 테스트")
    void getOverdueRentals() {
        LocalDate now = LocalDate.now();
        Rental overdueRental = Rental.builder()
                .id(2)
                .book(mockBook)
                .dueDate(now.minusDays(1))
                .status(RentalStatus.BORROWED)
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .build();

        when(rentalRepository.findOverdueRentals(any(LocalDate.class)))
                .thenReturn(List.of(overdueRental));
        when(rentalRepository.saveAll(anyList())).thenReturn(List.of(overdueRental));
        
        RentalResponseDto overdueResponse = new RentalResponseDto(
                2,
                null,
                now.minusDays(1),
                null,
                RentalStatus.OVERDUE,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now()
        );
        
        when(rentalMapper.toResponseList(anyList())).thenReturn(List.of(overdueResponse));

        List<RentalResponseDto> results = rentalService.getOverdueRentals();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(RentalStatus.OVERDUE);

        verify(rentalRepository, times(1)).findOverdueRentals(any(LocalDate.class));
        verify(rentalRepository, times(1)).saveAll(anyList());
        verify(rentalMapper, times(1)).toResponseList(anyList());
    }
} 