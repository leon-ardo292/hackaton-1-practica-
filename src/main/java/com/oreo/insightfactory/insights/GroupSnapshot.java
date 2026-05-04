package com.oreo.insightfactory.insights;

import java.math.BigDecimal;

public record GroupSnapshot(
        String name,
        long units,
        BigDecimal revenue
) {
}
