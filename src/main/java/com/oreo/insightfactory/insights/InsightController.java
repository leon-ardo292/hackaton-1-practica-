package com.oreo.insightfactory.insights;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping("/summary")
    SummaryResponse summary() {
        return insightService.generateSummary(new SummaryRequest(false, null));
    }

    @PostMapping("/summary")
    SummaryResponse summary(@RequestBody(required = false) SummaryRequest request) {
        return insightService.generateSummary(request);
    }
}
