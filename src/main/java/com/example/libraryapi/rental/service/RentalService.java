package com.example.libraryapi.rental.service;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.book.entity.BookStatus;
import com.example.libraryapi.book.repository.BookRepository;
import com.example.libraryapi.exception.InvalidRequestException;
import com.example.libraryapi.exception.ResourceInUseException;
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
import java.util.stream.Collectors;

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
        
        // 이미 대여중인지 확인 (BORROWED와 OVERDUE 상태 모두 확인)
        rentalRepository.findActiveRentalByBookId(request.bookId())
        .ifPresent(rental -> {
            throw new ResourceInUseException("이미 대여중인 도서입니다. 현재 상태: " + rental.getStatus());
        });

        // 도서 상태 확인
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new InvalidRequestException("도서를 대여할 수 없습니다. 현재 상태: " + book.getStatus());
        }
        
        // 대여 기간 확인
        // if (request.dueDate().isBefore(LocalDate.now())) {
        //     throw new InvalidRequestException("반납 예정일은 현재 날짜 이후로 설정해야 합니다.");
        // }
        
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
    public RentalResponseDto returnBook(Integer rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("대여 정보를 찾을 수 없습니다. ID: " + rentalId));
        
        // 이미 반납된 도서인지 확인
        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new InvalidRequestException("이미 반납된 도서입니다.");
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
    public RentalResponseDto getRentalById(Integer id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("대여 정보를 찾을 수 없습니다. ID: " + id));
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public List<RentalResponseDto> getOverdueRentals() {
        LocalDate now = LocalDate.now();
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(now);
        
        // 아직 OVERDUE 상태가 아닌 항목만 필터링하여 업데이트
        List<Rental> rentalsToUpdate = overdueRentals.stream()
                .filter(rental -> rental.getStatus() != RentalStatus.OVERDUE)
                .collect(Collectors.toList());
        
        // 연체 상태 업데이트
        if (!rentalsToUpdate.isEmpty()) {
            rentalsToUpdate.forEach(Rental::updateOverdueStatus);
            rentalRepository.saveAll(rentalsToUpdate);
        }
        
        // OVERDUE 상태인 항목을 포함하여 모든 연체 항목 조회
        return rentalMapper.toResponseList(overdueRentals);
    }
} 