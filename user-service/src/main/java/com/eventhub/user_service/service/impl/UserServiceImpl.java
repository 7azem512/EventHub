package com.eventhub.user_service.service.impl;

import com.eventhub.user_service.dto.request.UpdateUserRequest;
import com.eventhub.user_service.dto.response.UserResponse;
import com.eventhub.user_service.entity.UserProfile;
import com.eventhub.user_service.exception.DuplicateResourceException;
import com.eventhub.user_service.exception.ResourceNotFoundException;
import com.eventhub.user_service.mapper.UserMapper;
import com.eventhub.user_service.repository.UserProfileRepository;
import com.eventhub.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserProfileRepository userRepository;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public UserResponse getOrCreateCurrentUser(
            String keycloakUserId,
            String firstName,
            String lastName,
            String email
    ) {

        return userRepository
                .findByKeycloakUserId(keycloakUserId)
                .map(userMapper::toResponse)
                .orElseGet(() -> {

                    if (userRepository.existsByEmailIgnoreCase(email)) {
                        throw new DuplicateResourceException(
                                "A user profile already exists with email: " + email
                        );
                    }

                    UserProfile user = UserProfile.builder()
                            .keycloakUserId(keycloakUserId)
                            .firstName(firstName)
                            .lastName(lastName)
                            .email(email)
                            .build();

                    UserProfile savedUser =
                            userRepository.save(user);

                    return userMapper.toResponse(savedUser);
                });
    }

    @Override
    public UserResponse updateCurrentUser(String keycloakUserId, UpdateUserRequest request) {
        UserProfile user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        if(!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmailIgnoreCase(request.getEmail()))
            throw new DuplicateResourceException("User with email " + request.getEmail() + " already exists");

        userMapper.updateEntity(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }



    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmailIgnoreCase(request.getEmail()))
            throw new DuplicateResourceException("User with email " + request.getEmail() + " already exists");

        userMapper.updateEntity(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        return userMapper.toResponse(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByKeycloakId(String keycloakUserId) {
        return userMapper.toResponse(userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }
}
