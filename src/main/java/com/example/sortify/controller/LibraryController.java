package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.playback.PlaybackController;
import com.example.sortify.util.Actions;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LibraryController {

    @FXML
    private TableView<Song> table;
    @FXML
    private TableColumn<Song, Number> colNum;
    @FXML
    private TableColumn<Song, String> colTitle;
    @FXML
    private TableColumn<Song, String> colArtist;
    @FXML
    private TableColumn<Song, String> colAlbum;
    @FXML
    private TableColumn<Song, String> colGenre;
    @FXML
    private TableColumn<Song, String> colDuration;

    private FilteredList<Song> filtered;

    @FXML
    public void initialize() {
        FXServiceLocator.registerLibraryController(this);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        filtered = new FilteredList<>(FXServiceLocator.libraryView(), s -> true);
        table.setItems(filtered);

        colNum.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cell.getValue()) + 1));
        colTitle.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));
        colArtist.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getArtist()));
        colAlbum.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAlbum()));
        colGenre.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGenre()));
        colDuration.setCellValueFactory(cell ->
                new SimpleStringProperty(formatDuration(cell.getValue().getDurationSec())));
    }

    @FXML
    private void addToQueue() {
        List<Song> selection = selectedSongs();
        if (selection.isEmpty()) return;
        PlaybackController playback = FXServiceLocator.playback();
        playback.enqueueAll(selection);
    }

    @FXML
    private void addToPlaylist() {
        Playlist target = FXServiceLocator.activePlaylist();
        if (target == null) {
            target = new Playlist("New Playlist");
            FXServiceLocator.playlists().add(target);
            FXServiceLocator.setActivePlaylist(target);
        }
        List<Song> selection = selectedSongs();
        if (selection.isEmpty()) return;
        FXServiceLocator.undo().execute(new Actions.AddToPlaylist(target, selection));
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
        FXServiceLocator.savePlaylists();
    }

    public void applySearch(String query){
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        filtered.setPredicate(song -> {
            if (q.isBlank()) return true;
            return song.getTitle().toLowerCase(Locale.ROOT).contains(q)
                    || song.getArtist().toLowerCase(Locale.ROOT).contains(q)
                    || song.getAlbum().toLowerCase(Locale.ROOT).contains(q)
                    || song.getGenre().toLowerCase(Locale.ROOT).contains(q);
        });
    }

    public void refresh(){
        table.refresh();
    }

    public void selectByIndex(int idx){
        if (idx < 0 || idx >= table.getItems().size()) return;
        table.getSelectionModel().clearSelection();
        table.getSelectionModel().select(idx);
        table.scrollTo(idx);
    }

    private List<Song> selectedSongs(){
        ObservableList<Song> selection = table.getSelectionModel().getSelectedItems();
        return selection == null ? List.of() : selection.stream().collect(Collectors.toList());
    }

    private String formatDuration(int sec){
        int minutes = sec / 60;
        int seconds = sec % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
