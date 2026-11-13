package com.bikestore.io;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {
    @Test
    void parsesValidAndCollectsErrors() {
        List<String> lines = List.of(
                "id,name,category,price,stock",
                "1,Trek,MOUNTAIN,549.99,12",
                "2,Giant,CITY,abc,7",
                "3,Short,CITY,100",
                "4,Neg,ROAD,-5,1",
                "1,Dup,CITY,100,1"
        );
        ParseResult r = CsvParser.parseLines(lines);

        assertEquals(1, r.bikes().size());
        assertEquals(4, r.errors().size());
        assertTrue(r.errors().get(0).startsWith("Line 3"));
    }
}