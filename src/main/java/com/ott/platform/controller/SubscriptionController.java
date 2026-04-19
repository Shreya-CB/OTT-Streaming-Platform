package com.ott.platform.controller;

import com.ott.platform.dto.SubscriptionRequestDTO;
import com.ott.platform.model.*;
import com.ott.platform.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * MVC – Controller for Subscription & Access Control use cases.
 * Use Cases: Buy subscription, Update subscription, Renew subscription,
 *            Manage subscription plans (Admin).
 *
 * Base URL: /api/subscriptions
 */
@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /** POST /api/subscriptions/subscribe – Viewer buys a plan */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscriptionRequestDTO dto) {
        try {
            Subscription sub = subscriptionService.subscribe(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(sub);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/subscriptions/{id}/cancel */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/subscriptions/{id}/renew */
    @PostMapping("/{id}/renew")
    public ResponseEntity<?> renew(@PathVariable Long id,
                                    @RequestParam(defaultValue = "ONLINE") String paymentMethod) {
        try {
            return ResponseEntity.ok(subscriptionService.renewSubscription(id, paymentMethod));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/subscriptions/viewer/{viewerId}/active */
    @GetMapping("/viewer/{viewerId}/active")
    public ResponseEntity<?> getActive(@PathVariable Long viewerId) {
        try {
            return ResponseEntity.ok(subscriptionService.getActiveSubscription(viewerId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/subscriptions/viewer/{viewerId}/history */
    @GetMapping("/viewer/{viewerId}/history")
    public ResponseEntity<List<Subscription>> getHistory(@PathVariable Long viewerId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionHistory(viewerId));
    }

    // ── Plan management (Admin) ───────────────────────────────────────────────

    /** GET /api/subscriptions/plans */
    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlan>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }

    /** POST /api/subscriptions/plans */
    @PostMapping("/plans")
    public ResponseEntity<?> createPlan(@RequestBody Map<String, Object> body) {
        try {
            String name     = (String)  body.get("name");
            double price    = Double.parseDouble(body.get("price").toString());
            int    duration = Integer.parseInt(body.get("duration").toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(subscriptionService.createPlan(name, price, duration));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/subscriptions/plans/{planId} */
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<?> deletePlan(@PathVariable Long planId) {
        subscriptionService.deletePlan(planId);
        return ResponseEntity.ok(Map.of("message", "Plan deleted"));
    }
}
