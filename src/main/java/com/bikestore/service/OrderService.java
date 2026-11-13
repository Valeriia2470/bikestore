package com.bikestore.service;

import com.bikestore.model.Bike;
import com.bikestore.model.Order;
import com.bikestore.model.OrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final InventoryService inventory;
    private final List<Order> orders = new ArrayList<>();
    private int nextId = 1;

    public OrderService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public List<Order> orders() {
        return Collections.unmodifiableList(orders);
    }


    public Order createOrder(String customer, Map<Integer, Integer> requested) {
        if (customer == null || customer.isBlank()) throw new OrderException("customer name is empty");
        if (requested.isEmpty()) throw new OrderException("order is empty");


        List<OrderItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : requested.entrySet()) {
            int id = e.getKey();
            int qty = e.getValue();
            if (qty <= 0) throw new OrderException("quantity must be positive for bike " + id);

            Bike bike = inventory.search().findById(id)
                    .orElseThrow(() -> new OrderException("unknown bike id " + id));
            if (bike.getStock() < qty) {
                throw new OrderException("not enough stock for " + bike.getName()
                        + ": requested " + qty + ", available " + bike.getStock());
            }
            items.add(new OrderItem(id, bike.getName(), qty, bike.getPrice()));
        }


        List<OrderItem> deducted = new ArrayList<>();
        try {
            for (OrderItem item : items) {
                Bike bike = inventory.search().findById(item.bikeId()).orElseThrow();
                inventory.setStock(item.bikeId(), bike.getStock() - item.quantity());
                deducted.add(item);
            }
        } catch (RuntimeException ex) {
            for (OrderItem item : deducted) {
                Bike bike = inventory.search().findById(item.bikeId()).orElseThrow();
                inventory.setStock(item.bikeId(), bike.getStock() + item.quantity());
            }
            throw new OrderException("order failed, stock restored: " + ex.getMessage());
        }

        Order order = new Order(nextId++, customer.trim(), List.copyOf(items));
        orders.add(order);
        return order;
    }
}