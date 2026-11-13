package com.bikestore.controller;

import com.bikestore.model.Order;
import com.bikestore.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;

public class OrderController {
    private final InventoryController inventoryController;
    private final OrderService orderService;
    private final ObservableList<Order> history = FXCollections.observableArrayList();

    public OrderController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        this.orderService = new OrderService(inventoryController.service());
    }

    public ObservableList<Order> history() { return history; }


    public Order placeOrder(String customer, Map<Integer, Integer> requested) {
        Order order = orderService.createOrder(customer, requested);
        history.add(order);
        inventoryController.refresh(); // остатки изменились, таблица должна обновиться
        return order;
    }
}
