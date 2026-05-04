package com.oreo.insightfactory.model;

import java.time.Instant;

public record ReportRequestedEvent(
        String requestId,
        Instant from,
        Instant to,
        String branch,
        String emailTo,
        String requestedBy
) {
}
