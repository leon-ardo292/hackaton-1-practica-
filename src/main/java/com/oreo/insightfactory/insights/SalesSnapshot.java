package com.oreo.insightfactory.insights;

import java.math.BigDecimal;
import java.util.List;

public record SalesSnapshot(
        long totalSales,
        long totalUnits,
        BigDecimal totalRevenue,
        List<GroupSnapshot> byBranch,
        List<GroupSnapshot> bySku
) {
    static SalesSnapshot empty() {
        return new SalesSnapshot(0, 0, BigDecimal.ZERO, List.of(), List.of());
    }
}
