package com.eventhub.user_service.service;

import com.eventhub.user_service.dto.request.CreateUserRequest;
import com.eventhub.user_service.dto.request.UpdateUserRequest;
import com.eventhub.user_service.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(UUID userId, UpdateUserRequest request);
    UserResponse getUser(UUID userId);
    UserResponse getUserByKeycloakId(String keycloakUserId);
    Page<UserResponse> getAllUsers(Pageable pageable);

}
