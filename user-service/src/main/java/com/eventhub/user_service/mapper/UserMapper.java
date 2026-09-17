package com.eventhub.user_service.mapper;


import com.eventhub.user_service.dto.request.UpdateUserRequest;
import com.eventhub.user_service.dto.response.UserResponse;
import com.eventhub.user_service.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public UserProfile updateEntity(UserProfile user, UpdateUserRequest request){
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        return user;
    }

    public UserResponse toResponse(UserProfile user){
        return UserResponse.builder()
                .id(user.getId())
                .keycloakUserId(user.getKeycloakUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarMediaId(user.getAvatarMediaId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
