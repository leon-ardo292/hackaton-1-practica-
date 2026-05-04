package com.oreo.insightfactory.dto;

import com.oreo.insightfactory.model.Sale;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleResponse(
        String id,
        String sku,
        Integer units,
        BigDecimal price,
        String branch,
        Instant soldAt,
        String createdBy
) {
    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getPublicId(),
                sale.getSku(),
                sale.getUnits(),
                sale.getPrice(),
                sale.getBranch(),
                sale.getSoldAt(),
                sale.getCreatedBy()
        );
    }
}
