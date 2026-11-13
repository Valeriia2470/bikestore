package com.bikestore.io;

import com.bikestore.model.Bike;
import java.util.List;

public record ParseResult(List<Bike> bikes, List<String> errors) {
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
