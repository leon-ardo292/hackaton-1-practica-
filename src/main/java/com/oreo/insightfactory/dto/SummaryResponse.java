package com.oreo.insightfactory.dto;

import java.time.Instant;

public record SummaryResponse(
        String summary,
        boolean emailQueued,
        Instant generatedAt,
        SalesSnapshot data
) {
}
