package com.accet.parkinglot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.Collectors;

import com.accet.parkinglot.model.Booking;
import com.accet.parkinglot.model.ParkingLotFloor;
import com.accet.parkinglot.model.ParkingSpot;
import com.accet.parkinglot.model.Payment;
import com.accet.parkinglot.model.PaymentMethod;
import com.accet.parkinglot.model.Vehicle;
import com.accet.parkinglot.model.VehicleType;

public class ParkingLotService {
    private final Map<Integer, ParkingLotFloor> parkingFloors;
    private final BookingRegistry bookingRegistry;
    private final FareCalculator fareCalculator;
    private final SubscriptionManager subscriptionManager;
    private int totalSpotsInitialized = 0;

    public ParkingLotService(SubscriptionManager subscriptionManager) {
        this.parkingFloors = new HashMap<>();
        this.bookingRegistry = new BookingRegistry();
        this.subscriptionManager = subscriptionManager;
        this.fareCalculator = new FareCalculator(this.subscriptionManager);
    }

    public void addParkingFloors(Map<Integer, ParkingLotFloor> floors) {
        this.parkingFloors.putAll(floors);
        totalSpotsInitialized = floors.values().stream()
                .flatMap(floor -> floor.getSpotsByVehicleType().values().stream())
                .mapToInt(Queue::size)
                .sum();
    }

    public int getTotalSpotsInitialized() {
        return totalSpotsInitialized;
    }

    public ParkingSpot findNearestAvailableSpot(int requestedFloorNumber, VehicleType vehicleType) {
        List<Integer> searchOrder = new ArrayList<>();
        searchOrder.add(requestedFloorNumber);

        // Add floors above
        for (int i = requestedFloorNumber + 1; i <= Collections.max(parkingFloors.keySet()); i++) {
            if (parkingFloors.containsKey(i)) {
                searchOrder.add(i);
            }
        }
        // Add floors below
        for (int i = requestedFloorNumber - 1; i >= Collections.min(parkingFloors.keySet()); i--) {
            if (parkingFloors.containsKey(i)) {
                searchOrder.add(i);
            }
        }

        for (int floorNum : searchOrder) {
            ParkingLotFloor floor = parkingFloors.get(floorNum);
            if (floor != null) {
                ParkingSpot spot = floor.getAvailableSpot(vehicleType);
                if (spot != null) {
                    return spot;
                }
            }
        }
        return null;
    }


    public Booking bookSpot(Vehicle vehicle, ParkingSpot desiredSpot) {
        if (desiredSpot == null) {
            throw new RuntimeException("No suitable parking spot provided for booking.");
        }

        ParkingLotFloor floor = parkingFloors.get(desiredSpot.getFloorNumber());
        if (floor == null) {
            throw new RuntimeException("Spot's floor does not exist: " + desiredSpot.getFloorNumber());
        }

        if (!floor.allocateSpot(desiredSpot)) {
             throw new RuntimeException("Desired spot " + desiredSpot.getSpotId() + " is not available or already occupied.");
        }

        String bookingId = UUID.randomUUID().toString().substring(0, 8);
        Booking booking = new Booking(bookingId, vehicle, desiredSpot, LocalDateTime.now());
        bookingRegistry.addBooking(booking);

        return booking;
    }

    // Modified to accept PaymentMethod
    public void releaseSpot(String bookingId, PaymentMethod paymentMethod) {
        Booking booking = bookingRegistry.getBooking(bookingId);
        if (booking == null) {
            throw new RuntimeException("Invalid booking ID: " + bookingId);
        }

        ParkingSpot spot = booking.getSpot();
        ParkingLotFloor floor = parkingFloors.get(spot.getFloorNumber());

        if (floor == null) {
            throw new RuntimeException("Error: Spot's floor " + spot.getFloorNumber() + " not found during release.");
        }

        floor.releaseSpot(spot);

        LocalDateTime endTime = LocalDateTime.now();
        booking.setEndTime(endTime);

        int durationMinutes = (int) java.time.Duration.between(booking.getStartTime(), endTime).toMinutes();
        int durationHours = (int) Math.max(1, Math.ceil(durationMinutes / 60.0)); // Round up to nearest hour, minimum 1 hour

        String vehicleRegNumber = booking.getVehicle().getRegistrationNumber();
        String subscriptionPlanId = subscriptionManager.isSubscribed(vehicleRegNumber) ? vehicleRegNumber : null;

        int fee = fareCalculator.calculateFee(booking.getVehicle().getType(), durationHours, subscriptionPlanId);

        // Use the passed paymentMethod
        Payment payment = new Payment(UUID.randomUUID().toString(), fee, endTime, paymentMethod);
        booking.setPayment(payment);

        System.out.println("Vehicle " + booking.getVehicle().getRegistrationNumber() + " released from " + spot.getSpotId());
        System.out.println("Parking duration: " + durationHours + " hours. Total Fee: ₹" + fee + " (Paid by: " + paymentMethod + ")");
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookingRegistry.getAllBookings());
    }

    public void showAllParkedVehicles() {
        List<Booking> activeBookings = bookingRegistry.getAllBookings().stream()
                .filter(b -> b.getEndTime() == null) // Filter for active bookings (not yet released)
                .collect(Collectors.toList());

        System.out.println("\n--- Currently Parked Vehicles ---");
        if (activeBookings.isEmpty()) {
            System.out.println("No vehicles currently parked.");
            return;
        }

        activeBookings.forEach(booking -> {
            ParkingSpot spot = booking.getSpot();
            System.out.println("  Reg No: " + booking.getVehicle().getRegistrationNumber() +
                               ", Type: " + booking.getVehicle().getType() +
                               ", Spot: " + spot.getSpotId() +
                               " (Floor " + parkingFloors.get(spot.getFloorNumber()).getFloorName() + ")" +
                               ", Parked Since: " + booking.getStartTime() +
                               ", Booking ID: " + booking.getBookingId());
        });
        System.out.println("---------------------------------");
    }

    public void showParkingHistory() {
        List<Booking> completedBookings = bookingRegistry.getAllBookings().stream()
                .filter(b -> b.getEndTime() != null) // Filter for completed bookings
                .collect(Collectors.toList());

        System.out.println("\n--- Parking History ---");
        if (completedBookings.isEmpty()) {
            System.out.println("No parking history available.");
            return;
        }

        completedBookings.forEach(booking -> {
            ParkingSpot spot = booking.getSpot();
            Payment payment = booking.getPayment();
            String feeDetails = (payment != null) ? "Fee: ₹" + payment.getAmount() + " (" + payment.getPaymentMethod() + ")" : "Fee: N/A";
            System.out.println("  Booking ID: " + booking.getBookingId());
            System.out.println("    Vehicle: " + booking.getVehicle().getRegistrationNumber() + " (" + booking.getVehicle().getType() + ")");
            System.out.println("    Spot: " + spot.getSpotId() + " (Floor " + parkingFloors.get(spot.getFloorNumber()).getFloorName() + ")");
            System.out.println("    Parked: " + booking.getStartTime() + " to " + booking.getEndTime());
            System.out.println("    " + feeDetails);
            System.out.println("    ---");
        });
        System.out.println("---------------------");
    }

    public void showAvailableSpots() {
        System.out.println("\n--- Available Parking Spots ---");
        boolean anyAvailable = false;
        for (ParkingLotFloor floor : parkingFloors.values()) {
            System.out.println("  " + floor.getFloorName() + ":");
            Map<VehicleType, Queue<ParkingSpot>> spotsByVehicleType = floor.getSpotsByVehicleType();
            boolean floorHasAvailable = false;
            for (Map.Entry<VehicleType, Queue<ParkingSpot>> entry : spotsByVehicleType.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    System.out.println("    " + entry.getKey() + ": " + entry.getValue().size() + " spots available (e.g., " + entry.getValue().peek().getSpotId() + ")");
                    floorHasAvailable = true;
                    anyAvailable = true;
                }
            }
            if (!floorHasAvailable) {
                System.out.println("    No spots currently available on this floor.");
            }
        }
        if (!anyAvailable) {
            System.out.println("No parking spots available across all floors for any vehicle type.");
        }
        System.out.println("-----------------------------");
    }
}