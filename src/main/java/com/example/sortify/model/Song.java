package com.example.sortify.model;

public class Song {

    private final String id;
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private final String filePath;
    private final int durationSec;
    private int playCount = 0;

    public Song(String id, String title, String artist, String album,
                String genre, int durationSec, String filePath) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.durationSec = durationSec;
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public int getDurationSec() {
        return durationSec;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void incrementPlayCount() {
        playCount++;
    }
}
