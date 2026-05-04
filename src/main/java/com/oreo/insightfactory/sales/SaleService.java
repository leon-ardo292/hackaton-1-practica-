package com.oreo.insightfactory.sales;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Transactional
    public SaleResponse create(SaleRequest request) {
        Sale sale = new Sale(
                request.branch().trim(),
                request.sku().trim(),
                request.quantity(),
                request.unitPrice(),
                request.soldAt()
        );
        return SaleResponse.from(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> findAll(String branch, String sku, LocalDateTime from, LocalDateTime to) {
        List<Sale> sales;
        if (from != null && to != null) {
            sales = saleRepository.findBySoldAtBetweenOrderBySoldAtAsc(from, to);
        } else if (branch != null && !branch.isBlank()) {
            sales = saleRepository.findByBranchIgnoreCaseOrderBySoldAtAsc(branch);
        } else if (sku != null && !sku.isBlank()) {
            sales = saleRepository.findBySkuIgnoreCaseOrderBySoldAtAsc(sku);
        } else {
            sales = saleRepository.findAll();
        }

        return sales.stream().map(SaleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SaleResponse findById(Long id) {
        return saleRepository.findById(id)
                .map(SaleResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found: " + id));
    }

    @Transactional(readOnly = true)
    public SalesMetricsResponse metrics() {
        List<Sale> sales = saleRepository.findAll();
        long totalUnits = sales.stream().mapToLong(Sale::getQuantity).sum();
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageTicket = sales.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP);

        return new SalesMetricsResponse(
                sales.size(),
                totalUnits,
                totalRevenue,
                averageTicket,
                groupMetrics(sales, Sale::getBranch),
                groupMetrics(sales, Sale::getSku)
        );
    }

    private List<GroupMetric> groupMetrics(List<Sale> sales, Function<Sale, String> classifier) {
        Map<String, List<Sale>> grouped = sales.stream()
                .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet()
                .stream()
                .map(entry -> new GroupMetric(
                        entry.getKey(),
                        entry.getValue().stream().mapToLong(Sale::getQuantity).sum(),
                        entry.getValue().stream()
                                .map(Sale::getTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .sorted(Comparator.comparing(GroupMetric::totalRevenue).reversed())
                .toList();
    }
}
