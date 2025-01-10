package ru.practicum.category.mapper;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

public class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toCategory(NewCategoryDto dto) {
        Category c = new Category();
        c.setName(dto.getName());
        return c;
    }

    public static CategoryDto toCategoryDto(Category c) {
        return new CategoryDto(c.getId(), c.getName());
    }

    public static void updateEntityFromDto(CategoryDto dto, Category entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}
