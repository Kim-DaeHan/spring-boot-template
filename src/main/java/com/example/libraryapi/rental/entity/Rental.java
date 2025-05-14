package com.example.libraryapi.rental.entity;

import com.example.libraryapi.book.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = RentalStatus.BORROWED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void returnBook() {
        this.returnedDate = LocalDate.now();
        this.status = RentalStatus.RETURNED;
    }
    
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && status == RentalStatus.BORROWED;
    }
    
    public void updateOverdueStatus() {
        if (isOverdue()) {
            this.status = RentalStatus.OVERDUE;
        }
    }
} 