package com.oreo.insightfactory.model;

import java.time.Instant;

public record JwtToken(String value, Instant expiresAt) {
}
