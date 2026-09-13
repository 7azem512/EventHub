package com.eventhub.event.controller;

import com.eventhub.event.dto.request.CreateCategoryRequest;
import com.eventhub.event.dto.request.UpdateCategoryRequest;
import com.eventhub.event.dto.response.CategoryResponse;
import com.eventhub.event.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(
        name = "Categories",
        description = "Category management APIs"
)
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(
            summary = "Create category",
            description = "Creates a new event category"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category name or slug already exists")
    })
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieves all event categories"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully"
            )
    })
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    @GetMapping("/{categoryId}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a specific event category by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(
                categoryService.getCategoryById(categoryId)
        );
    }

    @PutMapping("/{categoryId}")
    @Operation(
            summary = "Update category",
            description = "Updates an existing event category"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category name or slug already exists"
            )
    })
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(
                categoryService.updateCategory(categoryId, request)
        );
    }

    @DeleteMapping("/{categoryId}")
    @Operation(
            summary = "Delete category",
            description = "Deletes an existing event category"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Category deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Category cannot be deleted because it is used by existing events"
            )
    })
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID categoryId
    ) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity
                .noContent()
                .build();
    }
}