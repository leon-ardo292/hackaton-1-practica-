package com.oreo.insightfactory.repository;

import com.oreo.insightfactory.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findBySoldAtBetweenOrderBySoldAtAsc(Instant from, Instant to);

    List<Sale> findByBranchIgnoreCaseOrderBySoldAtAsc(String branch);

    List<Sale> findBySkuIgnoreCaseOrderBySoldAtAsc(String sku);

    List<Sale> findBySoldAtBetweenAndBranchIgnoreCaseOrderBySoldAtAsc(Instant from, Instant to, String branch);
}
