package com.eventhub.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request used to create a new event category")
public class CreateCategoryRequest {

    @Schema(
            description = "Category name",
            example = "Technology"
    )
    @NotBlank(message = "Name is required")
    @Size(
            max = 100,
            message = "Name must not exceed 100 characters"
    )
    private String name;

    @Schema(
            description = "Category slug",
            example = "technology"
    )
    @NotBlank(message = "Slug is required")
    @Size(
            max = 120,
            message = "Slug must not exceed 120 characters"
    )
    private String slug;

    @Schema(
            description = "Optional category description",
            example = "Events related to technology and software development"
    )
    @Size(
            max = 500,
            message = "Description must not exceed 500 characters"
    )
    private String description;
}