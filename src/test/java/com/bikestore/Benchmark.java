package com.bikestore;

import com.bikestore.model.Bike;
import com.bikestore.service.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Benchmark {
    public static void main(String[] args) {
        int n = 100_000;
        Random rnd = new Random(42);
        List<Bike> bikes = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            bikes.add(new Bike(i, "Bike " + i, "CITY", 100 + rnd.nextInt(5000), rnd.nextInt(50)));
        }

        long t0 = System.nanoTime();
        SearchService s = new SearchService(bikes);
        System.out.printf("Index build: %.1f ms%n", (System.nanoTime() - t0) / 1e6);

        int queries = 2000;
        int[] ids = new int[queries];
        for (int i = 0; i < queries; i++) ids[i] = 1 + rnd.nextInt(n);


        for (int id : ids) { s.findById(id); s.findByIdLinear(id); }

        long sink = 0;
        t0 = System.nanoTime();
        for (int id : ids) sink += s.findByIdLinear(id).get().getId();
        double linear = (System.nanoTime() - t0) / 1e6;

        t0 = System.nanoTime();
        for (int id : ids) sink += s.findById(id).get().getId();
        double binary = (System.nanoTime() - t0) / 1e6;

        System.out.printf("ID lookup, %d queries: linear %.1f ms, binary %.2f ms (x%.0f faster)%n",
                queries, linear, binary, linear / binary);

        t0 = System.nanoTime();
        for (int i = 0; i < queries; i++) sink += s.priceRangeLinear(1000 + i, 1010 + i).size();
        linear = (System.nanoTime() - t0) / 1e6;

        t0 = System.nanoTime();
        for (int i = 0; i < queries; i++) sink += s.priceRange(1000 + i, 1010 + i).size();
        binary = (System.nanoTime() - t0) / 1e6;

        System.out.printf("Price range, %d queries: linear %.1f ms, binary %.2f ms (x%.0f faster)%n",
                queries, linear, binary, linear / binary);
        System.out.println(sink);
    }
}