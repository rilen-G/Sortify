package com.example.sortify.repo;

import com.example.sortify.model.Song;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LibraryRepository {
    // Master structures: fast ID lookups + sortable list
    private final HashMap<String, Song> byId = new HashMap<>();
    private final ArrayList<Song> library = new ArrayList<>();

    public ArrayList<Song> getLibrary(){ return library; }

    public HashMap<String, Song> byId(){ return byId; }

    public void loadFromCsv(Path csv) throws IOException {
        Path resourceSongsDir = null;
        try {
            resourceSongsDir = Paths.get(getClass()
                    .getResource("/com/example/sortify/songs").toURI());
        } catch (Exception ignored){}

        try (BufferedReader br = Files.newBufferedReader(csv)) {
            String line; br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] t = line.split(",", -1);
                // id,title,artist,album,genre,durationSec,filePath
                int duration = parseDuration(t[5]);
                String rawPath = t[6];
                String resolvedPath = rawPath;
                Path raw = Paths.get(rawPath);
                if (!raw.isAbsolute() && resourceSongsDir != null){
                    Path candidate = resourceSongsDir.resolve(rawPath);
                    if (Files.exists(candidate)){
                        resolvedPath = candidate.toAbsolutePath().toString();
                    }
                }
                Song s = new Song(t[0], t[1], t[2], t[3], t[4],
                        duration, resolvedPath);
                addOrReplace(s);
            }
        }
    }

    private int parseDuration(String token){
        if (token == null || token.isBlank()) return 0;
        if (token.contains(":")){
            String[] p = token.split(":");
            try {
                int minutes = Integer.parseInt(p[0]);
                int seconds = Integer.parseInt(p[1]);
                return minutes * 60 + seconds;
            } catch (NumberFormatException e){
                return 0;
            }
        }
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public void saveToCsv(Path csv) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(csv)) {
            bw.write("id,title,artist,album,genre,durationSec,filePath\n");
            for (Song s : library) {
                bw.write(String.join(",", escape(s.getId()), escape(s.getTitle()),
                        escape(s.getArtist()), escape(s.getAlbum()), escape(s.getGenre()),
                        String.valueOf(s.getDurationSec()),
                        escape(s.getFilePath())));
                bw.write("\n");
            }
        }
    }

    public void importFromFolder(Path folder) throws IOException {
        // Walk files; for mp3 create Song with filename as title fallback
        Files.walk(folder)
                .filter(p -> !Files.isDirectory(p))
                .filter(p -> {
                    String name = p.toString().toLowerCase();
                    return name.endsWith(".mp3") || name.endsWith(".mp4");
                })

                        .forEach(p -> {
                    String id = UUID.randomUUID().toString();
                    String title = p.getFileName().toString();
                    Song s = new Song(id, title, "Unknown", "Unknown", "Unknown", 0, p.toAbsolutePath().toString());
                    addOrReplace(s);
                });
    }

    private void addOrReplace(Song s){
        if (!byId.containsKey(s.getId())) library.add(s);
        byId.put(s.getId(), s);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", ";");
    }
}

