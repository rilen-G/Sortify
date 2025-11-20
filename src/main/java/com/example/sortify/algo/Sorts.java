package com.example.sortify.algo;

import java.util.*;

public class Sorts {
    public static <T> void mergeSort(List<T> a, Comparator<T> cmp){
        if (a == null || a.size() < 2) return;

        @SuppressWarnings("unchecked")
        T[] buffer = (T[]) new Object[a.size()];
        mergeSort(a, cmp, 0, a.size() - 1, buffer);
    }
    public static <T> void quickSort(List<T> a, Comparator<T> cmp){
        if (a == null || a.size() < 2) return;
        quickSort(a, cmp, 0, a.size() - 1);
    }

    private static <T> void mergeSort(List<T> a, Comparator<T> cmp, int left, int right, T[] buffer){
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(a, cmp, left, mid, buffer);
        mergeSort(a, cmp, mid + 1, right, buffer);
        merge(a, cmp, left, mid, right, buffer);
    }

    private static <T> void merge(List<T> a, Comparator<T> cmp, int left, int mid, int right, T[] buffer){
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right){
            if (cmp.compare(a.get(i), a.get(j)) <= 0){
                buffer[k++] = a.get(i++);
            } else {
                buffer[k++] = a.get(j++);
            }
        }

        while (i <= mid){
            buffer[k++] = a.get(i++);
        }
        while (j <= right){
            buffer[k++] = a.get(j++);
        }

        for (int idx = left; idx <= right; idx++){
            a.set(idx, buffer[idx]);
        }
    }

    private static <T> void quickSort(List<T> a, Comparator<T> cmp, int low, int high){
        if (low >= high) return;
        int p = partition(a, cmp, low, high);
        quickSort(a, cmp, low, p - 1);
        quickSort(a, cmp, p + 1, high);
    }

    private static <T> int partition(List<T> a, Comparator<T> cmp, int low, int high){
        T pivot = a.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++){
            if (cmp.compare(a.get(j), pivot) <= 0){
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(List<T> a, int i, int j){
        if (i == j) return;
        T tmp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, tmp);
    }
}

