package com.bikestore.model;

public record OrderItem(int bikeId, String bikeName, int quantity, double unitPrice) {
    public double subtotal() {
        return quantity * unitPrice;
    }
}