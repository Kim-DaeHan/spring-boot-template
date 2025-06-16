package com.example.libraryapi.rental.service;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.category.entity.Category;
import com.example.libraryapi.exception.InvalidRequestException;
import com.example.libraryapi.exception.ResourceInUseException;
import com.example.libraryapi.exception.ResourceNotFoundException;
import com.example.libraryapi.rental.dto.RentalRequestDto;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import com.example.libraryapi.rental.facade.RentalFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    private RentalFacade rentalFacade;

    @InjectMocks
    private RentalService rentalService;

    private Book mockBook;
    private Rental mockRental;
    private RentalRequestDto mockRentalRequest;
    private Category mockCategory;

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

        mockRental = new Rental();
        mockRental.setId(1);
        mockRental.setBook(mockBook);
        mockRental.setDueDate(LocalDate.now().plusDays(14));
        mockRental.setStatus(RentalStatus.BORROWED);
        mockRental.setCreatedAt(LocalDateTime.now());
        mockRental.setUpdatedAt(LocalDateTime.now());

        mockRentalRequest = new RentalRequestDto(1, LocalDate.now().plusDays(14));
    }

    @Test
    @DisplayName("도서 대여 성공")
    void borrowBook_Success() {
        // Given
        when(rentalFacade.findBookById(1)).thenReturn(mockBook);
        when(rentalFacade.saveRental(any(Rental.class))).thenReturn(mockRental);

        // When
        RentalResponseDto result = rentalService.borrowBook(mockRentalRequest);

        // Then
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.bookId()).isEqualTo(1);
        assertThat(result.bookTitle()).isEqualTo("해리포터와 비밀의 방");
        assertThat(result.status()).isEqualTo(RentalStatus.BORROWED);
        
        verify(rentalFacade).validateBookNotInUse(1);
        verify(rentalFacade).findBookById(1);
        verify(rentalFacade).validateBookAvailable(mockBook);
        verify(rentalFacade).updateBookStatus(mockBook, BookStatus.UNAVAILABLE);
        verify(rentalFacade).saveRental(any(Rental.class));
    }

    @Test
    @DisplayName("이미 대여 중인 도서 대여 시 예외 발생")
    void borrowBook_AlreadyBorrowed() {
        // Given
        doThrow(new ResourceInUseException("Book is already rented"))
                .when(rentalFacade).validateBookNotInUse(1);

        // When & Then
        assertThatThrownBy(() -> rentalService.borrowBook(mockRentalRequest))
                .isInstanceOf(ResourceInUseException.class);
        
        verify(rentalFacade).validateBookNotInUse(1);
        verify(rentalFacade, never()).saveRental(any(Rental.class));
    }

    @Test
    @DisplayName("대여할 수 없는 도서 상태일 때 예외 발생")
    void borrowBook_UnavailableBook() {
        // Given
        when(rentalFacade.findBookById(1)).thenReturn(mockBook);
        doThrow(new InvalidRequestException("Book cannot be rented"))
                .when(rentalFacade).validateBookAvailable(mockBook);

        // When & Then
        assertThatThrownBy(() -> rentalService.borrowBook(mockRentalRequest))
                .isInstanceOf(InvalidRequestException.class);
        
        verify(rentalFacade).validateBookNotInUse(1);
        verify(rentalFacade).findBookById(1);
        verify(rentalFacade).validateBookAvailable(mockBook);
        verify(rentalFacade, never()).saveRental(any(Rental.class));
    }

    @Test
    @DisplayName("도서 반납 성공")
    void returnBook_Success() {
        // Given
        mockRental.setStatus(RentalStatus.RETURNED);
        when(rentalFacade.findRentalById(1)).thenReturn(mockRental);
        when(rentalFacade.saveRental(any(Rental.class))).thenReturn(mockRental);

        // When
        RentalResponseDto result = rentalService.returnBook(1);

        // Then
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.status()).isEqualTo(RentalStatus.RETURNED);
        
        verify(rentalFacade).findRentalById(1);
        verify(rentalFacade).validateBookNotReturned(mockRental);
        verify(rentalFacade).updateBookStatus(mockBook, BookStatus.AVAILABLE);
        verify(rentalFacade).saveRental(mockRental);
    }

    @Test
    @DisplayName("이미 반납된 도서 반납 시 예외 발생")
    void returnBook_AlreadyReturned() {
        // Given
        when(rentalFacade.findRentalById(1)).thenReturn(mockRental);
        doThrow(new InvalidRequestException("Book is already returned"))
                .when(rentalFacade).validateBookNotReturned(mockRental);

        // When & Then
        assertThatThrownBy(() -> rentalService.returnBook(1))
                .isInstanceOf(InvalidRequestException.class);
        
        verify(rentalFacade).findRentalById(1);
        verify(rentalFacade).validateBookNotReturned(mockRental);
        verify(rentalFacade, never()).saveRental(any(Rental.class));
    }

    @Test
    @DisplayName("모든 대여 정보 조회 성공")
    void getAllRentals_Success() {
        // Given
        when(rentalFacade.findAllRentals()).thenReturn(List.of(mockRental));

        // When
        List<RentalResponseDto> result = rentalService.getAllRentals();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1);
        verify(rentalFacade).findAllRentals();
    }

    @Test
    @DisplayName("대여 정보 ID로 조회 성공")
    void getRentalById_Success() {
        // Given
        when(rentalFacade.findRentalById(1)).thenReturn(mockRental);

        // When
        RentalResponseDto result = rentalService.getRentalById(1);

        // Then
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.bookTitle()).isEqualTo("해리포터와 비밀의 방");
        verify(rentalFacade).findRentalById(1);
    }

    @Test
    @DisplayName("존재하지 않는 대여 ID로 조회 시 예외 발생")
    void getRentalById_NotFound() {
        // Given
        when(rentalFacade.findRentalById(999)).thenThrow(new ResourceNotFoundException("Rental not found"));

        // When & Then
        assertThatThrownBy(() -> rentalService.getRentalById(999))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(rentalFacade).findRentalById(999);
    }

    @Test
    @DisplayName("연체된 대여 목록 조회 성공")
    void getOverdueRentals_Success() {
        // Given
        Rental overdueRental = new Rental();
        overdueRental.setId(2);
        overdueRental.setBook(mockBook);
        overdueRental.setDueDate(LocalDate.now().minusDays(1));
        overdueRental.setStatus(RentalStatus.BORROWED);
        
        when(rentalFacade.findOverdueRentals()).thenReturn(List.of(overdueRental));
        when(rentalFacade.filterRentalsForOverdueUpdate(any())).thenReturn(List.of(overdueRental));
        when(rentalFacade.saveAllRentals(any())).thenReturn(List.of(overdueRental));

        // When
        List<RentalResponseDto> result = rentalService.getOverdueRentals();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).dueDate()).isBefore(LocalDate.now());
        verify(rentalFacade).findOverdueRentals();
        verify(rentalFacade).filterRentalsForOverdueUpdate(any());
        verify(rentalFacade).saveAllRentals(any());
    }
} 