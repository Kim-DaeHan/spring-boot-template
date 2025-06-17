package com.example.libraryapi.category.dto;

import com.example.libraryapi.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "카테고리 응답 DTO")
public record CategoryResponseDto(
		@Schema(description = "카테고리 ID", example = "1") Integer id,

		@Schema(description = "카테고리 이름", example = "문학") String name) {

	/**
	 * Category 엔티티로부터 CategoryResponseDto를 생성합니다.
	 */
	public static CategoryResponseDto from(Category category) {
		if (category == null) {
			return null;
		}

		return new CategoryResponseDto(
				category.getId(),
				category.getName());
	}

	/**
	 * Category 엔티티 리스트로부터 CategoryResponseDto 리스트를 생성합니다.
	 */
	public static List<CategoryResponseDto> listFrom(List<Category> categories) {
		if (categories == null) {
			return List.of();
		}

		return categories.stream()
				.map(CategoryResponseDto::from)
				.collect(Collectors.toList());
	}

	/**
	 * Category 엔티티 집합으로부터 CategoryResponseDto 집합을 생성합니다.
	 */
	public static Set<CategoryResponseDto> setFrom(Set<Category> categories) {
		if (categories == null) {
			return Set.of();
		}

		return categories.stream()
				.map(CategoryResponseDto::from)
				.collect(Collectors.toSet());
	}
}
