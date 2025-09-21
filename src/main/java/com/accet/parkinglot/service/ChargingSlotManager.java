package com.accet.parkinglot.service;

import com.accet.parkinglot.model.VehicleType;

import java.util.HashMap;
import java.util.Map;

public class ChargingSlotManager {
    private final Map<VehicleType, Integer> availableSlots = new HashMap<>();

    public ChargingSlotManager() {
        availableSlots.put(VehicleType.ELECTRIC_BIKE, 100);
        availableSlots.put(VehicleType.ELECTRIC_CAR, 50);
    }

    public boolean hasAvailableSlot(VehicleType type) {
        return availableSlots.getOrDefault(type, 0) > 0;
    }

    public void allocateSlot(VehicleType type) {
        if (!hasAvailableSlot(type)) {
            throw new IllegalStateException("No charging slots available for " + type);
        }
        availableSlots.put(type, availableSlots.get(type) - 1);
    }

    public void releaseSlot(VehicleType type) {
        availableSlots.put(type, availableSlots.getOrDefault(type, 0) + 1);
    }

    public int getAvailableSlotsCount(VehicleType type) {
        return availableSlots.getOrDefault(type, 0);
    }
}