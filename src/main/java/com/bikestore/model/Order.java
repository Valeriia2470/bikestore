package com.bikestore.model;

import java.util.List;

public record Order(int id, String customer, List<OrderItem> items) {
    public double total() {
        double sum = 0;
        for (OrderItem it : items) sum += it.subtotal();
        return sum;
    }
}