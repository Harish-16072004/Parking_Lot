package com.accet.parkinglot.model;

public class ParkingSpot {
    private final String spotId;
    private final VehicleType type;
    private final int floorNumber;
    private boolean isOccupied;

    public ParkingSpot(String spotId, VehicleType type, int floorNumber) {
        this.spotId = spotId;
        this.type = type;
        this.floorNumber = floorNumber;
        this.isOccupied = false; // Initially not occupied
    }

    public String getSpotId() {
        return spotId;
    }

    public VehicleType getType() {
        return type;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSpot that = (ParkingSpot) o;
        return spotId.equals(that.spotId);
    }

    @Override
    public int hashCode() {
        return spotId.hashCode();
    }
}