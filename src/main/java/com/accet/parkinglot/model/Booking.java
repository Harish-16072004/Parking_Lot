package com.accet.parkinglot.model;

import java.time.LocalDateTime;

public class Booking {
    private final String bookingId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private Payment payment;

    public Booking(String bookingId, Vehicle vehicle, ParkingSpot spot, LocalDateTime startTime) {
        this.bookingId = bookingId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.startTime = startTime;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}