package com.eventhub.event.services;

import com.eventhub.event.dto.request.CreateCategoryRequest;
import com.eventhub.event.dto.request.UpdateCategoryRequest;
import com.eventhub.event.dto.response.CategoryResponse;


import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);
    CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest updateCategoryRequest);
    CategoryResponse getCategoryById(UUID categoryId);
    List<CategoryResponse> getAllCategories();
    void deleteCategory(UUID categoryId);



}
