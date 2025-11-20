package com.example.sortify.stats;

import com.example.sortify.model.Song;
import java.util.*;

public class StatsService {

    private final PriorityQueue<Song> topPlayed =
            new PriorityQueue<>((a,b) -> Integer.compare(b.getPlayCount(), a.getPlayCount()));

    public List<Song> topN(Collection<Song> all, int n) {
        topPlayed.clear();
        topPlayed.addAll(all);

        List<Song> result = new ArrayList<>();

        for (int i = 0; i < n && !topPlayed.isEmpty(); i++) {
            result.add(topPlayed.poll());
        }

        return result;
    }
}

