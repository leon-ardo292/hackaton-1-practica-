package com.oreo.insightfactory.service;

import com.oreo.insightfactory.model.Sale;
import com.oreo.insightfactory.repository.SaleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class SalesDemoData {

    @Bean
    CommandLineRunner seedSales(SaleRepository saleRepository) {
        return args -> {
            if (saleRepository.count() > 0) {
                return;
            }

            saleRepository.save(new Sale("Miraflores", "OREO-REGULAR", 20, new BigDecimal("2.50"), LocalDateTime.now().minusDays(2)));
            saleRepository.save(new Sale("San Isidro", "OREO-DOUBLE", 15, new BigDecimal("3.20"), LocalDateTime.now().minusDays(1)));
            saleRepository.save(new Sale("Miraflores", "OREO-MINI", 32, new BigDecimal("1.80"), LocalDateTime.now().minusHours(8)));
        };
    }
}
