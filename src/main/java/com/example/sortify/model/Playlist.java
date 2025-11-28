package com.example.sortify.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Playlist {

    private String name;
    private final LinkedList<Song> tracks = new LinkedList<>();

    public Playlist(String name) { this.name = name; }

    public void add(Song s) { if (!contains(s)) tracks.add(s); }
    public void addFirst(Song s) { if (!contains(s)) tracks.addFirst(s); }
    public void addAt(int idx, Song s) { if (!contains(s)) tracks.add(idx, s); }
    public void remove(Song s) { tracks.remove(s); }

    public void move(int from, int to){
        if (from < 0 || from >= tracks.size() || to < 0 || to > tracks.size()) return;
        Song s = tracks.remove(from);
        tracks.add(Math.min(to, tracks.size()), s);
    }

    public List<Song> tracks() { return Collections.unmodifiableList(tracks); }

    public String getName(){ return name; }
    public void setName(String n){ name = n; }

    public boolean contains(Song s){
        if (s == null) return false;
        return tracks.stream().anyMatch(track -> track.getId().equals(s.getId()));
    }
}
