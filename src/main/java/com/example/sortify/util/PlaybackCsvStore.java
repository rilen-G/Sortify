package com.example.sortify.util;

import com.example.sortify.model.Song;
import com.example.sortify.playback.PlaybackController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lightweight CSV persistence for the playback queue/history so the session can be restored on restart.
 */
public final class PlaybackCsvStore {

    private static final String HEADER = "section,songId";
    private static final String SECTION_CURRENT = "current";
    private static final String SECTION_QUEUE = "queue";
    private static final String SECTION_HISTORY = "history";

    public record State(Song current, List<Song> queue, List<Song> history) {}

    private PlaybackCsvStore() {}

    public static void save(Path file, PlaybackController playback) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            bw.write(HEADER);
            bw.newLine();
            Song current = playback.current();
            if (current != null) {
                bw.write(SECTION_CURRENT);
                bw.write(",");
                bw.write(escape(current.getId()));
                bw.newLine();
            }
            for (Song s : playback.getQueue()) {
                bw.write(SECTION_QUEUE);
                bw.write(",");
                bw.write(escape(s.getId()));
                bw.newLine();
            }
            for (Song s : playback.getHistory()) {
                bw.write(SECTION_HISTORY);
                bw.write(",");
                bw.write(escape(s.getId()));
                bw.newLine();
            }
        }
    }

    public static State load(Path file, Map<String, Song> library) throws IOException {
        if (!Files.exists(file)) return new State(null, List.of(), List.of());
        Song current = null;
        List<Song> queue = new ArrayList<>();
        List<Song> history = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                String section = parts[0];
                String songId = unescape(parts[1]);
                Song s = library.get(songId);
                if (s == null) continue;
                switch (section) {
                    case SECTION_CURRENT -> current = s;
                    case SECTION_QUEUE -> queue.add(s);
                    case SECTION_HISTORY -> history.add(s);
                }
            }
        }
        return new State(current, queue, history);
    }

    private static String escape(String v) {
        if (v == null) return "";
        return v.replace(",", ";");
    }

    private static String unescape(String v) {
        if (v == null) return "";
        return v.replace(";", ",");
    }
}
