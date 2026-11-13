package com.bikestore.service;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}