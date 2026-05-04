package com.oreo.insightfactory.insights;

public record InsightEmailEvent(
        String recipient,
        String summary
) {
}
