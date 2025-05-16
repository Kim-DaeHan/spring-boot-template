package com.example.libraryapi.rental.service;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.rental.dto.RentalRequestDto;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import com.example.libraryapi.rental.facade.RentalFacade;
import com.example.libraryapi.rental.mapper.RentalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 대여 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalFacade rentalFacade;
    private final RentalMapper rentalMapper;

    /**
     * 도서 대여 처리
     */
    @Transactional
    public RentalResponseDto borrowBook(RentalRequestDto request) {
        // 데이터 검증
        rentalFacade.validateBookNotInUse(request.bookId());
        Book book = rentalFacade.findBookById(request.bookId());
        rentalFacade.validateBookAvailable(book);
        
        // 도서 상태 변경
        rentalFacade.updateBookStatus(book, BookStatus.UNAVAILABLE);
        
        // 대여 정보 생성 및 저장
        Rental rental = createRental(book, request.dueDate());
        Rental savedRental = rentalFacade.saveRental(rental);
        
        return rentalMapper.toResponse(savedRental);
    }

    /**
     * 대여 정보 생성
     */
    private Rental createRental(Book book, LocalDate dueDate) {
        return Rental.builder()
                .book(book)
                .dueDate(dueDate)
                .status(RentalStatus.BORROWED)
                .build();
    }

    /**
     * 도서 반납 처리
     */
    @Transactional
    public RentalResponseDto returnBook(Integer rentalId) {
        // 대여 정보 조회 및 검증
        Rental rental = rentalFacade.findRentalById(rentalId);
        rentalFacade.validateBookNotReturned(rental);
        
        // 도서 반납 처리
        rental.returnBook();
        
        // 도서 상태 변경
        Book book = rental.getBook();
        rentalFacade.updateBookStatus(book, BookStatus.AVAILABLE);
        
        return rentalMapper.toResponse(rentalFacade.saveRental(rental));
    }

    /**
     * 모든 대여 정보 조회
     */
    @Transactional(readOnly = true)
    public List<RentalResponseDto> getAllRentals() {
        List<Rental> rentals = rentalFacade.findAllRentals();
        return rentalMapper.toResponseList(rentals);
    }

    /**
     * 대여 정보 ID로 대여 정보 조회
     */
    @Transactional(readOnly = true)
    public RentalResponseDto getRentalById(Integer id) {
        Rental rental = rentalFacade.findRentalById(id);
        return rentalMapper.toResponse(rental);
    }

    /**
     * 연체된 대여 정보 조회 및 상태 업데이트
     */
    @Transactional
    public List<RentalResponseDto> getOverdueRentals() {
        // 연체된 대여 정보 조회
        List<Rental> overdueRentals = rentalFacade.findOverdueRentals();
        
        // 업데이트가 필요한 대여 정보 필터링
        List<Rental> rentalsToUpdate = rentalFacade.filterRentalsForOverdueUpdate(overdueRentals);
        
        // 연체 상태 업데이트
        if (!rentalsToUpdate.isEmpty()) {
            updateOverdueStatus(rentalsToUpdate);
        }
        
        return rentalMapper.toResponseList(overdueRentals);
    }
    
    /**
     * 연체 상태 업데이트
     */
    private void updateOverdueStatus(List<Rental> rentals) {
        rentals.forEach(Rental::updateOverdueStatus);
        rentalFacade.saveAllRentals(rentals);
    }
} 