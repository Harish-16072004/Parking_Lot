package com.accet.parkinglot.model;

import java.time.LocalDateTime;

public class Payment {
    private final String paymentId;
    private final int amount;
    private final LocalDateTime timestamp;
    private final PaymentMethod paymentMethod;

    public Payment(String paymentId, int amount, LocalDateTime timestamp, PaymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}