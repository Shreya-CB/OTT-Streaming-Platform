package com.ott.platform.model;

/**
 * States from the Subscription Lifecycle State Diagram.
 * Transitions: INACTIVE → PENDING_PAYMENT → ACTIVE (Current/GracePeriod)
 *              → SUSPENDED → CANCELLED / EXPIRED
 */
public enum SubscriptionStatus {
    INACTIVE,
    PENDING_PAYMENT,
    ACTIVE,
    GRACE_PERIOD,
    SUSPENDED,
    CANCELLED,
    EXPIRED
}
