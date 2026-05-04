package com.oreo.insightfactory.dto;

import java.time.Instant;

public record WeeklySummaryAcceptedResponse(
        String requestId,
        String status,
        String message,
        String estimatedTime,
        Instant requestedAt
) {
}
