package com.oreo.insightfactory.dto;

import java.math.BigDecimal;
import java.util.List;

public record SalesSnapshot(
        long totalSales,
        long totalUnits,
        BigDecimal totalRevenue,
        List<GroupSnapshot> byBranch,
        List<GroupSnapshot> bySku
) {
    public static SalesSnapshot empty() {
        return new SalesSnapshot(0, 0, BigDecimal.ZERO, List.of(), List.of());
    }
}
