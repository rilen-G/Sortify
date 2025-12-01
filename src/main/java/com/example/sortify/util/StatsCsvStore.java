package com.example.sortify.util;

import com.example.sortify.model.Song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Persists song play counts to a simple CSV: songId,playCount.
 */
public final class StatsCsvStore {

    private static final String HEADER = "songId,playCount";

    private StatsCsvStore() {}

    public static Map<String, Integer> load(Path file) throws IOException {
        Map<String, Integer> counts = new HashMap<>();
        if (!Files.exists(file)) return counts;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                try {
                    counts.put(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException ignored) {}
            }
        }
        return counts;
    }

    public static void save(Path file, Collection<Song> songs) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            bw.write(HEADER);
            bw.newLine();
            for (Song s : songs) {
                if (s == null || s.getPlayCount() <= 0) continue;
                bw.write(s.getId());
                bw.write(",");
                bw.write(Integer.toString(s.getPlayCount()));
                bw.newLine();
            }
        }
    }
}
