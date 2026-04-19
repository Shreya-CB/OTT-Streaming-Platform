package com.ott.platform.pattern.strategy;

import com.ott.platform.model.*;
import com.ott.platform.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete payment strategy – simulates an online card/UPI payment gateway.
 * Swap this for WalletPaymentService, EMIPaymentService etc. without changing
 * SubscriptionService at all (OCP + DIP in action).
 */
@Component("onlinePayment")
public class OnlinePaymentService implements PaymentService {

    private final PaymentRepository paymentRepo;

    @Autowired
    public OnlinePaymentService(PaymentRepository paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Override
    public Payment processPayment(Subscription subscription, double amount) {
        /*
         * In production: call real payment gateway SDK here.
         * For the demo we simulate success for all amounts > 0.
         */
        String status = (amount > 0) ? "SUCCESS" : "FAILED";
        Payment payment = new Payment(amount, status, subscription);
        return paymentRepo.save(payment);
    }
}
