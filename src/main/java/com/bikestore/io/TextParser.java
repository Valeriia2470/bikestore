package com.bikestore.io;

import com.bikestore.model.Bike;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TextParser {

    public static ParseResult parse(Path path) throws IOException {
        return parseLines(Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    public static ParseResult parseLines(List<String> lines) {
        List<Bike> bikes = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<Integer> seenIds = new HashSet<>();

        Map<String, String> block = new HashMap<>();
        int blockNo = 0;

        for (int i = 0; i <= lines.size(); i++) {
            boolean end = (i == lines.size());
            String line = end ? "" : lines.get(i).trim();

            if (!line.isEmpty()) {
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    errors.add("Line " + (i + 1) + ": expected key=value");
                } else {
                    block.put(line.substring(0, eq).trim(), line.substring(eq + 1).trim());
                }
                continue;
            }


            if (block.isEmpty()) continue;
            blockNo++;
            try {
                Bike bike = buildBike(block);
                if (!seenIds.add(bike.getId())) {
                    throw new ValidationException("duplicate id " + bike.getId());
                }
                bikes.add(bike);
            } catch (ValidationException | IllegalArgumentException e) {
                errors.add("Record " + blockNo + ": " + e.getMessage());
            }
            block = new HashMap<>();
        }
        return new ParseResult(bikes, errors);
    }

    private static Bike buildBike(Map<String, String> m) {
        for (String key : List.of("id", "name", "category", "price", "stock")) {
            String v = m.get(key);
            if (v == null || v.isEmpty()) throw new ValidationException("missing field '" + key + "'");
        }
        try {
            return new Bike(
                    Integer.parseInt(m.get("id")),
                    m.get("name"),
                    m.get("category").toUpperCase(),
                    Double.parseDouble(m.get("price")),
                    Integer.parseInt(m.get("stock")));
        } catch (NumberFormatException e) {
            throw new ValidationException("invalid number: " + e.getMessage());
        }
    }
}