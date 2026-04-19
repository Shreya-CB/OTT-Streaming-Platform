package com.ott.platform.service;

import com.ott.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * GRASP – Pure Fabrication: analytics logic does not belong to any domain entity.
 * Provides data for the Analytics & Administrative Management Activity Diagram.
 */
@Service
public class AnalyticsService {

    private final UserRepository         userRepo;
    private final ContentRepository      contentRepo;
    private final SubscriptionRepository subRepo;
    private final WatchHistoryRepository watchHistoryRepo;
    private final RatingReviewRepository ratingRepo;

    @Autowired
    public AnalyticsService(UserRepository userRepo,
                             ContentRepository contentRepo,
                             SubscriptionRepository subRepo,
                             WatchHistoryRepository watchHistoryRepo,
                             RatingReviewRepository ratingRepo) {
        this.userRepo         = userRepo;
        this.contentRepo      = contentRepo;
        this.subRepo          = subRepo;
        this.watchHistoryRepo = watchHistoryRepo;
        this.ratingRepo       = ratingRepo;
    }

    /** Dashboard summary for Administrator */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers",          userRepo.count());
        stats.put("totalContent",         contentRepo.count());
        stats.put("totalSubscriptions",   subRepo.count());
        stats.put("totalStreamingSessions", watchHistoryRepo.count());
        stats.put("totalRatings",         ratingRepo.count());

        // Content breakdown by status
        Map<String, Long> contentByStatus = new LinkedHashMap<>();
        Arrays.stream(com.ott.platform.model.ContentStatus.values())
              .forEach(s -> contentByStatus.put(s.name(), (long) contentRepo.findByStatus(s).size()));
        stats.put("contentByStatus", contentByStatus);

        return stats;
    }

    /** Revenue report: sum of all successful payments */
    public Map<String, Object> getRevenueReport(SubscriptionRepository subscriptionRepo,
                                                  PaymentRepository paymentRepo) {
        Map<String, Object> report = new LinkedHashMap<>();
        double totalRevenue = paymentRepo.findAll().stream()
                .filter(p -> "SUCCESS".equals(p.getPaymentStatus()))
                .mapToDouble(p -> p.getAmount())
                .sum();
        report.put("totalRevenue",       totalRevenue);
        report.put("totalSubscriptions", subscriptionRepo.count());
        return report;
    }
}
