package com.accet.parkinglot.model;

public class Gate {
    public enum GateType {
        ENTRY, EXIT
    }

    private final String gateId;
    private final int floorNumber; // Floor this gate is associated with
    private final GateType type;
    private final String locationDescription;

    public Gate(String gateId, int floorNumber, GateType type, String locationDescription) {
        this.gateId = gateId;
        this.floorNumber = floorNumber;
        this.type = type;
        this.locationDescription = locationDescription;
    }

    public String getGateId() {
        return gateId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public GateType getType() {
        return type;
    }

    public String getLocationDescription() {
        return locationDescription;
    }
}