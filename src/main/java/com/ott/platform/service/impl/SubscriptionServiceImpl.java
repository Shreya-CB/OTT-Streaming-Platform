package com.ott.platform.service.impl;

import com.ott.platform.dto.SubscriptionRequestDTO;
import com.ott.platform.model.*;
import com.ott.platform.pattern.strategy.OnlinePaymentService;
import com.ott.platform.pattern.strategy.PaymentService;
import com.ott.platform.repository.*;
import com.ott.platform.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * Drives the Subscription Lifecycle State Machine:
 *   INACTIVE → PENDING_PAYMENT → ACTIVE → GRACE_PERIOD → SUSPENDED → CANCELLED/EXPIRED
 *
 * DIP  – depends on PaymentService interface (not OnlinePaymentService directly).
 * SRP  – subscription logic only; payment delegated to PaymentService.
 * GRASP – Creator: Viewer creates Subscription.
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository     subRepo;
    private final SubscriptionPlanRepository planRepo;
    private final ViewerRepository           viewerRepo;
    private final NotificationRepository     notifRepo;
    private final PaymentService             paymentService;  // Strategy / DIP

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subRepo,
                                    SubscriptionPlanRepository planRepo,
                                    ViewerRepository viewerRepo,
                                    NotificationRepository notifRepo,
                                    @Qualifier("onlinePayment") PaymentService paymentService) {
        this.subRepo         = subRepo;
        this.planRepo        = planRepo;
        this.viewerRepo      = viewerRepo;
        this.notifRepo       = notifRepo;
        this.paymentService  = paymentService;
    }

    /** Activity Diagram – Subscription & Access Control: full checkout flow. */
    @Override
    public Subscription subscribe(SubscriptionRequestDTO dto) {
        Viewer viewer = viewerRepo.findById(dto.getViewerId())
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + dto.getViewerId()));

        SubscriptionPlan plan = planRepo.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found: " + dto.getPlanId()));

        // INACTIVE → PENDING_PAYMENT
        Subscription sub = new Subscription(viewer, plan);
        sub.setStatus(SubscriptionStatus.PENDING_PAYMENT);
        sub = subRepo.save(sub);

        // Process payment via Strategy Pattern
        Payment payment = paymentService.processPayment(sub, plan.getPrice());

        if ("SUCCESS".equals(payment.getPaymentStatus())) {
            // PENDING_PAYMENT → ACTIVE
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setStartDate(LocalDate.now());
            sub.setEndDate(LocalDate.now().plusMonths(plan.getDuration()));
            sub = subRepo.save(sub);
            notifRepo.save(new Notification(
                "Subscription activated! Plan: " + plan.getName() +
                " | Valid until: " + sub.getEndDate(), viewer));
        } else {
            // PENDING_PAYMENT → INACTIVE (payment failed)
            sub.setStatus(SubscriptionStatus.INACTIVE);
            sub = subRepo.save(sub);
            throw new RuntimeException("Payment failed. Please try again.");
        }
        return sub;
    }

    /** Viewer cancels → ACTIVE/GRACE_PERIOD → CANCELLED */
    @Override
    public Subscription cancelSubscription(Long subscriptionId) {
        Subscription sub = subRepo.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub = subRepo.save(sub);
        notifRepo.save(new Notification("Your subscription has been cancelled.", sub.getViewer()));
        return sub;
    }

    /** Renewal: re-processes payment and extends endDate */
    @Override
    public Subscription renewSubscription(Long subscriptionId, String paymentMethod) {
        Subscription sub = subRepo.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        Payment payment = paymentService.processPayment(sub, sub.getPlan().getPrice());

        if ("SUCCESS".equals(payment.getPaymentStatus())) {
            sub.setStatus(SubscriptionStatus.ACTIVE);
            // Extend from current endDate (or today if already expired)
            LocalDate base = (sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDate.now()))
                    ? sub.getEndDate() : LocalDate.now();
            sub.setEndDate(base.plusMonths(sub.getPlan().getDuration()));
            sub = subRepo.save(sub);
            notifRepo.save(new Notification(
                "Subscription renewed until " + sub.getEndDate(), sub.getViewer()));
        } else {
            // Payment failed on renewal → GRACE_PERIOD
            sub.setStatus(SubscriptionStatus.GRACE_PERIOD);
            subRepo.save(sub);
            throw new RuntimeException("Renewal payment failed. Entered grace period.");
        }
        return sub;
    }

    @Override
    public Subscription getActiveSubscription(Long viewerId) {
        Viewer viewer = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        return subRepo.findTopByViewerAndStatusOrderByStartDateDesc(viewer, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active subscription found for viewer: " + viewerId));
    }

    @Override
    public List<Subscription> getSubscriptionHistory(Long viewerId) {
        Viewer viewer = viewerRepo.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("Viewer not found: " + viewerId));
        return subRepo.findByViewer(viewer);
    }

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        return planRepo.findAll();
    }

    @Override
    public SubscriptionPlan createPlan(String name, double price, int durationMonths) {
        return planRepo.save(new SubscriptionPlan(name, price, durationMonths));
    }

    @Override
    public void deletePlan(Long planId) {
        planRepo.deleteById(planId);
    }
}
