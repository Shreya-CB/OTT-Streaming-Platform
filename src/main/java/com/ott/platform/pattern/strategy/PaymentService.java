package com.ott.platform.pattern.strategy;

import com.ott.platform.model.*;

/**
 * ╔═══════════════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN 2b: STRATEGY (Behavioral) – Payment      ║
 * ║  Decouples payment processing from subscription logic.    ║
 * ║  DIP: SubscriptionService depends on this interface,      ║
 * ║       not on any concrete payment provider.              ║
 * ╚═══════════════════════════════════════════════════════════╝
 */
public interface PaymentService {
    /**
     * Processes a payment for the given subscription.
     * @return Payment record with SUCCESS or FAILED status
     */
    Payment processPayment(Subscription subscription, double amount);
}
