package com.example.sortify.stats;

import com.example.sortify.model.Song;
import java.util.*;

public class StatsService {

    public List<Song> topN(Collection<Song> all, int n) {
        if (all == null || n <= 0) return List.of();

        // Local priority queue, max-heap by play count, tie-break by title
        PriorityQueue<Song> pq = new PriorityQueue<>(
                (a, b) -> {
                    int cmp = Integer.compare(b.getPlayCount(), a.getPlayCount());
                    if (cmp != 0) return cmp;
                    return a.getTitle().compareToIgnoreCase(b.getTitle());
                }
        );
        pq.addAll(all);

        List<Song> result = new ArrayList<>();
        for (int i = 0; i < n && !pq.isEmpty(); i++) {
            result.add(pq.poll());
        }

        return result;
    }
}

