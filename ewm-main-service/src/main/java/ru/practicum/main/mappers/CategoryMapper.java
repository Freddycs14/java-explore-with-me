package ru.practicum.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.model.Category;

@UtilityClass
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }
}
