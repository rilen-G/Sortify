package com.example.sortify.playback;

import com.example.sortify.model.Song;
import java.util.*;

public class PlaybackController {

    private final Queue<Song> queue = new ArrayDeque<>();
    private final Stack<Song> history = new Stack<>();
    private Song nowPlaying;

    public void enqueue(Song s){
        queue.offer(s);
    }

    public void enqueueAll(Collection<Song> songs){
        queue.addAll(songs);
    }

    public Optional<Song> playNext(){
        if (nowPlaying != null){
            history.push(nowPlaying);
        }
        if (nowPlaying == null && !queue.isEmpty()) {
            nowPlaying = queue.poll();
        } else {
            nowPlaying = queue.poll();
        }
        return Optional.ofNullable(nowPlaying);
    }

    public Optional<Song> playPrev(){
        if (!history.isEmpty()){
            if (nowPlaying != null) queue.add(nowPlaying);
            nowPlaying = history.pop();
            return Optional.of(nowPlaying);
        }
        return Optional.empty();
    }

    public Optional<Song> play(Song song) {
        if (song == null) {
            return Optional.empty();
        }
        if (nowPlaying != null) {
            history.push(nowPlaying);
        }
        nowPlaying = song;
        return Optional.of(song);
    }

    public Song current(){ return nowPlaying; }
    public Queue<Song> getQueue(){ return queue; }
    public Stack<Song> getHistory(){ return history; }
}

