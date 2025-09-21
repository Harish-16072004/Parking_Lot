package com.accet.parkinglot.exception;

public class ChargingLimitExceededException extends RuntimeException {
    public ChargingLimitExceededException(String message) {
        super(message);
    }
}