package com.example.sortify.model;

import java.util.LinkedList;

public class Playlist {

    private String name;

    private final LinkedList<Song> tracks = new LinkedList<>();
    public Playlist(String name) {
        this.name = name;
    }

    public void add(Song s){
        if (contains(s)) return;
        tracks.add(s);
    }

    public void addFirst(Song s){
        if (contains(s)) return;
        tracks.addFirst(s);
    }

    public void addAt(int idx, Song s){
        if (contains(s)) return;
        tracks.add(idx, s);
    }

    public void remove(Song s){
        tracks.remove(s);
    }

    public void move(int from, int to){
        if (from < 0 || from >= tracks.size() || to < 0 || to >= tracks.size()) return;
        Song s = tracks.remove(from);
        tracks.add(to, s);
    }

    public LinkedList<Song> tracks(){
        return tracks;
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }

    public boolean contains(Song s){
        if (s == null) return false;
        for (Song track : tracks){
            if (track.getId().equals(s.getId())) return true;
        }
        return false;
    }
}
