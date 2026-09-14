package com.eventhub.user_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @NotNull(message = "Keycloak user ID cannot be null")
    @Size(
            max = 100,
            message = "Keycloak user ID must not exceed 100 characters"
    )
    @Column(
            name = "keycloak_user_id",
            nullable = false,
            unique = true,
            length = 100
    )
    private String keycloakUserId;

    @NotNull(message = "First name cannot be null")
    @Size(
            min = 2,
            max = 100,
            message = "First name must be between 2 and 100 characters"
    )
    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(
            min = 2,
            max = 100,
            message = "Last name must be between 2 and 100 characters"
    )
    @Column(
            name = "last_name",
            nullable = false,
            length = 100
    )
    private String lastName;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be a valid email address")
    @Size(
            max = 255,
            message = "Email must not exceed 255 characters"
    )
    @Column(
            name = "user_email",
            nullable = false,
            unique = true,
            length = 255
    )
    private String email;

    @Size(
            max = 30,
            message = "Phone number must not exceed 30 characters"
    )
    @Column(
            name = "phone_number",
            length = 30
    )
    private String phone;

    @Column(name = "avatar_media_id")
    private UUID avatarMediaId;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}