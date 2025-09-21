package com.accet.parkinglot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.accet.parkinglot.model.Gate;
import com.accet.parkinglot.model.ParkingLotFloor;
import com.accet.parkinglot.model.ParkingSpot;
import com.accet.parkinglot.model.VehicleType;

public class ParkingLotInitializer {

    private final Map<Integer, ParkingLotFloor> parkingFloors;
    private final List<Gate> gates;

    public ParkingLotInitializer() {
        this.parkingFloors = new HashMap<>();
        this.gates = new ArrayList<>();
        initializeParkingFloors();
        initializeGates();
    }

    private void initializeParkingFloors() {
        // Ground Floor (Floor 0)
        ParkingLotFloor gf = new ParkingLotFloor(0, "Ground Floor");
        addSpots(gf, VehicleType.TWO_WHEELER, 10, "GF-BIKE-");
        addSpots(gf, VehicleType.CAR, 15, "GF-CAR-");
        addSpots(gf, VehicleType.ELECTRIC_CAR, 5, "GF-ECAR-");
        parkingFloors.put(0, gf);

        // First Floor (Floor 1)
        ParkingLotFloor f1 = new ParkingLotFloor(1, "First Floor");
        addSpots(f1, VehicleType.CAR, 20, "F1-CAR-");
        addSpots(f1, VehicleType.ELECTRIC_BIKE, 8, "F1-EBIKE-");
        parkingFloors.put(1, f1);

        // Second Floor (Floor 2)
        ParkingLotFloor f2 = new ParkingLotFloor(2, "Second Floor");
        addSpots(f2, VehicleType.CAR, 25, "F2-CAR-");
        addSpots(f2, VehicleType.TWO_WHEELER, 10, "F2-BIKE-");
        parkingFloors.put(2, f2);

        System.out.println("Parking floors and spots initialized.");
    }

    private void addSpots(ParkingLotFloor floor, VehicleType type, int count, String prefix) {
        for (int i = 1; i <= count; i++) {
            floor.addSpot(new ParkingSpot(prefix + i, type, floor.getFloorNumber()));
        }
    }

    private void initializeGates() {
        // Entry Gates
        gates.add(new Gate("E1", 0, Gate.GateType.ENTRY, "Main Entry - Ground Floor"));
        gates.add(new Gate("E2", 1, Gate.GateType.ENTRY, "Entry - First Floor"));

        // Exit Gates
        gates.add(new Gate("X1", 0, Gate.GateType.EXIT, "Main Exit - Ground Floor"));
        gates.add(new Gate("X2", 2, Gate.GateType.EXIT, "Exit - Second Floor"));

        System.out.println("Parking gates initialized.");
    }

    public Map<Integer, ParkingLotFloor> getParkingFloors() {
        return parkingFloors;
    }

    public List<Gate> getGates() {
        return gates;
    }
}