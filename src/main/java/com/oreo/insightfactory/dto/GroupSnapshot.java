package com.oreo.insightfactory.dto;

import java.math.BigDecimal;

public record GroupSnapshot(
        String name,
        long units,
        BigDecimal revenue
) {
}
