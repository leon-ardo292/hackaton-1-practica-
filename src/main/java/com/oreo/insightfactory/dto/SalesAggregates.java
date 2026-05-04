package com.oreo.insightfactory.dto;

import java.math.BigDecimal;

public record SalesAggregates(
        long totalUnits,
        BigDecimal totalRevenue,
        String topSku,
        String topBranch
) {
}
