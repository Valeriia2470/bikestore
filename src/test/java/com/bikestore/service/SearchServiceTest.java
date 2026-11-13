package com.bikestore.service;

import com.bikestore.model.Bike;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    private List<Bike> randomBikes(int n, long seed) {
        Random rnd = new Random(seed);
        String[] cats = {"ROAD", "CITY", "MOUNTAIN", "KIDS"};
        List<Bike> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            list.add(new Bike(i, "Bike " + i, cats[rnd.nextInt(cats.length)],
                    100 + rnd.nextInt(2000), rnd.nextInt(20)));
        }
        return list;
    }

    private Set<Integer> ids(List<Bike> bikes) {
        Set<Integer> s = new HashSet<>();
        for (Bike b : bikes) s.add(b.getId());
        return s;
    }

    @Test
    void priceRangeMatchesLinear() {
        SearchService s = new SearchService(randomBikes(2000, 1));
        assertEquals(ids(s.priceRangeLinear(500, 900)), ids(s.priceRange(500, 900)));
        assertEquals(ids(s.priceRangeLinear(0, 50)), ids(s.priceRange(0, 50)));       // пусто
        assertEquals(ids(s.priceRangeLinear(0, 99999)), ids(s.priceRange(0, 99999))); // все
    }

    @Test
    void categoryAndStock() {
        List<Bike> bikes = randomBikes(1000, 2);
        SearchService s = new SearchService(bikes);

        long road = bikes.stream().filter(b -> b.getCategory().equals("ROAD")).count();
        assertEquals(road, s.byCategory("road").size());

        long inStock = bikes.stream().filter(b -> b.getStock() >= 1).count();
        assertEquals(inStock, s.minStock(1).size());
    }

    @Test
    void findByIdWorks() {
        SearchService s = new SearchService(randomBikes(500, 3));
        assertEquals(250, s.findById(250).orElseThrow().getId());
        assertTrue(s.findById(9999).isEmpty());
    }
}
