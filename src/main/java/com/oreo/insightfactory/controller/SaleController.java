package com.oreo.insightfactory.controller;

import com.oreo.insightfactory.dto.SaleRequest;
import com.oreo.insightfactory.dto.SaleResponse;
import com.oreo.insightfactory.dto.WeeklySummaryAcceptedResponse;
import com.oreo.insightfactory.dto.WeeklySummaryRequest;
import com.oreo.insightfactory.service.SaleService;
import com.oreo.insightfactory.service.WeeklySummaryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;
    private final WeeklySummaryService weeklySummaryService;

    public SaleController(SaleService saleService, WeeklySummaryService weeklySummaryService) {
        this.saleService = saleService;
        this.weeklySummaryService = weeklySummaryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    SaleResponse create(@Valid @RequestBody SaleRequest request) {
        return saleService.create(request);
    }

    @GetMapping
    Page<SaleResponse> findAll(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String branch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return saleService.findAll(from, to, branch, page, size);
    }

    @GetMapping("/{id}")
    SaleResponse findById(@PathVariable String id) {
        return saleService.findById(parseId(id, "s_"));
    }

    @PutMapping("/{id}")
    SaleResponse update(@PathVariable String id, @Valid @RequestBody SaleRequest request) {
        return saleService.update(parseId(id, "s_"), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('CENTRAL')")
    void delete(@PathVariable String id) {
        saleService.delete(parseId(id, "s_"));
    }

    @PostMapping("/summary/weekly")
    ResponseEntity<WeeklySummaryAcceptedResponse> requestWeeklySummary(@Valid @RequestBody WeeklySummaryRequest request) {
        return ResponseEntity.accepted().body(weeklySummaryService.requestSummary(request));
    }

    private Long parseId(String id, String prefix) {
        return Long.parseLong(id.startsWith(prefix) ? id.substring(prefix.length()) : id);
    }
}
