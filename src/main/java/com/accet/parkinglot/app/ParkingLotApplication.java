package com.accet.parkinglot.app;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.accet.parkinglot.model.Booking;
import com.accet.parkinglot.model.Gate;
import com.accet.parkinglot.model.ParkingLotFloor;
import com.accet.parkinglot.model.ParkingSpot;
import com.accet.parkinglot.model.PaymentMethod;
import com.accet.parkinglot.model.Vehicle;
import com.accet.parkinglot.model.VehicleType;
import com.accet.parkinglot.service.ChargingService;
import com.accet.parkinglot.service.ParkingLotInitializer;
import com.accet.parkinglot.service.ParkingLotService;
import com.accet.parkinglot.service.SubscriptionManager;

public class ParkingLotApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static ParkingLotService parkingLotService;
    private static SubscriptionManager subscriptionManager;
    private static ChargingService chargingService;
    private static List<Gate> gates;
    private static Map<Integer, ParkingLotFloor> parkingFloors;

    public static void main(String[] args) {
        try (Scanner scannerResource = new Scanner(System.in)) {
            scanner = scannerResource;
            initializeSystem();
            runApplication();
        }
    }

    private static void initializeSystem() {
        subscriptionManager = new SubscriptionManager();
        parkingLotService = new ParkingLotService(subscriptionManager);
        chargingService = new ChargingService();
        // ChargingSlotManager chargingSlotManager = new ChargingSlotManager(); // Not directly used in main flow yet

        ParkingLotInitializer initializer = new ParkingLotInitializer();
        parkingFloors = initializer.getParkingFloors();
        gates = initializer.getGates();
        parkingLotService.addParkingFloors(parkingFloors);

        System.out.println("--- Parking Lot System Initialized ---");
        System.out.println("Total parking spots created: " + parkingLotService.getTotalSpotsInitialized());
        System.out.println("Parking lot is ready for operations.");
        System.out.println("--------------------------------------\n");
    }

    private static void runApplication() {
        while (true) {
            System.out.println("\n--- Welcome to Parking Lot System ---");
            System.out.println("Are you an (A)dmin or (C)ustomer? (Type 'exit' to quit)");
            System.out.print("Enter your role: ");
            String role = scanner.nextLine().trim().toLowerCase();

            switch (role) {
                case "a":
                case "admin":
                    handleAdminRole();
                    break;
                case "c":
                case "customer":
                    handleCustomerRole();
                    break;
                case "exit":
                    System.out.println("Exiting Parking Lot System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid role. Please enter 'Admin' or 'Customer'.");
            }
        }
    }

    private static void handleAdminRole() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Show Parked Vehicles");
            System.out.println("2. Show Parking History");
            System.out.println("3. Show Subscriber Details");
            System.out.println("4. Show Available Spots");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    parkingLotService.showAllParkedVehicles();
                    break;
                case "2":
                    parkingLotService.showParkingHistory();
                    break;
                case "3":
                    subscriptionManager.showSubscriptionDetails();
                    break;
                case "4":
                    parkingLotService.showAvailableSpots();
                    break;
                case "5":
                    return; // Go back to main menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleCustomerRole() {
        System.out.println("\n--- Customer Menu ---");
        System.out.print("Are you a subscriber? (yes/no): ");
        String isSubscriber = scanner.nextLine().trim().toLowerCase();

        if ("yes".equals(isSubscriber)) {
            handleSubscriberCustomer();
        } else {
            handleNonSubscriberCustomer();
        }
    }

    private static void handleSubscriberCustomer() {
        System.out.print("Enter your subscriber ID (Vehicle Registration Number, e.g., TN 01 AA 0001): ");
        String subscriberId = scanner.nextLine().trim().toUpperCase();

        if (!subscriptionManager.isSubscribed(subscriberId)) {
            System.out.println("Subscriber ID not found or not active. Please check your ID or consider subscribing.");
            return;
        }

        VehicleType vehicleType = subscriptionManager.getVehicleTypeForSubscriber(subscriberId);
        if (vehicleType == null) {
            System.out.println("Error: Could not retrieve vehicle type for this subscriber ID.");
            return;
        }
        Vehicle vehicle;
        try {
            vehicle = new Vehicle(subscriberId, vehicleType);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        Gate selectedGate = selectEntryGate();
        if (selectedGate == null) {
            System.out.println("Gate selection cancelled or invalid.");
            return;
        }

        try {
            System.out.println("Searching for nearest available spot from " + parkingFloors.get(selectedGate.getFloorNumber()).getFloorName() + " for your " + vehicle.getType() + "...");
            ParkingSpot nearestSpot = parkingLotService.findNearestAvailableSpot(selectedGate.getFloorNumber(), vehicle.getType());

            if (nearestSpot != null) {
                Booking booking = parkingLotService.bookSpot(vehicle, nearestSpot);
                System.out.println("SUCCESS! Parked your " + vehicle.getType() + " at spot: " + booking.getSpot().getSpotId() + " on " + parkingFloors.get(booking.getSpot().getFloorNumber()).getFloorName());
                System.out.println("Your booking ID is: " + booking.getBookingId());

                if (vehicle.getType() == VehicleType.ELECTRIC_BIKE || vehicle.getType() == VehicleType.ELECTRIC_CAR) {
                    askForCharging(vehicle.getType());
                }
            } else {
                System.out.println("Sorry, no available spots found for your " + vehicle.getType() + " at this time.");
            }
        } catch (RuntimeException e) {
            System.err.println("Parking failed: " + e.getMessage());
        }
    }

    private static void handleNonSubscriberCustomer() {
        while (true) {
            System.out.println("\n--- Non-Subscriber Customer Menu ---");
            System.out.println("1. Book a Spot");
            System.out.println("2. View Available Spots");
            System.out.println("3. New Subscription");
            System.out.println("4. Exit Vehicle");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    bookSpotForNonSubscriber();
                    break;
                case "2":
                    parkingLotService.showAvailableSpots();
                    break;
                case "3":
                    handleNewSubscription();
                    break;
                case "4":
                    exitVehicle();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void bookSpotForNonSubscriber() {
        VehicleType vehicleType = selectVehicleType();
        if (vehicleType == null) {
            return;
        }
        String regNumber;
        while (true) {
            System.out.print("Enter vehicle registration number (e.g., TN 01 AA 0001): ");
            regNumber = scanner.nextLine().trim().toUpperCase();
            if (Vehicle.isValidRegistrationNumber(regNumber)) {
                break;
            } else {
                System.out.println("Invalid format. Expected: 'CC NN CC NNNN' (e.g., 'TN 01 AA 0001'). Please try again.");
            }
        }
        Vehicle vehicle = new Vehicle(regNumber, vehicleType);

        Gate selectedGate = selectEntryGate();
        if (selectedGate == null) {
            System.out.println("Gate selection cancelled or invalid.");
            return;
        }

        try {
            System.out.println("Searching for nearest available spot from " + parkingFloors.get(selectedGate.getFloorNumber()).getFloorName() + " for " + vehicle.getType() + "...");
            ParkingSpot nearestSpot = parkingLotService.findNearestAvailableSpot(selectedGate.getFloorNumber(), vehicle.getType());

            if (nearestSpot != null) {
                Booking booking = parkingLotService.bookSpot(vehicle, nearestSpot);
                System.out.println("Successfully booked: " + booking.getSpot().getSpotId() + " for " + booking.getVehicle().getRegistrationNumber());
                System.out.println("Your booking ID is: " + booking.getBookingId());

                if (vehicle.getType() == VehicleType.ELECTRIC_BIKE || vehicle.getType() == VehicleType.ELECTRIC_CAR) {
                    askForCharging(vehicle.getType());
                }

            } else {
                System.out.println("Sorry, no available spots found for " + vehicle.getType() + " at this time.");
            }
        } catch (RuntimeException e) {
            System.err.println("Booking failed: " + e.getMessage());
        }
    }

    private static void handleNewSubscription() {
        String regNumber;
        while (true) {
            System.out.print("Enter vehicle registration number for subscription (e.g., TN 01 AA 0001): ");
            regNumber = scanner.nextLine().trim().toUpperCase();
            if (Vehicle.isValidRegistrationNumber(regNumber)) {
                break;
            } else {
                System.out.println("Invalid format. Expected: 'CC NN CC NNNN' (e.g., 'TN 01 AA 0001'). Please try again.");
            }
        }
        VehicleType vehicleType = selectVehicleType();
        if (vehicleType == null) {
            return;
        }
        subscriptionManager.subscribe(regNumber, vehicleType);
    }

    private static void exitVehicle() {
        System.out.print("Enter your Booking ID to exit: ");
        String bookingId = scanner.nextLine().trim();

        PaymentMethod paymentMethod = selectPaymentMethod();
        if (paymentMethod == null) {
            System.out.println("Payment method not selected. Exit cancelled.");
            return;
        }

        try {
            parkingLotService.releaseSpot(bookingId, paymentMethod);
        } catch (RuntimeException e) {
            System.err.println("Error exiting vehicle: " + e.getMessage());
        }
    }

    private static PaymentMethod selectPaymentMethod() {
        while (true) {
            System.out.println("\nSelect Payment Method:");
            int i = 1;
            for (PaymentMethod method : PaymentMethod.values()) {
                System.out.println((i++) + ". " + method);
            }
            System.out.print("Enter payment method number (or '0' to cancel): ");
            try {
                int methodChoice = Integer.parseInt(scanner.nextLine());
                if (methodChoice == 0) {
                    return null;
                }
                if (methodChoice > 0 && methodChoice <= PaymentMethod.values().length) {
                    return PaymentMethod.values()[methodChoice - 1];
                } else {
                    System.out.println("Invalid payment method number. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static void askForCharging(VehicleType vehicleType) {
        System.out.print("Do you need charging for your " + vehicleType + "? (yes/no): ");
        String chargingChoice = scanner.nextLine().trim().toLowerCase();

        if ("yes".equals(chargingChoice)) {
            double kwh = -1;
            while (kwh < 0) {
                System.out.print("Enter units of charge needed in kWh (e.g., 5.5): ");
                try {
                    kwh = Double.parseDouble(scanner.nextLine());
                    if (kwh < 0) {
                        System.out.println("Units cannot be negative. Please enter a positive value.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a numerical value for kWh.");
                }
            }
            int chargingCost = (int) Math.ceil(chargingService.calculateChargingCost(vehicleType, kwh));
            System.out.println("Charging cost for " + kwh + " kWh is: ₹" + chargingCost);
            System.out.println("Please pay the charging amount separately.");
        } else {
            System.out.println("No charging requested.");
        }
    }

    private static Gate selectEntryGate() {
        System.out.println("\nAvailable Entry Gates:");
        List<Gate> entryGates = gates.stream()
                                    .filter(g -> g.getType() == Gate.GateType.ENTRY)
                                    .toList();
        for (int i = 0; i < entryGates.size(); i++) {
            System.out.println((i + 1) + ". " + entryGates.get(i).getGateId() + " (" + entryGates.get(i).getLocationDescription() + ")");
        }

        while (true) {
            System.out.print("Enter the number of your preferred Entry Gate (or '0' to cancel): ");
            try {
                int gateChoice = Integer.parseInt(scanner.nextLine());
                if (gateChoice == 0) {
                    return null;
                }
                if (gateChoice > 0 && gateChoice <= entryGates.size()) {
                    return entryGates.get(gateChoice - 1);
                } else {
                    System.out.println("Invalid gate number. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static VehicleType selectVehicleType() {
        while (true) {
            System.out.println("\nSelect Vehicle Type:");
            int i = 1;
            for (VehicleType type : VehicleType.values()) {
                System.out.println((i++) + ". " + type);
            }
            System.out.print("Enter vehicle type number (or '0' to cancel): ");
            try {
                int typeChoice = Integer.parseInt(scanner.nextLine());
                if (typeChoice == 0) {
                    return null;
                }
                if (typeChoice > 0 && typeChoice <= VehicleType.values().length) {
                    return VehicleType.values()[typeChoice - 1];
                } else {
                    System.out.println("Invalid vehicle type number. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}