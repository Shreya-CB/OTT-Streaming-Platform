package com.ott.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Represents a subscription tier (e.g. Basic, Standard, Premium).
 * Managed exclusively by Administrator.
 */
@Entity
@Table(name = "subscription_plans")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int duration; // months

    @JsonIgnore
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    public SubscriptionPlan(String name, double price, int duration) {
        this.name     = name;
        this.price    = price;
        this.duration = duration;
    }
}
