package com.example.sortify.algo;

import java.util.*;

public class Sorts {
    public static <T> void mergeSort(List<T> a, Comparator<T> cmp){
        if (a == null || a.size() < 2) {
            return;
        }
        List<T> temp = new ArrayList<>(a);
        mergeSortHelper(a, temp, 0, a.size() - 1, cmp);
    }

    private static <T> void mergeSortHelper(List<T> a, List<T> temp, int left, int right, Comparator<T> cmp) {
        if (left >= right) return;

        int mid = (left + right) / 2;
        mergeSortHelper(a, temp, left, mid, cmp);
        mergeSortHelper(a, temp, mid + 1, right, cmp);
        merge(a, temp, left, mid, right, cmp);
    }

    private static <T> void merge(List<T> a, List<T> temp, int left, int mid, int right, Comparator<T> cmp) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (cmp.compare(a.get(i), a.get(j)) <= 0) {
                temp.set(k++, a.get(i++));
            } else {
                temp.set(k++, a.get(j++));
            }
        }

        while (i <= mid) temp.set(k++, a.get(i++));
        while (j <= right) temp.set(k++, a.get(j++));

        for (int p = left; p <= right; p++) {
            a.set(p, temp.get(p));
        }
    }
    public static <T> void quickSort(List<T> a, Comparator<T> cmp){
        if (a == null || a.size() < 2) return;
        quickSortHelper(a, 0, a.size() - 1, cmp);
    }

    private static <T> void quickSortHelper(List<T> a, int low, int high, Comparator<T> cmp) {
        if (low < high) {
            int p = partition(a, low, high, cmp);
            quickSortHelper(a, low, p - 1, cmp);
            quickSortHelper(a, p + 1, high, cmp);
        }
    }

    private static <T> int partition(List<T> a, int low, int high, Comparator<T> cmp) {
        T pivot = a.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (cmp.compare(a.get(j), pivot) <= 0) {
                i++;
                Collections.swap(a, i, j);
            }
        }

        Collections.swap(a, i + 1, high);
        return i + 1;
    }
}

