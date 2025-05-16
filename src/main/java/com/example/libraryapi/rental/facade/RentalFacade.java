package com.example.libraryapi.rental.facade;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.exception.InvalidRequestException;
import com.example.libraryapi.exception.ResourceInUseException;
import com.example.libraryapi.exception.ResourceNotFoundException;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import com.example.libraryapi.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 대여 관련 데이터 액세스 계층과 비즈니스 로직 사이의 Facade
 */
@Component
@RequiredArgsConstructor
public class RentalFacade {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;

    /**
     * 도서 ID로 도서를 조회합니다.
     */
    public Book findBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다. ID: " + bookId));
    }

    /**
     * 도서 ID로 활성화된 대여 정보를 조회합니다.
     */
    public Optional<Rental> findActiveRentalByBookId(Integer bookId) {
        return rentalRepository.findActiveRentalByBookId(bookId);
    }

    /**
     * 도서의 상태를 변경합니다.
     */
    public Book updateBookStatus(Book book, BookStatus status) {
        book.setStatus(status);
        return bookRepository.save(book);
    }

    /**
     * 대여 정보를 저장합니다.
     */
    public Rental saveRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    /**
     * 대여 정보 ID로 대여 정보를 조회합니다.
     */
    public Rental findRentalById(Integer rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("대여 정보를 찾을 수 없습니다. ID: " + rentalId));
    }

    /**
     * 모든 대여 정보를 조회합니다.
     */
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    /**
     * 연체된 대여 정보를 조회합니다.
     */
    public List<Rental> findOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDate.now());
    }

    /**
     * 연체된 대여 정보 목록을 저장합니다.
     */
    public List<Rental> saveAllRentals(List<Rental> rentals) {
        return rentalRepository.saveAll(rentals);
    }

    /**
     * 대여 가능한 상태인지 검증합니다.
     */
    public void validateBookAvailable(Book book) {
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new InvalidRequestException("도서를 대여할 수 없습니다. 현재 상태: " + book.getStatus());
        }
    }

    /**
     * 이미 대여 중인지 검증합니다.
     */
    public void validateBookNotInUse(Integer bookId) {
        findActiveRentalByBookId(bookId)
            .ifPresent(rental -> {
                throw new ResourceInUseException("이미 대여중인 도서입니다. 현재 상태: " + rental.getStatus());
            });
    }

    /**
     * 이미 반납된 도서인지 검증합니다.
     */
    public void validateBookNotReturned(Rental rental) {
        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new InvalidRequestException("이미 반납된 도서입니다.");
        }
    }

    /**
     * 연체 상태 업데이트가 필요한 대여 목록을 필터링합니다.
     */
    public List<Rental> filterRentalsForOverdueUpdate(List<Rental> rentals) {
        return rentals.stream()
                .filter(rental -> rental.getStatus() != RentalStatus.OVERDUE)
                .collect(Collectors.toList());
    }
} 