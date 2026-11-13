package com.bikestore.service;

import com.bikestore.model.Bike;
import com.bikestore.util.BinarySearch;
import com.bikestore.util.MergeSort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SearchService {
    private static final Comparator<Bike> BY_ID = Comparator.comparingInt(Bike::getId);
    private static final Comparator<Bike> BY_PRICE = Comparator.comparingDouble(Bike::getPrice);
    private static final Comparator<Bike> BY_CATEGORY = Comparator.comparing(Bike::getCategory);
    private static final Comparator<Bike> BY_STOCK = Comparator.comparingInt(Bike::getStock);

    private final List<Bike> byId = new ArrayList<>();
    private final List<Bike> byPrice = new ArrayList<>();
    private final List<Bike> byCategory = new ArrayList<>();
    private final List<Bike> byStock = new ArrayList<>();

    public SearchService(List<Bike> bikes) {
        reindex(bikes);
    }


    public void reindex(List<Bike> bikes) {
        rebuild(byId, bikes, BY_ID);
        rebuild(byPrice, bikes, BY_PRICE);
        rebuild(byCategory, bikes, BY_CATEGORY);
        rebuild(byStock, bikes, BY_STOCK);
    }

    private static void rebuild(List<Bike> target, List<Bike> source, Comparator<Bike> cmp) {
        target.clear();
        target.addAll(source);
        MergeSort.sort(target, cmp);
    }


    private static Bike probe(int id, String category, double price, int stock) {
        return new Bike(id, "probe", category, price, stock);
    }

    public Optional<Bike> findById(int id) {
        int i = BinarySearch.search(byId, probe(id, "", 0, 0), BY_ID);
        return i < 0 ? Optional.empty() : Optional.of(byId.get(i));
    }


    public List<Bike> priceRange(double min, double max) {
        int from = BinarySearch.lowerBound(byPrice, probe(0, "", Math.max(min, 0), 0), BY_PRICE);
        int to = BinarySearch.upperBound(byPrice, probe(0, "", Math.max(max, 0), 0), BY_PRICE);
        return from >= to ? List.of() : new ArrayList<>(byPrice.subList(from, to));
    }

    public List<Bike> byCategory(String category) {
        String cat = category.toUpperCase();
        int from = BinarySearch.lowerBound(byCategory, probe(0, cat, 0, 0), BY_CATEGORY);
        int to = BinarySearch.upperBound(byCategory, probe(0, cat, 0, 0), BY_CATEGORY);
        return new ArrayList<>(byCategory.subList(from, to));
    }


    public List<Bike> minStock(int minStock) {
        int from = BinarySearch.lowerBound(byStock, probe(0, "", 0, Math.max(minStock, 0)), BY_STOCK);
        return new ArrayList<>(byStock.subList(from, byStock.size()));
    }


    public List<Bike> priceRangeLinear(double min, double max) {
        List<Bike> result = new ArrayList<>();
        for (Bike b : byId) {
            if (b.getPrice() >= min && b.getPrice() <= max) result.add(b);
        }
        return result;
    }

    public Optional<Bike> findByIdLinear(int id) {
        for (Bike b : byId) {
            if (b.getId() == id) return Optional.of(b);
        }
        return Optional.empty();
    }
}
