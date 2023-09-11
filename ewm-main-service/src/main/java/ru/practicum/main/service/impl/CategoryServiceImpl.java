package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mappers.CategoryMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.CategoryService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        boolean isExists = checkCategoryName(categoryDto);
        if (isExists) {
            throw new ConflictException("Имя категории уже занято");
        }
        Category category = CategoryMapper.toCategory(categoryDto);
        Category createCategory = categoryRepository.save(category);
        log.info("Создание новой категории - {}", createCategory);
        return CategoryMapper.toCategoryDto(createCategory);
    }

    private boolean checkCategoryName(CategoryDto categoryDto) {
        Category category = categoryRepository.findCategoryByName(categoryDto.getName());
        if (category == null) {
            return false;
        }
        if (categoryDto.getId() != null) {
            throw new ConflictException("Имя категории уже занято");
        }
        return true;
    }

    @Override
    @Transactional
    public void removeCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория не найдена"));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Нельзя удалить категорию, в которой есть события");
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Категория не найдена или недоступна"));
        Category catName = categoryRepository.findCategoryByName(categoryDto.getName());
        if (catName != null && !Objects.equals(catName.getId(), categoryId)) {
            throw new ConflictException("Категория не является пустой");
        }
        if (catName != null && Objects.equals(catName.getId(), categoryId)) {
            categoryDto.setId(categoryId);
            return categoryDto;
        }
        if (category.getId().equals(categoryId)) {
            categoryDto.setId(categoryId);
            return categoryDto;
        }
        category = categoryRepository.save(category);
        category.setName(categoryDto.getName());
        log.info("Изменение категории - {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").ascending());
        List<Category> categories = categoryRepository.findAllCategories(pageable);
        log.info("Получение списка категорий");
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Категория не найдена или недоступна"));
        log.info("Получение категории по id - {}", categoryId);
        return CategoryMapper.toCategoryDto(category);
    }
}
