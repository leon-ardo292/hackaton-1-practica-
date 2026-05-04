package com.oreo.insightfactory.dto;

public record SummaryRequest(
        Boolean sendEmail,
        String recipient
) {
    public boolean shouldSendEmail() {
        return Boolean.TRUE.equals(sendEmail);
    }
}
