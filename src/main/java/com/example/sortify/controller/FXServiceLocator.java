package com.example.sortify.controller;

import com.example.sortify.algo.Sorts;
import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.playback.AudioEngine;
import com.example.sortify.playback.PlaybackController;
import com.example.sortify.repo.LibraryRepository;
import com.example.sortify.stats.StatsService;
import com.example.sortify.util.UndoManager;
import com.example.sortify.util.PlaylistCsvStore;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

public class FXServiceLocator {

    private static MainController main;

    private static final LibraryRepository library = new LibraryRepository();
    private static final ObservableList<Song> libraryView = FXCollections.observableArrayList();
    private static final PlaybackController playback = new PlaybackController();
    private static final AudioEngine audio = new AudioEngine();
    private static final StatsService stats = new StatsService();
    private static final UndoManager undo = new UndoManager();
    private static final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
    private static final ObjectProperty<Playlist> activePlaylist = new SimpleObjectProperty<>();
    private static LibraryController libraryController;
    private static PlaylistController playlistController;
    private static final Path playlistCsv = Paths.get("playlists.csv");

    public static void bootstrap(MainController m){
        main = m;
        try {
            Path csv = Paths.get(FXServiceLocator.class
                    .getResource("/com/example/sortify/data/song_list.csv").toURI());
            library.loadFromCsv(csv);
            libraryView.setAll(library.getLibrary());
            Sorts.mergeSort(libraryView, Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER));

            // Load playlists from csv if present
            var loaded = PlaylistCsvStore.load(playlistCsv, library.byId());
            if (!loaded.isEmpty()){
                playlists.setAll(loaded.values());
                activePlaylist.set(playlists.get(0));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (playlists.isEmpty()){
            Playlist purpleRadio = new Playlist("Purple Radio");
            if (!libraryView.isEmpty()){
                int pick = Math.min(8, libraryView.size());
                for (int i = 0; i < pick; i++){
                    purpleRadio.add(libraryView.get(i));
                }
            }
            playlists.add(purpleRadio);
            activePlaylist.set(purpleRadio);
        }
    }

    public static void setMainController(MainController m) {
        main = m;
    }

    public static MainController getMain() {
        return main;
    }

    public static LibraryRepository library(){
        return library;
    }

    public static ObservableList<Song> libraryView(){
        return libraryView;
    }

    public static ObservableList<Playlist> playlists(){
        return playlists;
    }

    public static void savePlaylists(){
        try {
            PlaylistCsvStore.save(playlistCsv, playlists);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static PlaybackController playback(){
        return playback;
    }

    public static AudioEngine audio(){
        return audio;
    }

    public static StatsService stats(){
        return stats;
    }

    public static UndoManager undo(){
        return undo;
    }

    public static void setActivePlaylist(Playlist p){
        activePlaylist.set(p);
    }

    public static Playlist activePlaylist(){
        return activePlaylist.get();
    }

    public static ObjectProperty<Playlist> activePlaylistProperty(){
        return activePlaylist;
    }

    public static void registerLibraryController(LibraryController c){
        libraryController = c;
    }

    public static Optional<LibraryController> libraryController(){
        return Optional.ofNullable(libraryController);
    }

    public static void registerPlaylistController(PlaylistController c){
        playlistController = c;
    }

    public static Optional<PlaylistController> playlistController(){
        return Optional.ofNullable(playlistController);
    }
}
