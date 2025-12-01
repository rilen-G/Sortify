package com.example.sortify.stats;

import com.example.sortify.model.Song;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Scores songs for "For You" using weighted factors:
 *  - song play frequency (strongest)
 *  - genre affinity (next strongest)
 *  - freshness bonus (less-played songs get a nudge)
 *  - recency penalty (just-played songs sink slightly)
 */
public class RecommenderService {

    private static final double SONG_WEIGHT = 0.60;
    private static final double GENRE_WEIGHT = 0.25;
    private static final double FRESH_WEIGHT = 0.10;
    private static final double RECENCY_WEIGHT = 0.05;

    private record ScoredSong(Song song, double score) {}

    public List<Song> recommend(List<Song> library, List<Song> recentHistory, int limit){
        if (library == null || library.isEmpty()) return List.of();
        recentHistory = recentHistory == null ? List.of() : recentHistory;
        limit = Math.max(1, limit);

        Map<String, Integer> genreCounts = computeGenreCounts(library);
        int maxGenre = Math.max(1, genreCounts.values().stream().max(Integer::compareTo).orElse(0));
        int maxPlays = Math.max(1, library.stream().mapToInt(Song::getPlayCount).max().orElse(0));

        Map<String, Integer> recentIndex = indexRecent(recentHistory);

        List<ScoredSong> scored = new ArrayList<>(library.size());
        for (Song s : library){
            double songPlay = normalize(s.getPlayCount(), maxPlays);
            double genrePlay = normalize(genreCounts.getOrDefault(safeGenre(s), 0), maxGenre);
            double freshness = 1.0 - songPlay; // bonus for not being overplayed
            double recencyPenalty = recencyPenalty(recentIndex.get(s.getId()));

            double score = SONG_WEIGHT * songPlay
                    + GENRE_WEIGHT * genrePlay
                    + FRESH_WEIGHT * freshness
                    - RECENCY_WEIGHT * recencyPenalty;

            scored.add(new ScoredSong(s, score));
        }

        return scored.stream()
                .sorted((a, b) -> Double.compare(b.score(), a.score()))
                .limit(limit)
                .map(ScoredSong::song)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> computeGenreCounts(List<Song> library){
        Map<String, Integer> counts = new HashMap<>();
        for (Song s : library){
            String g = safeGenre(s);
            counts.merge(g, Math.max(0, s.getPlayCount()), Integer::sum);
        }
        return counts;
    }

    private Map<String, Integer> indexRecent(List<Song> recent){
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < recent.size(); i++){
            Song s = recent.get(i);
            if (s != null) idx.putIfAbsent(s.getId(), i); // keep first (most recent) occurrence
        }
        return idx;
    }

    private double normalize(int value, int max){
        if (max <= 0) return 0.0;
        return Math.min(1.0, (double)value / (double)max);
    }

    private double recencyPenalty(Integer idx){
        if (idx == null) return 0.0;
        // Most recent (idx 0) gets full penalty; fades quickly.
        return 1.0 / (1.0 + idx);
    }

    private String safeGenre(Song s){
        String g = s == null ? "" : s.getGenre();
        if (g == null || g.isBlank()) return "unknown";
        return g.toLowerCase(Locale.ROOT).trim();
    }
}
