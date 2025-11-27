package com.example.sortify.util;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.model.UserAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Reusable reversible actions to feed the UndoManager stacks.
 */
public final class Actions {
    private Actions() {}

    public static class AddToPlaylist implements UserAction {
        private final Playlist playlist;
        private final List<Song> songs;

        public AddToPlaylist(Playlist playlist, List<Song> songs) {
            this.playlist = playlist;
            this.songs = new ArrayList<>(songs);
        }

        @Override
        public void doIt() {
            songs.forEach(playlist::add);
        }

        @Override
        public void undo() {
            songs.forEach(playlist::remove);
        }

        @Override
        public String label() {
            return "Add to playlist";
        }
    }

    public static class RemoveFromPlaylist implements UserAction {
        private final Playlist playlist;
        private final List<Song> songs;

        public RemoveFromPlaylist(Playlist playlist, List<Song> songs) {
            this.playlist = playlist;
            this.songs = new ArrayList<>(songs);
        }

        @Override
        public void doIt() {
            songs.forEach(playlist::remove);
        }

        @Override
        public void undo() {
            songs.forEach(playlist::addFirst);
        }

        @Override
        public String label() {
            return "Remove from playlist";
        }
    }

    public static class RenamePlaylist implements UserAction {
        private final Playlist playlist;
        private final String oldName;
        private final String newName;

        public RenamePlaylist(Playlist playlist, String oldName, String newName) {
            this.playlist = playlist;
            this.oldName = oldName;
            this.newName = newName;
        }

        @Override
        public void doIt() {
            playlist.setName(newName);
        }

        @Override
        public void undo() {
            playlist.setName(oldName);
        }

        @Override
        public String label() {
            return "Rename playlist";
        }
    }

    public static class MoveInPlaylist implements UserAction {
        private final Playlist playlist;
        private final Song song;
        private final int fromIdx;
        private final int toIdx;

        public MoveInPlaylist(Playlist playlist, Song song, int fromIdx, int toIdx) {
            this.playlist = playlist;
            this.song = song;
            this.fromIdx = fromIdx;
            this.toIdx = toIdx;
        }

        @Override
        public void doIt() {
            playlist.move(fromIdx, toIdx);
        }

        @Override
        public void undo() {
            playlist.move(toIdx, fromIdx);
        }

        @Override
        public String label() {
            return "Move track";
        }
    }
}
