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
        try (BufferedReader br = Files.newBufferedReader(csv)) {
            String line; br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] t = line.split(",", -1);
                // id,title,artist,album,genre,durationSec,filePath
                Song s = new Song(t[0], t[1], t[2], t[3], t[4],
                        Integer.parseInt(t[5]), t[6]);
                addOrReplace(s);
            }
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
                .filter(p -> p.toString().toLowerCase().endsWith(".mp4"))

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

