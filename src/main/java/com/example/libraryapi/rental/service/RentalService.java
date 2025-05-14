package com.example.libraryapi.rental.service;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.exception.ResourceNotFoundException;
import com.example.libraryapi.rental.dto.RentalRequestDto;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import com.example.libraryapi.rental.mapper.RentalMapper;
import com.example.libraryapi.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final RentalMapper rentalMapper;

    @Transactional
    public RentalResponseDto borrowBook(RentalRequestDto request) {
        // 도서 조회
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다. ID: " + request.bookId()));
        
        // 도서 상태 확인
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalArgumentException("도서를 대여할 수 없습니다. 현재 상태: " + book.getStatus());
        }
        
        // 이미 대여중인지 확인
        rentalRepository.findActiveRentalByBookId(request.bookId())
                .ifPresent(rental -> {
                    throw new IllegalArgumentException("이미 대여중인 도서입니다.");
                });
        
        // 대여 기간 확인
        if (request.dueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("반납 예정일은 현재 날짜 이후로 설정해야 합니다.");
        }
        
        // 도서 상태 변경
        book.setStatus(BookStatus.UNAVAILABLE);
        bookRepository.save(book);
        
        // 대여 정보 생성
        Rental rental = Rental.builder()
                .book(book)
                .dueDate(request.dueDate())
                .status(RentalStatus.BORROWED)
                .build();
        
        return rentalMapper.toResponse(rentalRepository.save(rental));
    }

    @Transactional
    public RentalResponseDto returnBook(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("대여 정보를 찾을 수 없습니다. ID: " + rentalId));
        
        // 이미 반납된 도서인지 확인
        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new IllegalArgumentException("이미 반납된 도서입니다.");
        }
        
        // 도서 반납 처리
        rental.returnBook();
        
        // 도서 상태 변경
        Book book = rental.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);
        
        return rentalMapper.toResponse(rentalRepository.save(rental));
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDto> getAllRentals() {
        return rentalMapper.toResponseList(rentalRepository.findAll());
    }

    @Transactional(readOnly = true)
    public RentalResponseDto getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("대여 정보를 찾을 수 없습니다. ID: " + id));
        return rentalMapper.toResponse(rental);
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDto> getOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDate.now());
        
        // 연체 상태 업데이트
        overdueRentals.forEach(Rental::updateOverdueStatus);
        rentalRepository.saveAll(overdueRentals);
        
        return rentalMapper.toResponseList(overdueRentals);
    }
} 