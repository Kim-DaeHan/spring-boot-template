package com.example.libraryapi.rental.repository;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.rental.entity.Rental;
import com.example.libraryapi.rental.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    
    List<Rental> findByStatus(RentalStatus status);
    
    List<Rental> findByBook(Book book);
    
    @Query("SELECT r FROM Rental r WHERE r.book.id = :bookId AND r.status = 'BORROWED'")
    Optional<Rental> findActiveRentalByBookId(Long bookId);
    
    @Query("SELECT r FROM Rental r WHERE r.dueDate < :date AND r.status = 'BORROWED'")
    List<Rental> findOverdueRentals(LocalDate date);
} 