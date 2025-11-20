package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.playback.PlaybackController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

public class PlaylistController {

    @FXML private Label plTitle;
    @FXML private ListView<Song> trackList;

    private final ObservableList<Song> tracks = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        FXServiceLocator.setPlaylistController(this);
        trackList.setItems(tracks);
        trackList.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " • " + item.getArtist());
                }
            }
        });
        refresh();
    }

    public void refresh() {
        Playlist current = FXServiceLocator.getCurrentPlaylist();
        if (current != null) {
            plTitle.setText(current.getName());
            tracks.setAll(current.tracks());
        } else {
            plTitle.setText("Playlist");
            tracks.clear();
        }
    }

    @FXML private void playAll() {
        if (tracks.isEmpty()) return;
        PlaybackController playback = FXServiceLocator.getPlaybackController();
        playback.enqueueAll(tracks);
        if (FXServiceLocator.getPlayerBarController() != null) {
            FXServiceLocator.getPlayerBarController().playNextInQueue();
        }
    }
    @FXML private void remove() {
        Song selected = trackList.getSelectionModel().getSelectedItem();
        Playlist current = FXServiceLocator.getCurrentPlaylist();
        if (selected != null && current != null) {
            current.remove(selected);
            refresh();
        }
    }
    @FXML private void rename() {
        Playlist current = FXServiceLocator.getCurrentPlaylist();
        if (current == null) return;
        TextInputDialog dialog = new TextInputDialog(current.getName());
        dialog.setHeaderText("Rename playlist");
        dialog.showAndWait().ifPresent(name -> {
            current.setName(name);
            plTitle.setText(name);
            if (FXServiceLocator.getPlaylists().contains(current)) {
                int idx = FXServiceLocator.getPlaylists().indexOf(current);
                FXServiceLocator.getPlaylists().set(idx, current);
            }
        });
    }
}

