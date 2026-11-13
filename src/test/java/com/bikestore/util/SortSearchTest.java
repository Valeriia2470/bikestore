package com.bikestore.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SortSearchTest {

    @Test
    void mergeSortMatchesCollectionsSort() {
        Random rnd = new Random(42);
        List<Integer> a = new ArrayList<>();
        for (int i = 0; i < 1000; i++) a.add(rnd.nextInt(500));
        List<Integer> b = new ArrayList<>(a);

        MergeSort.sort(a, Comparator.naturalOrder());
        Collections.sort(b);

        assertEquals(b, a);
    }

    @Test
    void binarySearchFindsElementsAndMisses() {
        List<Integer> list = new ArrayList<>(List.of(1, 3, 5, 7, 9, 11));
        Comparator<Integer> cmp = Comparator.naturalOrder();

        assertEquals(3, BinarySearch.search(list, 7, cmp));
        assertEquals(-1, BinarySearch.search(list, 4, cmp));
    }

    @Test
    void boundsGiveRange() {
        List<Integer> list = List.of(1, 3, 3, 3, 5, 7);
        Comparator<Integer> cmp = Comparator.naturalOrder();

        assertEquals(1, BinarySearch.lowerBound(list, 3, cmp));
        assertEquals(4, BinarySearch.upperBound(list, 3, cmp));
    }
}