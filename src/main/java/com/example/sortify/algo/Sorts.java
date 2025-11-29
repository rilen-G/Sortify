package com.example.sortify.algo;

import java.util.*;

public class Sorts {
    public static <T> void mergeSort(List<T> list, Comparator<T> cmp){
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(cmp, "comparator");
        if (list.size() < 2) return;

        List<T> temp = new ArrayList<>(list);
        mergeSort(list, temp, 0, list.size() - 1, cmp);
    }

    public static <T> void quickSort(List<T> list, Comparator<T> cmp){
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(cmp, "comparator");
        if (list.size() < 2) return;
        quickSort(list, 0, list.size() - 1, cmp);
    }

    private static <T> void mergeSort(List<T> list, List<T> temp, int left, int right, Comparator<T> cmp){
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(list, temp, left, mid, cmp);
        mergeSort(list, temp, mid + 1, right, cmp);
        merge(list, temp, left, mid, right, cmp);
    }

    // Copy to temp then merge back so ObservableList updates in-place and stays stable.
    private static <T> void merge(List<T> list, List<T> temp, int left, int mid, int right, Comparator<T> cmp){
        for (int i = left; i <= right; i++){
            temp.set(i, list.get(i));
        }
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right){
            if (cmp.compare(temp.get(i), temp.get(j)) <= 0){
                list.set(k++, temp.get(i++));
            } else {
                list.set(k++, temp.get(j++));
            }
        }
        while (i <= mid){
            list.set(k++, temp.get(i++));
        }
        while (j <= right){
            list.set(k++, temp.get(j++));
        }
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> cmp){
        if (low >= high) return;
        int pivotIndex = partition(list, low, high, cmp);
        quickSort(list, low, pivotIndex - 1, cmp);
        quickSort(list, pivotIndex + 1, high, cmp);
    }

    // Lomuto partition for clarity
    private static <T> int partition(List<T> list, int low, int high, Comparator<T> cmp){
        T pivot = list.get(high);
        int i = low;
        for (int j = low; j < high; j++){
            if (cmp.compare(list.get(j), pivot) <= 0){
                swap(list, i, j);
                i++;
            }
        }
        swap(list, i, high);
        return i;
    }

    private static <T> void swap(List<T> list, int i, int j){
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }
}

