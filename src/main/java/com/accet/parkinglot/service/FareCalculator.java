package com.accet.parkinglot.service;

import com.accet.parkinglot.model.VehicleType;

public class FareCalculator {

    private final SubscriptionManager subscriptionManager; // New field

    // Constructor now takes SubscriptionManager
    public FareCalculator(SubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

    // calculateFee now takes a third argument for subscription status/plan
    public int calculateFee(VehicleType type, int hours, String subscriptionPlanId) {
        int rate;
        switch (type) {
            case BICYCLE -> rate = 10;
            case TWO_WHEELER -> rate = 15;
            case THREE_WHEELER -> rate = 20;
            case CAR -> rate = 30;
            case VAN, MINI_TRUCK -> rate = 35;
            case ELECTRIC_BIKE -> rate = 15;
            case ELECTRIC_CAR -> rate = 30;
            default -> throw new IllegalArgumentException("Unsupported vehicle type");
        }

        int baseFee = rate * hours;

        // Apply discount based on subscription plan, if provided and valid
        if (subscriptionPlanId != null && subscriptionManager.isSubscribed(subscriptionPlanId)) {
            // Assuming "Premium" plan offers a discount, or any subscribed vehicle gets one.
            // This logic can be further refined based on actual subscription tiers.
            System.out.println("Applying subscription discount for " + subscriptionPlanId);
            return (int) (baseFee * 0.8); // Example: 20% discount for subscribed vehicles
        }

        return baseFee;
    }
}