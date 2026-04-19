package com.ott.platform.controller;

import com.ott.platform.repository.*;
import com.ott.platform.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * MVC – Controller for Administrator use cases.
 * Use Cases: Generate analytics, Generate reports, Manage system configuration.
 *
 * Base URL: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AnalyticsService       analyticsService;
    private final SubscriptionRepository subRepo;
    private final PaymentRepository      paymentRepo;

    @Autowired
    public AdminController(AnalyticsService analyticsService,
                            SubscriptionRepository subRepo,
                            PaymentRepository paymentRepo) {
        this.analyticsService = analyticsService;
        this.subRepo          = subRepo;
        this.paymentRepo      = paymentRepo;
    }

    /** GET /api/admin/dashboard – Overall system statistics */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    /** GET /api/admin/revenue – Revenue report */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue() {
        return ResponseEntity.ok(
            analyticsService.getRevenueReport(subRepo, paymentRepo));
    }
}
