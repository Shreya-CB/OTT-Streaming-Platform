package com.ott.platform.service;

import com.ott.platform.dto.SubscriptionRequestDTO;
import com.ott.platform.model.*;
import java.util.List;

public interface SubscriptionService {
    Subscription subscribe(SubscriptionRequestDTO dto);
    Subscription cancelSubscription(Long subscriptionId);
    Subscription renewSubscription(Long subscriptionId, String paymentMethod);
    Subscription getActiveSubscription(Long viewerId);
    List<Subscription> getSubscriptionHistory(Long viewerId);
    List<SubscriptionPlan> getAllPlans();
    SubscriptionPlan createPlan(String name, double price, int durationMonths);
    void deletePlan(Long planId);
}
