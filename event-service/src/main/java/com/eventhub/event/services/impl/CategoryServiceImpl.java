package com.eventhub.event.services.impl;

import com.eventhub.event.dto.request.CreateCategoryRequest;
import com.eventhub.event.dto.request.UpdateCategoryRequest;
import com.eventhub.event.dto.response.CategoryResponse;
import com.eventhub.event.entity.Category;
import com.eventhub.event.exception.BusinessRuleException;
import com.eventhub.event.exception.DuplicateResourceException;
import com.eventhub.event.exception.ResourceNotFoundException;
import com.eventhub.event.mapper.CategoryMapper;
import com.eventhub.event.repository.CategoryRepository;
import com.eventhub.event.repository.EventRepository;
import com.eventhub.event.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;


    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Category name already exists"
            );
        }

        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException(
                    "Category slug already exists"
            );
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }
    @Override
    public CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest updateCategoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (!category.getName().equals(updateCategoryRequest.getName())
                && categoryRepository.existsByName(updateCategoryRequest.getName()
        )) {
            throw new DuplicateResourceException("Category already exists");
        }
        if (!category.getSlug().equals(updateCategoryRequest.getSlug())
                && categoryRepository.existsBySlug(updateCategoryRequest.getSlug())) {
            throw new DuplicateResourceException("Category already exists");
        }
        categoryMapper.updateEntity(category, updateCategoryRequest);
       Category savedCategory=  categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID categoryId) {
        return categoryMapper.toResponse(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    public void deleteCategory(UUID categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );

        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new BusinessRuleException(
                    "Category cannot be deleted because it is used by existing events"
            );
        }

        categoryRepository.delete(category);
    }

}
