package com.oreo.insightfactory.sales;

import java.math.BigDecimal;

public record GroupMetric(
        String name,
        long totalUnits,
        BigDecimal totalRevenue
) {
}
