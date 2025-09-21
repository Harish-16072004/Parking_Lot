package com.accet.parkinglot.service;

import com.accet.parkinglot.model.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingRegistry {
    private final Map<String, Booking> bookingMap;

    public BookingRegistry() {
        this.bookingMap = new HashMap<>();
    }

    public void addBooking(Booking booking) {
        bookingMap.put(booking.getBookingId(), booking);
    }

    public Booking getBooking(String bookingId) {
        return bookingMap.get(bookingId);
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookingMap.values());
    }
}