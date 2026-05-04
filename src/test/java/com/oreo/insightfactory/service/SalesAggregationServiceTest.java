package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.SalesAggregates;
import com.oreo.insightfactory.model.Sale;
import com.oreo.insightfactory.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        when(saleRepository.findAll()).thenReturn(List.of(
                sale("OREO_CLASSIC", 10, "1.99", "Miraflores", "2025-09-01T10:00:00Z"),
                sale("OREO_DOUBLE", 5, "2.49", "San Isidro", "2025-09-02T10:00:00Z"),
                sale("OREO_CLASSIC", 15, "1.99", "Miraflores", "2025-09-03T10:00:00Z")
        ));

        SalesAggregates result = salesAggregationService.calculateAggregates(
                Instant.parse("2025-09-01T00:00:00Z"),
                Instant.parse("2025-09-07T23:59:59Z"),
                null
        );

        assertThat(result.totalUnits()).isEqualTo(30);
        assertThat(result.totalRevenue()).isEqualByComparingTo("62.20");
        assertThat(result.topSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.topBranch()).isEqualTo("Miraflores");
    }

    @Test
    void shouldReturnEmptyAggregatesWhenThereAreNoSales() {
        when(saleRepository.findAll()).thenReturn(List.of());

        SalesAggregates result = salesAggregationService.calculateAggregates(
                Instant.parse("2025-09-01T00:00:00Z"),
                Instant.parse("2025-09-07T23:59:59Z"),
                null
        );

        assertThat(result.totalUnits()).isZero();
        assertThat(result.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.topSku()).isNull();
        assertThat(result.topBranch()).isNull();
    }

    @Test
    void shouldFilterByBranch() {
        when(saleRepository.findAll()).thenReturn(List.of(
                sale("OREO_CLASSIC", 10, "1.99", "Miraflores", "2025-09-01T10:00:00Z"),
                sale("OREO_DOUBLE", 40, "2.49", "San Isidro", "2025-09-02T10:00:00Z")
        ));

        SalesAggregates result = salesAggregationService.calculateAggregates(null, null, "Miraflores");

        assertThat(result.totalUnits()).isEqualTo(10);
        assertThat(result.totalRevenue()).isEqualByComparingTo("19.90");
        assertThat(result.topBranch()).isEqualTo("Miraflores");
    }

    @Test
    void shouldFilterByDateRange() {
        when(saleRepository.findAll()).thenReturn(List.of(
                sale("OREO_CLASSIC", 10, "1.99", "Miraflores", "2025-08-31T10:00:00Z"),
                sale("OREO_DOUBLE", 40, "2.49", "San Isidro", "2025-09-03T10:00:00Z"),
                sale("OREO_THINS", 50, "2.19", "Surco", "2025-09-10T10:00:00Z")
        ));

        SalesAggregates result = salesAggregationService.calculateAggregates(
                Instant.parse("2025-09-01T00:00:00Z"),
                Instant.parse("2025-09-07T23:59:59Z"),
                null
        );

        assertThat(result.totalUnits()).isEqualTo(40);
        assertThat(result.topSku()).isEqualTo("OREO_DOUBLE");
        assertThat(result.topBranch()).isEqualTo("San Isidro");
    }

    @Test
    void shouldChooseAlphabeticalSkuWhenTopSkuUnitsAreTied() {
        when(saleRepository.findAll()).thenReturn(List.of(
                sale("OREO_Z", 10, "1.99", "Miraflores", "2025-09-01T10:00:00Z"),
                sale("OREO_A", 10, "2.49", "San Isidro", "2025-09-02T10:00:00Z")
        ));

        SalesAggregates result = salesAggregationService.calculateAggregates(null, null, null);

        assertThat(result.topSku()).isEqualTo("OREO_A");
    }

    private Sale sale(String sku, int units, String price, String branch, String soldAt) {
        return new Sale(sku, units, new BigDecimal(price), branch, Instant.parse(soldAt), "tester");
    }
}
