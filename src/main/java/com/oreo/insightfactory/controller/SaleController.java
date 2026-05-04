package com.oreo.insightfactory.controller;

import com.oreo.insightfactory.dto.SaleRequest;
import com.oreo.insightfactory.dto.SaleResponse;
import com.oreo.insightfactory.dto.SalesMetricsResponse;
import com.oreo.insightfactory.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    SaleResponse create(@Valid @RequestBody SaleRequest request) {
        return saleService.create(request);
    }

    @GetMapping
    List<SaleResponse> findAll(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return saleService.findAll(branch, sku, from, to);
    }

    @GetMapping("/{id}")
    SaleResponse findById(@PathVariable Long id) {
        return saleService.findById(id);
    }

    @GetMapping("/metrics")
    SalesMetricsResponse metrics() {
        return saleService.metrics();
    }
}
