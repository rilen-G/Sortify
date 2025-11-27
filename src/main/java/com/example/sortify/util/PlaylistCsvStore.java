package com.example.sortify.util;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistCsvStore {

    private static final String HEADER = "playlistName,songId";

    public static void save(Path file, List<Playlist> playlists) throws IOException {
        if (playlists == null) return;
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            bw.write(HEADER);
            bw.newLine();
            for (Playlist pl : playlists) {
                for (Song s : pl.tracks()) {
                    bw.write(escape(pl.getName()));
                    bw.write(",");
                    bw.write(escape(s.getId()));
                    bw.newLine();
                }
            }
        }
    }

    public static Map<String, Playlist> load(Path file, Map<String, Song> library) throws IOException {
        Map<String, Playlist> byName = new HashMap<>();
        if (!Files.exists(file)) return byName;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 2) continue;
                String plName = unescape(parts[0]);
                String songId = unescape(parts[1]);
                Song s = library.get(songId);
                if (s == null) continue;
                Playlist pl = byName.computeIfAbsent(plName, Playlist::new);
                pl.add(s);
            }
        }
        return byName;
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

