package com.bikestore.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSort {

    public static <T> void sort(List<T> list, Comparator<? super T> cmp) {
        if (list.size() < 2) return;
        List<T> sorted = mergeSort(new ArrayList<>(list), cmp);
        for (int i = 0; i < sorted.size(); i++) {
            list.set(i, sorted.get(i));
        }
    }

    private static <T> List<T> mergeSort(List<T> list, Comparator<? super T> cmp) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        List<T> left = mergeSort(new ArrayList<>(list.subList(0, mid)), cmp);
        List<T> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())), cmp);
        return merge(left, right, cmp);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<? super T> cmp) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {

            if (cmp.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }
}
