package com.example.sortify.playback;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;

import java.util.*;
import java.util.stream.Collectors;

public class PlaybackController {

    private final Deque<Song> queue = new ArrayDeque<>();
    private final Stack<Song> history = new Stack<>();
    private Song nowPlaying;
    private Runnable onChange;
    private Playlist loopPlaylist; // active playlist loop (Play All) if any
    private boolean shuffleLoop = false;
    private final Set<String> shuffleServed = new HashSet<>();

    public void enqueue(Song s){
        if (s == null) return;
        boolean loopBroken = breakLoopIfExternal(List.of(s));
        if (isInQueue(s)) {
            if (loopBroken) changed();
            return;
        }
        queue.offer(s);
        changed();
    }

    public void enqueueAll(Collection<Song> songs){
        if (songs == null) return;
        boolean loopBroken = breakLoopIfExternal(songs);
        boolean added = false;
        for (Song s : songs){
            if (s == null || isInQueue(s)) continue;
            queue.offer(s);
            added = true;
        }
        if (!added && !loopBroken) return;
        changed();
    }

    public Optional<Song> startPlaylistLoop(Playlist playlist, boolean shuffle){
        if (playlist == null || playlist.tracks().isEmpty()) return Optional.empty();
        if (nowPlaying != null){
            history.push(nowPlaying);
        }
        loopPlaylist = playlist;
        shuffleLoop = shuffle;
        shuffleServed.clear();
        queue.clear();
        nowPlaying = null;
        if (shuffle){
            var copy = new ArrayList<>(playlist.tracks());
            java.util.Collections.shuffle(copy);
            queue.addAll(copy);
        } else {
            queue.addAll(playlist.tracks());
        }
        return playNext();
    }

    public Optional<Song> playNext(){
        if (nowPlaying != null){
            history.push(nowPlaying);
        }
        nowPlaying = pullNextFromQueue();
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
        clearLoopState();
        queue.clear();
        if (songs != null) queue.addAll(songs);
        changed();
    }

    public void replaceState(Song current, Collection<Song> queueItems, Collection<Song> historyItems){
        clearLoopState();
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

    /**
     * When actively looping a playlist, allow switching shuffle on/off mid-loop.
     * Rebuilds the queue based on the current song so playback continues smoothly.
     */
    public void updateLoopShuffle(boolean shuffle){
        shuffleLoop = shuffle;
        if (!isLooping() || loopPlaylist.tracks().isEmpty()) {
            changed();
            return;
        }
        if (shuffleLoop){
            refillShuffleQueue();
        } else {
            shuffleServed.clear();
            rebuildOrderedQueue();
            changed();
        }
    }

    private Song pullNextFromQueue(){
        Song next = queue.poll();
        if (next == null && isLooping()){
            if (loopPlaylist.tracks().isEmpty()){
                clearLoopState();
                return null;
            }
            if (shuffleLoop){
                refillShuffleQueue();
            } else {
                queue.addAll(loopPlaylist.tracks());
            }
            next = queue.poll();
        }
        if (shuffleLoop && next != null){
            shuffleServed.add(next.getId());
        }
        return next;
    }

    private boolean breakLoopIfExternal(Collection<Song> incoming){
        if (!isLooping() || incoming == null) return false;
        boolean hasExternal = incoming.stream()
                .filter(Objects::nonNull)
                .anyMatch(s -> !belongsToLoopPlaylist(s));
        if (!hasExternal) return false;
        boolean removed = queue.removeIf(this::belongsToLoopPlaylist);
        clearLoopState();
        return removed;
    }

    private boolean belongsToLoopPlaylist(Song s){
        return s != null && loopPlaylist != null && loopPlaylist.contains(s);
    }

    private boolean isLooping(){
        return loopPlaylist != null;
    }

    private void clearLoopState(){
        loopPlaylist = null;
        shuffleLoop = false;
        shuffleServed.clear();
    }

    private void refillShuffleQueue(){
        if (loopPlaylist == null) return;
        var tracks = new ArrayList<>(loopPlaylist.tracks());
        // Remaining excludes the current song
        tracks.remove(nowPlaying);
        List<Song> remaining = tracks.stream()
                .filter(Objects::nonNull)
                .filter(s -> !shuffleServed.contains(s.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        // If we've played all songs in this cycle, reset and start a new cycle
        if (remaining.isEmpty()){
            shuffleServed.clear();
            remaining = tracks.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        Collections.shuffle(remaining);
        queue.clear();
        queue.addAll(remaining);
        changed();
    }

    private void rebuildOrderedQueue(){
        if (loopPlaylist == null) return;
        var tracks = new ArrayList<>(loopPlaylist.tracks());
        queue.clear();
        if (nowPlaying == null){
            queue.addAll(tracks);
            return;
        }
        int idx = tracks.indexOf(nowPlaying);
        for (int i = 1; i <= tracks.size(); i++){
            Song s = tracks.get((idx + i) % tracks.size());
            queue.add(s);
        }
    }

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

