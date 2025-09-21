package com.accet.parkinglot.service;

import com.accet.parkinglot.model.VehicleType;
import com.accet.parkinglot.model.Vehicle;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionManager {
    // Stores registration number -> VehicleType for subscribed vehicles
    private final Map<String, VehicleType> subscribedVehicles;

    public SubscriptionManager() {
        this.subscribedVehicles = new HashMap<>();
        // Removed all dummy subscribers
    }

    public void subscribe(String registrationNumber, VehicleType type) {
        if (!Vehicle.isValidRegistrationNumber(registrationNumber)) {
            System.out.println("Subscription failed: Invalid vehicle registration number format for " + registrationNumber);
            return;
        }
        if (subscribedVehicles.containsKey(registrationNumber)) {
            System.out.println("Vehicle " + registrationNumber + " is already subscribed.");
        } else {
            subscribedVehicles.put(registrationNumber, type);
            System.out.println("Vehicle " + registrationNumber + " (" + type + ") has been subscribed.");
        }
    }

    public boolean isSubscribed(String registrationNumber) {
        return subscribedVehicles.containsKey(registrationNumber);
    }

    public VehicleType getVehicleTypeForSubscriber(String registrationNumber) {
        return subscribedVehicles.get(registrationNumber);
    }

    public Map<String, VehicleType> getAllSubscribedVehicles() {
        return new HashMap<>(subscribedVehicles);
    }

    public void showSubscriptionDetails() {
        System.out.println("\n--- Subscription Plans ---");
        System.out.println("Basic Plan: ₹100/month");
        System.out.println("Premium Plan: ₹250/month (priority parking, EV charging included, 20% discount on parking fees)");
        System.out.println("\n--- Currently Subscribed Vehicles ---");
        if (subscribedVehicles.isEmpty()) {
            System.out.println("No vehicles currently subscribed.");
        } else {
            subscribedVehicles.forEach((regNum, type) ->
                System.out.println("  Reg No: " + regNum + ", Type: " + type));
        }
        System.out.println("------------------------------------");
    }
}