package com.example.sortify.playback;

import com.example.sortify.model.Song;

import java.util.*;

public class PlaybackController {

    private final Deque<Song> queue = new ArrayDeque<>();
    private final Deque<Song> history = new ArrayDeque<>();
    private Song nowPlaying;

    public void enqueue(Song s){ queue.offer(s); }
    public void enqueueAll(Collection<Song> songs){ queue.addAll(songs); }

    public Optional<Song> playNext(){
        if (nowPlaying != null) history.push(nowPlaying);
        nowPlaying = queue.poll();
        return Optional.ofNullable(nowPlaying);
    }

    public Optional<Song> playPrev(){
        if (!history.isEmpty()){
            if (nowPlaying != null) queue.addFirst(nowPlaying);
            nowPlaying = history.pop();
            return Optional.of(nowPlaying);
        }
        return Optional.empty();
    }

    public Song current(){ return nowPlaying; }
    public Deque<Song> getQueue(){ return queue; }
    public Deque<Song> getHistory(){ return history; }
}

