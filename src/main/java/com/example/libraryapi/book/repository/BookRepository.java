package com.example.libraryapi.book.repository;

import com.example.libraryapi.book.entity.Book;
import com.example.libraryapi.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.categories WHERE :category MEMBER OF b.categories")
    List<Book> findByCategories(Category category);
    
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.categories c " +
           "WHERE (:categoryName IS NULL OR c.name = :categoryName) " +
           "AND (:title IS NULL OR b.title LIKE %:title%) " +
           "AND (:author IS NULL OR b.author LIKE %:author%)")
    List<Book> findByFilters(@Param("categoryName") String categoryName, 
                            @Param("title") String title, 
                            @Param("author") String author);
} 