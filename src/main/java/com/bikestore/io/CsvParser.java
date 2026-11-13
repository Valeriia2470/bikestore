package com.bikestore.io;

import com.bikestore.model.Bike;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvParser {
    private static final int COLUMNS = 5;

    public static ParseResult parse(Path path) throws IOException {
        return parseLines(Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    // Первая строка считается заголовком и пропускается
    public static ParseResult parseLines(List<String> lines) {
        List<Bike> bikes = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<Integer> seenIds = new HashSet<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) continue;
            int lineNo = i + 1;

            try {
                Bike bike = parseLine(line);
                if (!seenIds.add(bike.getId())) {
                    throw new ValidationException("duplicate id " + bike.getId());
                }
                bikes.add(bike);
            } catch (ValidationException | IllegalArgumentException e) {
                errors.add("Line " + lineNo + ": " + e.getMessage());
            }
        }
        return new ParseResult(bikes, errors);
    }

    private static Bike parseLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != COLUMNS) {
            throw new ValidationException("expected " + COLUMNS + " columns, got " + parts.length);
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
            if (parts[i].isEmpty()) {
                throw new ValidationException("empty field in column " + (i + 1));
            }
        }

        int id = parseInt(parts[0], "id");
        double price = parseDouble(parts[3], "price");
        int stock = parseInt(parts[4], "stock");


        return new Bike(id, parts[1], parts[2].toUpperCase(), price, stock);
    }

    private static int parseInt(String s, String field) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ValidationException("invalid " + field + ": '" + s + "'");
        }
    }

    private static double parseDouble(String s, String field) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new ValidationException("invalid " + field + ": '" + s + "'");
        }
    }
}
