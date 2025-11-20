package com.example.sortify.algo;

import java.util.*;

public class Search {
    public static <T> int binarySearch(List<T> list, T key, Comparator<T> comparator) {

        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;

            // Compare the middle element with the key
            int result = comparator.compare(list.get(mid), key);

            if (result == 0) {
                // Key found
                return mid;
            }

            if (result < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        // Key not found
        return -1;
    }
}


