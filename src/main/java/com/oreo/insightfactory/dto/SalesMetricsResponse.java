package com.oreo.insightfactory.dto;

import java.math.BigDecimal;
import java.util.List;

public record SalesMetricsResponse(
        long totalSales,
        long totalUnits,
        BigDecimal totalRevenue,
        BigDecimal averageTicket,
        List<GroupMetric> byBranch,
        List<GroupMetric> bySku
) {
}
