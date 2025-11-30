package com.example.sortify.playback;

import com.example.sortify.model.Song;
import java.util.*;

public class PlaybackController {

    private final Deque<Song> queue = new ArrayDeque<>();
    private final Stack<Song> history = new Stack<>();
    private Song nowPlaying;
    private Runnable onChange;

    public void enqueue(Song s){
        if (s == null || isInQueue(s)) return;
        queue.offer(s);
        changed();
    }

    public void enqueueAll(Collection<Song> songs){
        if (songs == null) return;
        boolean added = false;
        for (Song s : songs){
            if (s == null || isInQueue(s)) continue;
            queue.offer(s);
            added = true;
        }
        if (!added) return;
        changed();
    }

    public Optional<Song> playNext(){
        if (nowPlaying == null){
            nowPlaying = queue.poll();
            changed();
            return Optional.ofNullable(nowPlaying);
        }
        history.push(nowPlaying);
        nowPlaying = queue.poll();
        changed();
        return Optional.ofNullable(nowPlaying);
    }

    public Optional<Song> playPrev(){
        if (!history.isEmpty()){
            if (nowPlaying != null) queue.addFirst(nowPlaying);
            nowPlaying = history.pop();
            changed();
            return Optional.of(nowPlaying);
        }
        return Optional.empty();
    }

    public void resetQueue(Collection<Song> songs){
        queue.clear();
        if (songs != null) queue.addAll(songs);
        changed();
    }

    public void replaceState(Song current, Collection<Song> queueItems, Collection<Song> historyItems){
        queue.clear();
        history.clear();
        if (queueItems != null) queue.addAll(queueItems);
        if (historyItems != null) history.addAll(historyItems);
        nowPlaying = current;
        changed();
    }

    public void setOnChange(Runnable cb){
        onChange = cb;
    }

    public Song current(){ return nowPlaying; }
    public Queue<Song> getQueue(){ return queue; }
    public Stack<Song> getHistory(){ return history; }

    private boolean isInQueue(Song s){
        if (s == null) return false;
        String id = s.getId();
        return queue.stream().anyMatch(song -> Objects.equals(song.getId(), id));
    }

    private void changed(){
        if (onChange != null){
            onChange.run();
        }
    }
}

