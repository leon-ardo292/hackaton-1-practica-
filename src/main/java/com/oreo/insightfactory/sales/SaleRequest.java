package com.oreo.insightfactory.sales;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleRequest(
        @NotBlank String branch,
        @NotBlank String sku,
        @NotNull @Min(1) Integer quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        @NotNull LocalDateTime soldAt
) {
}
