package com.oreo.insightfactory.dto;

import com.oreo.insightfactory.model.AppUser;
import com.oreo.insightfactory.model.UserRole;

import java.time.Instant;

public record UserResponse(
        String id,
        String username,
        String email,
        UserRole role,
        String branch,
        Instant createdAt
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getPublicId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBranch(),
                user.getCreatedAt()
        );
    }
}
