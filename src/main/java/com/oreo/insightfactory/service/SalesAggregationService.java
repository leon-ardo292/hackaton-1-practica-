package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.SalesAggregates;
import com.oreo.insightfactory.model.Sale;
import com.oreo.insightfactory.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesAggregationService {

    private final SaleRepository saleRepository;

    public SalesAggregationService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public SalesAggregates calculateAggregates(Instant from, Instant to, String branch) {
        List<Sale> sales = saleRepository.findAll()
                .stream()
                .filter(sale -> branch == null || branch.isBlank() || sale.getBranch().equalsIgnoreCase(branch))
                .filter(sale -> from == null || !sale.getSoldAt().isBefore(from))
                .filter(sale -> to == null || !sale.getSoldAt().isAfter(to))
                .toList();

        long totalUnits = sales.stream().mapToLong(Sale::getUnits).sum();
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SalesAggregates(
                totalUnits,
                totalRevenue,
                topByUnits(sales, Sale::getSku),
                topByUnits(sales, Sale::getBranch)
        );
    }

    private String topByUnits(List<Sale> sales, java.util.function.Function<Sale, String> classifier) {
        return sales.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.summingLong(Sale::getUnits)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()).thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
