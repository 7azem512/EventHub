package com.eventhub.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request used to create a new user profile")
public class CreateUserRequest {

    @Schema(
            description = "Unique user identifier from Keycloak",
            example = "8f14e45f-ea6b-4f8c-9c8a-7b2f67d5e1a2"
    )
    @NotBlank(message = "Keycloak user ID cannot be null or empty")
    @Size(max = 100, message = "Keycloak user ID must not exceed 100 characters")
    private String keycloakUserId;

    @Schema(
            description = "User first name",
            example = "Ayman"
    )
    @NotBlank(message = "First name cannot be null or empty")
    @Size(
            min = 2,
            max = 100,
            message = "First name must be between 2 and 100 characters long"
    )
    private String firstName;

    @Schema(
            description = "User last name",
            example = "Elkady"
    )
    @NotBlank(message = "Last name cannot be null or empty")
    @Size(
            min = 2,
            max = 100,
            message = "Last name must be between 2 and 100 characters long"
    )
    private String lastName;

    @Schema(
            description = "User email address",
            example = "ayman@example.com"
    )
    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must be at most 255 characters long")
    private String email;

    @Schema(
            description = "User phone number",
            example = "+201001234567"
    )
    @Size(max = 30, message = "Phone number must be at most 30 characters long")
    private String phone;
}