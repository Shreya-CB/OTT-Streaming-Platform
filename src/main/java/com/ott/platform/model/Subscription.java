package com.ott.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

/**
 * Subscription entity.
 * Status lifecycle mirrors the Subscription Lifecycle State Diagram:
 *   INACTIVE → PENDING_PAYMENT → ACTIVE (Current/GracePeriod)
 *           → SUSPENDED → CANCELLED / EXPIRED
 *
 * GRASP – Creator: Viewer creates Subscription.
 */
@Entity
@Table(name = "subscriptions")
@Getter @Setter
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.INACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "viewer_id")
    private Viewer viewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @JsonIgnore
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    public Subscription(Viewer viewer, SubscriptionPlan plan) {
        this.viewer = viewer;
        this.plan   = plan;
        this.status = SubscriptionStatus.INACTIVE;
    }
}
