package com.bikestore.controller;

import com.bikestore.io.CsvParser;
import com.bikestore.io.ParseResult;
import com.bikestore.io.RecordWriter;
import com.bikestore.io.TextParser;
import com.bikestore.model.Bike;
import com.bikestore.service.InventoryService;
import com.bikestore.service.SearchService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {

    public record Filter(String category, Double minPrice, Double maxPrice, boolean inStockOnly) {
        public static final Filter NONE = new Filter(null, null, null, false);
    }

    private final InventoryService inventory = new InventoryService(List.of());
    private final ObservableList<Bike> visible = FXCollections.observableArrayList();
    private Filter filter = Filter.NONE;
    private List<String> lastErrors = List.of();


    public ObservableList<Bike> visibleBikes() { return visible; }

    public InventoryService service() { return inventory; }


    public List<String> lastErrors() { return lastErrors; }

    public ParseResult load(Path path) throws IOException {
        ParseResult result = isCsv(path) ? CsvParser.parse(path) : TextParser.parse(path);
        inventory.replaceAll(result.bikes());
        lastErrors = result.errors();
        refresh();
        return result;
    }

    public void save(Path path) throws IOException {
        if (isCsv(path)) RecordWriter.writeCsv(path, inventory.all());
        else RecordWriter.writeText(path, inventory.all());
    }

    public void add(Bike bike) {
        inventory.add(bike);
        refresh();
    }

    public boolean remove(int id) {
        boolean removed = inventory.remove(id);
        refresh();
        return removed;
    }

    public void applyFilter(Filter f) {
        filter = f;
        refresh();
    }


    public void refresh() {
        SearchService s = inventory.search();
        Filter f = filter;
        boolean hasPrice = f.minPrice() != null || f.maxPrice() != null;
        boolean hasCat = f.category() != null && !f.category().isBlank();


        List<Bike> base;
        if (hasPrice) {
            double min = f.minPrice() == null ? 0 : f.minPrice();
            double max = f.maxPrice() == null ? Double.MAX_VALUE : f.maxPrice();
            base = s.priceRange(min, max);
        } else if (hasCat) {
            base = s.byCategory(f.category());
        } else if (f.inStockOnly()) {
            base = s.minStock(1);
        } else {
            base = new ArrayList<>(inventory.all());
        }

        List<Bike> result = new ArrayList<>();
        for (Bike b : base) {
            if (hasCat && !b.getCategory().equalsIgnoreCase(f.category().trim())) continue;
            if (f.inStockOnly() && b.getStock() < 1) continue;
            result.add(b);
        }
        visible.setAll(result);
    }

    private static boolean isCsv(Path path) {
        return path.toString().toLowerCase().endsWith(".csv");
    }
}