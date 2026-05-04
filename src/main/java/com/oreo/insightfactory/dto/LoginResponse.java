package com.oreo.insightfactory.dto;

import com.oreo.insightfactory.model.UserRole;

public record LoginResponse(
        String token,
        long expiresIn,
        UserRole role,
        String branch
) {
}
