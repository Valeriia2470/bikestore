package com.bikestore.io;

import com.bikestore.model.Bike;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundTripTest {

    private final List<Bike> original = List.of(
            new Bike(1, "Trek Marlin 5", "MOUNTAIN", 549.99, 12),
            new Bike(2, "Giant Escape 3", "CITY", 429.0, 7),
            new Bike(3, "Specialized Allez", "ROAD", 1099.5, 0)
    );

    private void assertSame(List<Bike> expected, List<Bike> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Bike e = expected.get(i), a = actual.get(i);
            assertEquals(e.getId(), a.getId());
            assertEquals(e.getName(), a.getName());
            assertEquals(e.getCategory(), a.getCategory());
            assertEquals(e.getPrice(), a.getPrice(), 1e-9);
            assertEquals(e.getStock(), a.getStock());
        }
    }

    @Test
    void csvRoundTrip() {
        ParseResult r = CsvParser.parseLines(RecordWriter.toCsvLines(original));
        assertFalse(r.hasErrors());
        assertSame(original, r.bikes());
    }

    @Test
    void textRoundTrip() {
        ParseResult r = TextParser.parseLines(RecordWriter.toTextLines(original));
        assertFalse(r.hasErrors());
        assertSame(original, r.bikes());
    }

    @Test
    void textParserReportsBadRecords() {
        List<String> lines = List.of(
                "id=1", "name=Ok", "category=CITY", "price=100", "stock=5", "",
                "id=2", "name=NoPrice", "category=CITY", "stock=5", "",
                "id=x", "name=BadId", "category=CITY", "price=10", "stock=1"
        );
        ParseResult r = TextParser.parseLines(lines);
        assertEquals(1, r.bikes().size());
        assertEquals(2, r.errors().size());
    }
}