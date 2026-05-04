package com.oreo.insightfactory.insights;

public record SummaryRequest(
        Boolean sendEmail,
        String recipient
) {
    boolean shouldSendEmail() {
        return Boolean.TRUE.equals(sendEmail);
    }
}
