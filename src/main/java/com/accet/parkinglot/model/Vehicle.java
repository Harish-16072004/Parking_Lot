package com.accet.parkinglot.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vehicle {
    private final String registrationNumber;
    private final VehicleType type;

    // Regex for "CHARCHAR NUMNUM CHARCHAR NUMNUMNUMNUM" e.g., "TN 01 AA 0001"
    private static final String REG_NUMBER_PATTERN = "^[A-Z]{2}\\s\\d{2}\\s[A-Z]{2}\\s\\d{4}$";
    private static final Pattern REG_NUMBER_REGEX_PATTERN = Pattern.compile(REG_NUMBER_PATTERN);

    public Vehicle(String registrationNumber, VehicleType type) {
        if (!isValidRegistrationNumber(registrationNumber)) {
            throw new IllegalArgumentException("Invalid vehicle registration number format. Expected: 'CC NN CC NNNN' (e.g., 'TN 01 AA 0001')");
        }
        this.registrationNumber = registrationNumber;
        this.type = type;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public VehicleType getType() {
        return type;
    }

    // New static method for validation
    public static boolean isValidRegistrationNumber(String regNumber) {
        if (regNumber == null) {
            return false;
        }
        Matcher matcher = REG_NUMBER_REGEX_PATTERN.matcher(regNumber);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return "Vehicle{" +
               "registrationNumber='" + registrationNumber + '\'' +
               ", type=" + type +
               '}';
    }
}