package com.oreo.insightfactory.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponse(
        Long id,
        String branch,
        String sku,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        LocalDateTime soldAt
) {
    static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getBranch(),
                sale.getSku(),
                sale.getQuantity(),
                sale.getUnitPrice(),
                sale.getTotalAmount(),
                sale.getSoldAt()
        );
    }
}
