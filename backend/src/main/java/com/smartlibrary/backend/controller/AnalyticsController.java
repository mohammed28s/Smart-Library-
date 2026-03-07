package com.smartlibrary.backend.controller;

import com.smartlibrary.backend.dto.analytics.InventoryMetricsDto;
import com.smartlibrary.backend.dto.analytics.RevenueMetricsDto;
import com.smartlibrary.backend.service.AnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Inventory and revenue metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('WORKER')")
    public InventoryMetricsDto inventory() {
        return analyticsService.getInventoryMetrics();
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('WORKER')")
    public RevenueMetricsDto revenue() {
        return analyticsService.getRevenueMetrics();
    }
}
