package com.accet.parkinglot.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ParkingLotFloor {
    private final int floorNumber;
    private final String floorName;
    // Map to store queues of available parking spots by vehicle type
    private final Map<VehicleType, Queue<ParkingSpot>> spotsByVehicleType;
    // Map to store all occupied spots by their Spot ID for quick lookup
    private final Map<String, ParkingSpot> occupiedSpots;

    public ParkingLotFloor(int floorNumber, String floorName) {
        this.floorNumber = floorNumber;
        this.floorName = floorName;
        this.spotsByVehicleType = new HashMap<>();
        this.occupiedSpots = new HashMap<>();
        // Initialize queues for all vehicle types
        for (VehicleType type : VehicleType.values()) {
            spotsByVehicleType.put(type, new LinkedList<>());
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public String getFloorName() {
        return floorName;
    }

    public Map<VehicleType, Queue<ParkingSpot>> getSpotsByVehicleType() {
        return spotsByVehicleType;
    }

    /**
     * Adds a parking spot to the floor. Initially, all spots are available.
     * @param spot The ParkingSpot to add.
     */
    public void addSpot(ParkingSpot spot) {
        if (spot.getFloorNumber() != this.floorNumber) {
            System.err.println("Warning: Attempted to add spot " + spot.getSpotId() + " to wrong floor. Spot floor: " + spot.getFloorNumber() + ", Current floor: " + this.floorNumber);
            return;
        }
        spotsByVehicleType.get(spot.getType()).offer(spot);
    }

    /**
     * Retrieves an available spot for a given vehicle type.
     * The spot is NOT removed from the available queue yet.
     * @param vehicleType The type of vehicle.
     * @return An available ParkingSpot, or null if none is available.
     */
    public ParkingSpot getAvailableSpot(VehicleType vehicleType) {
        Queue<ParkingSpot> availableSpots = spotsByVehicleType.get(vehicleType);
        if (availableSpots != null && !availableSpots.isEmpty()) {
            return availableSpots.peek(); // Just peek, don't remove yet
        }
        return null;
    }

    /**
     * Marks a specific spot as occupied. Removes it from available queue and adds to occupied map.
     * @param spot The ParkingSpot to allocate.
     * @return true if allocation was successful, false otherwise (e.g., spot not found or already occupied).
     */
    public boolean allocateSpot(ParkingSpot spot) {
        Queue<ParkingSpot> availableSpots = spotsByVehicleType.get(spot.getType());
        if (availableSpots != null && availableSpots.contains(spot) && !occupiedSpots.containsKey(spot.getSpotId())) {
            availableSpots.remove(spot); // Remove from available queue
            spot.setOccupied(true);
            occupiedSpots.put(spot.getSpotId(), spot); // Add to occupied map
            return true;
        }
        return false;
    }

    /**
     * Marks a specific spot as free. Removes it from occupied map and adds back to available queue.
     * @param spot The ParkingSpot to release.
     * @return true if release was successful, false otherwise (e.g., spot not found in occupied).
     */
    public boolean releaseSpot(ParkingSpot spot) {
        if (occupiedSpots.containsKey(spot.getSpotId())) {
            occupiedSpots.remove(spot.getSpotId()); // Remove from occupied map
            spot.setOccupied(false);
            spotsByVehicleType.get(spot.getType()).offer(spot); // Add back to available queue
            return true;
        }
        return false;
    }

    // You could add a method to get occupied spots if needed for admin view
    public Map<String, ParkingSpot> getOccupiedSpots() {
        return new HashMap<>(occupiedSpots); // Return a copy to prevent external modification
    }
}