package com.example.libraryapi.rental.entity;

import com.example.libraryapi.book.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
    
    /**
     * 현재 대여가 연체 상태인지 확인합니다.
     * 반납 기한이 지났고, 아직 반납되지 않은 상태면 연체로 간주합니다.
     * 
     * @return 연체 여부
     */
    public boolean isOverdue() {
        // 반납 완료된 항목은 연체로 간주하지 않음
        if (status == RentalStatus.RETURNED) {
            return false;
        }
        
        // 반납 기한이 지났으면 연체
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * 연체 상태인 경우 상태를 OVERDUE로 업데이트합니다.
     */
    public void updateOverdueStatus() {
        if (isOverdue() && status != RentalStatus.OVERDUE) {
            this.status = RentalStatus.OVERDUE;
        }
    }
} 