package com.eventhub.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request used to create a new ticket type")
public class CreateTicketTypeRequest {

    @Schema(
            description = "Ticket type name",
            example = "VIP"
    )
    @NotBlank(message = "name must not be blank")
    @Size(
            max = 100,
            message = "name must not exceed 100 characters"
    )
    private String name;

    @Schema(
            description = "Ticket price",
            example = "1200.00"
    )
    @NotNull(message = "price must not be null")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "price must be greater than or equal to 0.00"
    )
    private BigDecimal price;

    @Schema(
            description = "Maximum number of tickets available for this type",
            example = "100"
    )
    @NotNull(message = "capacity must not be null")
    @Min(
            value = 1,
            message = "capacity must be greater than or equal to 1"
    )
    private Integer capacity;
}