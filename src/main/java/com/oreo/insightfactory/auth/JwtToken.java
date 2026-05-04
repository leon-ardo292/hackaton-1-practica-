package com.oreo.insightfactory.auth;

import java.time.Instant;

public record JwtToken(String value, Instant expiresAt) {
}
