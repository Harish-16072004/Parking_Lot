package com.accet.parkinglot.service;

import com.accet.parkinglot.model.VehicleType;

public class ChargingService {

    // Define charging rates per kWh for different EV types
    private static final int ELECTRIC_BIKE_RATE_PER_KWH = 5;  // Example: 5 rupees per kWh
    private static final int ELECTRIC_CAR_RATE_PER_KWH = 10; // Example: 10 rupees per kWh

    public ChargingService() {
        // Constructor, no special initialization needed for now
    }

    public int calculateChargingCost(VehicleType vehicleType, double kwhConsumed) {
        int rate;
        switch (vehicleType) {
            case ELECTRIC_BIKE -> rate = ELECTRIC_BIKE_RATE_PER_KWH;
            case ELECTRIC_CAR -> rate = ELECTRIC_CAR_RATE_PER_KWH;
            default -> {
                System.out.println("Warning: Charging service called for non-electric vehicle type: " + vehicleType);
                return 0; // Or throw an IllegalArgumentException
            }
        }
        return (int) (rate * kwhConsumed);
    }
}