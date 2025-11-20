package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.playback.AudioEngine;
import com.example.sortify.playback.PlaybackController;
import com.example.sortify.repo.LibraryRepository;
import com.example.sortify.stats.StatsService;
import com.example.sortify.util.UndoManager;
import com.example.sortify.controller.LibraryController;
import com.example.sortify.controller.PlaylistController;
import com.example.sortify.controller.PlayerBarController;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FXServiceLocator {

    private static MainController main;
    private static final LibraryRepository libraryRepository = new LibraryRepository();
    private static final PlaybackController playbackController = new PlaybackController();
    private static final AudioEngine audioEngine = new AudioEngine();
    private static final StatsService statsService = new StatsService();
    private static final UndoManager undoManager = new UndoManager();
    private static final List<Playlist> playlists = new ArrayList<>();
    private static Playlist currentPlaylist;
    private static LibraryController libraryController;
    private static PlaylistController playlistController;
    private static PlayerBarController playerBarController;

    public static void bootstrap() {
        if (!playlists.isEmpty()) return;
        try {
            var resource = FXServiceLocator.class.getResource("/com/example/sortify/data/song_list.csv");
            if (resource != null) {
                Path csv = Path.of(resource.toURI());
                libraryRepository.loadFromCsv(csv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Playlist defaultPl = new Playlist("Favorites");
        playlists.add(defaultPl);
        currentPlaylist = defaultPl;
    }

    public static void setMainController(MainController m) {
        main = m;
    }

    public static MainController getMain() {
        return main;
    }

    public static LibraryRepository getLibraryRepository() {
        return libraryRepository;
    }

    public static PlaybackController getPlaybackController() {
        return playbackController;
    }

    public static AudioEngine getAudioEngine() {
        return audioEngine;
    }

    public static StatsService getStatsService() {
        return statsService;
    }

    public static UndoManager getUndoManager() {
        return undoManager;
    }

    public static List<Playlist> getPlaylists() {
        return playlists;
    }

    public static Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public static void setCurrentPlaylist(Playlist playlist) {
        currentPlaylist = playlist;
    }

    public static void setLibraryController(LibraryController controller) {
        libraryController = controller;
    }

    public static LibraryController getLibraryController() {
        return libraryController;
    }

    public static void setPlaylistController(PlaylistController controller) {
        playlistController = controller;
    }

    public static PlaylistController getPlaylistController() {
        return playlistController;
    }

    public static void setPlayerBarController(PlayerBarController controller) {
        playerBarController = controller;
    }

    public static PlayerBarController getPlayerBarController() {
        return playerBarController;
    }
}

