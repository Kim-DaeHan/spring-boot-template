package com.example.libraryapi.book.repository;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByAuthorContaining(String author);
    
    List<Book> findByTitleContaining(String title);
    
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.name = :categoryName")
    List<Book> findByCategoryName(String categoryName);
    
    List<Book> findByCategories(Category category);
} 