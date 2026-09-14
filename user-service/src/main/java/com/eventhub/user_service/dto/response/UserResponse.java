package com.eventhub.user_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String keycloakUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UUID avatarMediaId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
