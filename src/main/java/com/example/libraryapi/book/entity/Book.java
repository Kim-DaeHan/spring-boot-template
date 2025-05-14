package com.example.libraryapi.book.entity;

import com.example.libraryapi.category.entity.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BookStatus.AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 카테고리 추가 메서드
    public void addCategory(Category category) {
        this.categories.add(category);
        category.getBooks().add(this);
    }
    
    // 카테고리 제거 메서드
    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getBooks().remove(this);
    }
    
    // 카테고리 전체 교체 메서드
    public void updateCategories(Set<Category> newCategories) {
        // 기존 카테고리에서 책 정보 제거
        for (Category category : this.categories) {
            category.getBooks().remove(this);
        }
        
        // 새 카테고리로 설정
        this.categories.clear();
        if (newCategories != null) {
            for (Category category : newCategories) {
                addCategory(category);
            }
        }
    }
} 