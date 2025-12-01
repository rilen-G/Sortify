package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.util.Actions;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaylistController {

    @FXML private Label plTitle;
    @FXML private TableView<Song> trackList;
    @FXML private TableColumn<Song, String> colTitle;
    @FXML private TableColumn<Song, String> colArtist;
    @FXML private TableColumn<Song, String> colAlbum;

    private final ObservableList<Song> currentTracks = FXCollections.observableArrayList();
    private FilteredList<Song> filteredTracks;

    @FXML
    public void initialize() {
        FXServiceLocator.registerPlaylistController(this);
        trackList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        filteredTracks = new FilteredList<>(currentTracks, s -> true);
        trackList.setItems(filteredTracks);

        colTitle.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));
        colArtist.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getArtist()));
        colAlbum.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAlbum()));

        FXServiceLocator.activePlaylistProperty().addListener((obs, old, pl) -> loadPlaylist(pl));
        loadPlaylist(FXServiceLocator.activePlaylist());
    }

    @FXML private void playAll() {
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null || pl.tracks().isEmpty()) return;
        Optional<Song> first = FXServiceLocator.playback().startPlaylistLoop(pl);
        first.ifPresent(song -> FXServiceLocator.playerBarController()
                .ifPresent(bar -> bar.playSong(song)));
    }

    @FXML private void remove() {
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null) return;
        List<Song> selection = selectedSongs();
        if (selection.isEmpty()) return;
        FXServiceLocator.undo().execute(new Actions.RemoveFromPlaylist(pl, selection));
        refresh();
        FXServiceLocator.savePlaylists();
    }

    @FXML private void rename() {
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null) return;
        TextInputDialog dialog = new TextInputDialog(pl.getName());
        dialog.setHeaderText("Rename playlist");
        Optional<String> name = dialog.showAndWait();
        name.map(String::trim).filter(s -> !s.isEmpty()).ifPresent(newName -> {
            FXServiceLocator.undo().execute(new Actions.RenamePlaylist(pl, pl.getName(), newName));
            plTitle.setText(newName);
            int idx = FXServiceLocator.playlists().indexOf(pl);
            if (idx >= 0) FXServiceLocator.playlists().set(idx, pl);
            trackList.refresh();
            FXServiceLocator.savePlaylists();
        });
    }

    @FXML private void moveUp() {
        moveSelected(-1);
    }

    @FXML private void moveDown() {
        moveSelected(1);
    }

    public void refresh(){
        loadPlaylist(FXServiceLocator.activePlaylist());
    }

    public void applySearch(String query){
        if (filteredTracks == null) return;
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        filteredTracks.setPredicate(song -> {
            if (q.isBlank()) return true;
            return song.getTitle().toLowerCase(Locale.ROOT).contains(q)
                    || song.getArtist().toLowerCase(Locale.ROOT).contains(q)
                    || song.getAlbum().toLowerCase(Locale.ROOT).contains(q);
        });
    }

    public void selectSong(Song song){
        if (song == null) return;
        int idx = trackList.getItems().indexOf(song);
        if (idx < 0) return;
        trackList.getSelectionModel().clearSelection();
        trackList.getSelectionModel().select(idx);
        trackList.scrollTo(idx);
    }

    private void moveSelected(int delta){
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null) return;
        Song selected = trackList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        int idx = pl.tracks().indexOf(selected);
        if (idx < 0) return;
        int target = idx + delta;
        if (target < 0 || target >= pl.tracks().size()) return;
        FXServiceLocator.undo().execute(new Actions.MoveInPlaylist(pl, selected, idx, target));
        refresh();
        selectSong(selected);
        FXServiceLocator.savePlaylists();
    }

    private List<Song> selectedSongs(){
        return trackList.getSelectionModel().getSelectedItems()
                .stream()
                .collect(Collectors.toList());
    }

    private void loadPlaylist(Playlist pl){
        if (pl == null) {
            plTitle.setText("No playlist");
            currentTracks.clear();
            return;
        }
        plTitle.setText(pl.getName());
        currentTracks.setAll(pl.tracks());
    }
}
