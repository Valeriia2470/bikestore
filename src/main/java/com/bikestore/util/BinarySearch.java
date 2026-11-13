package com.bikestore.util;

import java.util.Comparator;
import java.util.List;

public class BinarySearch {


    public static <T> int search(List<T> sorted, T key, Comparator<? super T> cmp) {
        int lo = 0, hi = sorted.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = cmp.compare(sorted.get(mid), key);
            if (c == 0) return mid;
            if (c < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }


    public static <T> int lowerBound(List<T> sorted, T key, Comparator<? super T> cmp) {
        int lo = 0, hi = sorted.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (cmp.compare(sorted.get(mid), key) < 0) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }


    public static <T> int upperBound(List<T> sorted, T key, Comparator<? super T> cmp) {
        int lo = 0, hi = sorted.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (cmp.compare(sorted.get(mid), key) <= 0) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
