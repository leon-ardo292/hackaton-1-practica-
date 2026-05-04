package com.oreo.insightfactory.handlerexception;

import java.time.Instant;

public record ApiError(
        String error,
        String message,
        Instant timestamp,
        String path
) {
}
