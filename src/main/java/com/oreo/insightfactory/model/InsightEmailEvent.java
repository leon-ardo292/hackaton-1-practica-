package com.oreo.insightfactory.model;

public record InsightEmailEvent(
        String recipient,
        String summary
) {
}
