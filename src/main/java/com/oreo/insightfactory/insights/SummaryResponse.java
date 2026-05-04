package com.oreo.insightfactory.insights;

import java.time.Instant;

public record SummaryResponse(
        String summary,
        boolean emailQueued,
        Instant generatedAt,
        SalesSnapshot data
) {
}
