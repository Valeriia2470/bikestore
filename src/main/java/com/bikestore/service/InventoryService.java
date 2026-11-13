package com.bikestore.service;

import com.bikestore.model.Bike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryService {
    private final List<Bike> bikes = new ArrayList<>();
    private final SearchService search;

    public InventoryService(List<Bike> initial) {
        bikes.addAll(initial);
        search = new SearchService(bikes);
    }

    public List<Bike> all() {
        return Collections.unmodifiableList(bikes);
    }

    public SearchService search() {
        return search;
    }

    public void add(Bike bike) {
        if (search.findById(bike.getId()).isPresent()) {
            throw new IllegalArgumentException("duplicate id " + bike.getId());
        }
        bikes.add(bike);
        search.reindex(bikes);
    }

    public boolean remove(int id) {
        boolean removed = bikes.removeIf(b -> b.getId() == id);
        if (removed) search.reindex(bikes);
        return removed;
    }

    public void setStock(int id, int stock) {
        Bike bike = search.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("no bike with id " + id));
        bike.setStock(stock);
        search.reindex(bikes);
    }
    public void replaceAll(List<Bike> newBikes) {
        bikes.clear();
        bikes.addAll(newBikes);
        search.reindex(bikes);
    }
}