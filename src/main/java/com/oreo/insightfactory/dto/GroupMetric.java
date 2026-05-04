package com.oreo.insightfactory.dto;

import java.math.BigDecimal;

public record GroupMetric(
        String name,
        long totalUnits,
        BigDecimal totalRevenue
) {
}
