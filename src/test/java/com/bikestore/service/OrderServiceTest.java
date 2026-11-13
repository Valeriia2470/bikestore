package com.bikestore.service;

import com.bikestore.model.Bike;
import com.bikestore.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    private InventoryService inventory;
    private OrderService orders;

    @BeforeEach
    void setUp() {
        inventory = new InventoryService(List.of(
                new Bike(1, "Trek", "MOUNTAIN", 500, 5),
                new Bike(2, "Giant", "CITY", 400, 2)
        ));
        orders = new OrderService(inventory);
    }

    private int stock(int id) {
        return inventory.search().findById(id).orElseThrow().getStock();
    }

    @Test
    void successfulOrderDeductsStock() {
        Order o = orders.createOrder("Anna", Map.of(1, 2, 2, 1));
        assertEquals(3, stock(1));
        assertEquals(1, stock(2));
        assertEquals(2 * 500 + 400, o.total(), 1e-9);
    }

    @Test
    void insufficientStockChangesNothing() {
        Map<Integer, Integer> req = new LinkedHashMap<>();
        req.put(1, 3);   // хватает
        req.put(2, 10);  // не хватает
        assertThrows(OrderException.class, () -> orders.createOrder("Anna", req));

        assertEquals(5, stock(1));
        assertEquals(2, stock(2));
        assertTrue(orders.orders().isEmpty());
    }

    @Test
    void unknownBikeAndBadQuantity() {
        assertThrows(OrderException.class, () -> orders.createOrder("Anna", Map.of(99, 1)));
        assertThrows(OrderException.class, () -> orders.createOrder("Anna", Map.of(1, 0)));
        assertThrows(OrderException.class, () -> orders.createOrder(" ", Map.of(1, 1)));
    }

    @Test
    void searchIndexStaysCorrectAfterOrder() {
        orders.createOrder("Anna", Map.of(2, 2)); // Giant: остаток 0
        assertEquals(1, inventory.search().minStock(1).size()); // только Trek
    }
}