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

    public void loadFromCsv(InputStream csvStream) throws IOException {
        Path temp = Files.createTempFile("sortify_songs", ".csv");
        temp.toFile().deleteOnExit();
        Files.copy(csvStream, temp, StandardCopyOption.REPLACE_EXISTING);
        loadFromCsv(temp);
    }

    public void loadFromCsv(Path csv) throws IOException {
        Path resourceSongsDir = null;
        try {
            resourceSongsDir = Paths.get(getClass()
                    .getResource("/com/example/sortify/songs").toURI());
        } catch (Exception ignored){}

        try (BufferedReader br = Files.newBufferedReader(csv)) {
            String line; br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue; // skip empty rows
                String[] t = line.split(",", -1);
                if (t.length < 7) continue; // guard against malformed rows
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

    private void addOrReplace(Song s){
        if (!byId.containsKey(s.getId())) library.add(s);
        byId.put(s.getId(), s);
    }
}
