package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.util.Actions;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaylistController {

    @FXML private Label plTitle;
    @FXML private TableView<Song> trackList;
    @FXML private TableColumn<Song, String> colTitle;
    @FXML private TableColumn<Song, String> colArtist;

    private final ObservableList<Song> currentTracks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        FXServiceLocator.registerPlaylistController(this);
        trackList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        trackList.setItems(currentTracks);

        colTitle.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));
        colArtist.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getArtist()));

        FXServiceLocator.activePlaylistProperty().addListener((obs, old, pl) -> loadPlaylist(pl));
        loadPlaylist(FXServiceLocator.activePlaylist());
    }

    @FXML private void playAll() {
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null || pl.tracks().isEmpty()) return;
        FXServiceLocator.playback().getQueue().clear();
        FXServiceLocator.playback().enqueueAll(pl.tracks());
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

    private void moveSelected(int delta){
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null) return;
        int idx = trackList.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;
        int target = idx + delta;
        if (target < 0 || target >= pl.tracks().size()) return;
        Song s = currentTracks.get(idx);
        FXServiceLocator.undo().execute(new Actions.MoveInPlaylist(pl, s, idx, target));
        refresh();
        trackList.getSelectionModel().select(target);
        trackList.scrollTo(target);
        FXServiceLocator.savePlaylists();
    }

    private List<Song> selectedSongs(){
        return trackList.getSelectionModel().getSelectedItems()
                .stream()
                .collect(Collectors.toList());
    }

    private void loadPlaylist(Playlist pl){
        currentTracks.clear();
        if (pl == null) {
            plTitle.setText("No playlist");
            return;
        }
        plTitle.setText(pl.getName());
        currentTracks.addAll(pl.tracks());
    }
}
