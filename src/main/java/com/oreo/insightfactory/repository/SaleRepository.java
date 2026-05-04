package com.oreo.insightfactory.repository;

import com.oreo.insightfactory.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findBySoldAtBetweenOrderBySoldAtAsc(LocalDateTime from, LocalDateTime to);

    List<Sale> findByBranchIgnoreCaseOrderBySoldAtAsc(String branch);

    List<Sale> findBySkuIgnoreCaseOrderBySoldAtAsc(String sku);
}
