package com.bikestore.io;

import com.bikestore.model.Bike;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecordWriter {

    public static List<String> toCsvLines(List<Bike> bikes) {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,category,price,stock");
        for (Bike b : bikes) {
            lines.add(b.getId() + "," + b.getName() + "," + b.getCategory() + ","
                    + b.getPrice() + "," + b.getStock());
        }
        return lines;
    }

    public static List<String> toTextLines(List<Bike> bikes) {
        List<String> lines = new ArrayList<>();
        for (Bike b : bikes) {
            lines.add("id=" + b.getId());
            lines.add("name=" + b.getName());
            lines.add("category=" + b.getCategory());
            lines.add("price=" + b.getPrice());
            lines.add("stock=" + b.getStock());
            lines.add("");
        }
        return lines;
    }

    public static void writeCsv(Path path, List<Bike> bikes) throws IOException {
        Files.write(path, toCsvLines(bikes), StandardCharsets.UTF_8);
    }

    public static void writeText(Path path, List<Bike> bikes) throws IOException {
        Files.write(path, toTextLines(bikes), StandardCharsets.UTF_8);
    }
}