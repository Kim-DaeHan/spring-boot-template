package com.example.libraryapi;

import com.example.libraryapi.book.controller.BookControllerTest;
import com.example.libraryapi.book.service.BookServiceTest;
import com.example.libraryapi.category.controller.CategoryControllerTest;
import com.example.libraryapi.category.service.CategoryServiceTest;
import com.example.libraryapi.exception.BusinessExceptionTest;
import com.example.libraryapi.exception.ErrorResponseTest;
import com.example.libraryapi.exception.GlobalExceptionHandlerTest;
import com.example.libraryapi.rental.controller.RentalControllerTest;
import com.example.libraryapi.rental.service.RentalServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("도서관 API 통합 테스트 스위트")
@SelectClasses({
        // Book Module Tests
        BookControllerTest.class,
        BookServiceTest.class,
        
        // Category Module Tests
        CategoryControllerTest.class,
        CategoryServiceTest.class,
        
        // Rental Module Tests
        RentalControllerTest.class,
        RentalServiceTest.class,
        
        // Exception Module Tests
        GlobalExceptionHandlerTest.class,
        BusinessExceptionTest.class,
        ErrorResponseTest.class
})
public class LibraryApiTestSuite {
    // 이 클래스는 JUnit 5의 테스트 스위트를 정의하는 용도로만 사용됩니다.
    // 실제 테스트 메서드는 포함하지 않습니다.
} 