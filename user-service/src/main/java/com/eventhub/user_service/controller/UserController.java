package com.eventhub.user_service.controller;


import com.eventhub.user_service.dto.request.UpdateUserRequest;
import com.eventhub.user_service.dto.response.UserResponse;
import com.eventhub.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "User profile management APIs"
)
public class UserController {

    private final UserService userService;



    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt)
    {
        String keycloakUserId = jwt.getSubject();
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String email = jwt.getClaimAsString("email");

        UserResponse response = userService.getOrCreateCurrentUser(keycloakUserId, firstName, lastName, email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserRequest request) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(userService.updateCurrentUser(keycloakId, request));
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves all user profiles with pagination and sorting"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @ParameterObject
            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                userService.getAllUsers(pageable)
        );
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user profile by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable UUID id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(id));
    }

    @Operation(
            summary = "Get user by Keycloak ID",
            description = "Retrieves a user profile by its Keycloak user identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/keycloak/{keycloakUserId}")
    public ResponseEntity<UserResponse> getUserByKeycloakId(
            @PathVariable String keycloakUserId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserByKeycloakId(keycloakUserId));
    }

    @Operation(
            summary = "Update user",
            description = "Updates an existing user profile"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse userResponse = userService.updateUser(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponse);
    }
}