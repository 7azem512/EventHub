package com.eventhub.event.mapper;

import com.eventhub.event.dto.request.CreateCategoryRequest;
import com.eventhub.event.dto.request.UpdateCategoryRequest;
import com.eventhub.event.dto.response.CategoryResponse;
import com.eventhub.event.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toEntity(CreateCategoryRequest createCategoryRequest){
        return Category.builder()
                .name(createCategoryRequest.getName())
                .slug(createCategoryRequest.getSlug())
                .description(createCategoryRequest.getDescription())
                .build();
    }
    public void updateEntity(Category category, UpdateCategoryRequest updateCategoryRequest){
        category.setName(updateCategoryRequest.getName());
        category.setSlug(updateCategoryRequest.getSlug());
        category.setDescription(updateCategoryRequest.getDescription());
    }
    public CategoryResponse toResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .build();
    }
}
