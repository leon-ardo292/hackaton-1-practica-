package com.oreo.insightfactory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleRequest(
        @NotBlank String sku,
        @NotNull @Min(1) Integer units,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotBlank String branch,
        @NotNull Instant soldAt
) {
}
