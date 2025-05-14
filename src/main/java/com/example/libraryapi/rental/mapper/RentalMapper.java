package com.example.libraryapi.rental.mapper;

import com.example.libraryapi.book.mapper.BookMapper;
import com.example.libraryapi.rental.dto.RentalResponseDto;
import com.example.libraryapi.rental.entity.Rental;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RentalMapper {

    private final BookMapper bookMapper;

    public RentalResponseDto toResponse(Rental rental) {
        if (rental == null) {
            return null;
        }
        
        return new RentalResponseDto(
                rental.getId(),
                bookMapper.toResponse(rental.getBook()),
                rental.getDueDate(),
                rental.getReturnedDate(),
                rental.getStatus(),
                rental.getCreatedAt(),
                rental.getUpdatedAt()
        );
    }
    
    public List<RentalResponseDto> toResponseList(List<Rental> rentals) {
        return rentals.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
} 