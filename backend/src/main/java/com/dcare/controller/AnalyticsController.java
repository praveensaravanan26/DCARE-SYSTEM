package com.dcare.controller;

import com.dcare.dto.AnalyticsSummaryDTO;
import com.dcare.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsSummaryDTO> getDashboardAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalyticsSummary());
    }
}
