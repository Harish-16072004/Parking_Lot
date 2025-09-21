package com.accet.parkinglot.exception;

public class UnsupportedVehicleTypeException extends RuntimeException {
    public UnsupportedVehicleTypeException(String message) {
        super(message);
    }
}