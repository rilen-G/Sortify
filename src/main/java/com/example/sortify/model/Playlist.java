package com.example.sortify.model;

import java.util.LinkedList;

public class Playlist {

    private String name;

    private final LinkedList<Song> tracks = new LinkedList<>();
    public Playlist(String name) {
        this.name = name;
    }

    public void add(Song s){
        tracks.add(s);
    }

    public void addFirst(Song s){
        tracks.addFirst(s);
    }

    public void addAt(int idx, Song s){
        tracks.add(idx, s);
    }

    public void remove(Song s){
        tracks.remove(s);
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
}
