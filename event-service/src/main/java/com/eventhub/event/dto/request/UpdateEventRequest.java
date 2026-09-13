package com.eventhub.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request used to update an existing event")
public class UpdateEventRequest {

    @Schema(
            description = "Event title",
            example = "Java Spring Conference 2026"
    )
    @NotBlank(message = "title must not be blank")
    @Size(
            max = 200,
            message = "title must not exceed 200 characters"
    )
    private String title;

    @Schema(
            description = "Event description",
            example = "A conference focused on Java, Spring Boot and Microservices"
    )
    @Size(
            max = 3000,
            message = "description must not exceed 3000 characters"
    )
    private String description;

    @Schema(
            description = "Event location",
            example = "Cairo International Convention Center"
    )
    @Size(
            max = 255,
            message = "location must not exceed 255 characters"
    )
    private String location;

    @Schema(
            description = "Event starting date and time",
            example = "2026-12-20T10:00:00"
    )
    @NotNull(message = "start date must not be null")
    @Future(message = "start date must be in the future")
    private LocalDateTime startDate;

    @Schema(
            description = "Event ending date and time",
            example = "2026-12-20T18:00:00"
    )
    @NotNull(message = "end date must not be null")
    @Future(message = "end date must be in the future")
    private LocalDateTime endDate;

    @Schema(
            description = "Booking starting date and time",
            example = "2026-11-01T10:00:00"
    )
    @NotNull(message = "booking start date must not be null")
    private LocalDateTime bookingStartDate;

    @Schema(
            description = "Booking ending date and time",
            example = "2026-12-20T10:00:00"
    )
    @NotNull(message = "booking end date must not be null")
    private LocalDateTime bookingEndDate;

    @Schema(
            description = "Category identifier",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull(message = "category id must not be null")
    private UUID categoryId;
}