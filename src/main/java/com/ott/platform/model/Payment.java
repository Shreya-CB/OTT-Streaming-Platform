package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

/**
 * Records each payment attempt against a Subscription.
 * PaymentStatus: SUCCESS | FAILED | PENDING
 */
@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String paymentStatus;   // SUCCESS | FAILED | PENDING

    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public Payment(double amount, String paymentStatus, Subscription subscription) {
        this.amount         = amount;
        this.paymentStatus  = paymentStatus;
        this.subscription   = subscription;
        this.paymentDate    = LocalDateTime.now();
    }
}
