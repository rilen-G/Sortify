package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class ForYouController {

    @FXML private ListView<Song> suggested;

    @FXML
    private void generate() {
        List<Song> top = FXServiceLocator.getStatsService().topN(
                FXServiceLocator.getLibraryRepository().getLibrary(),
                5
        );
        suggested.getItems().setAll(top);
        suggested.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle() + " — " + item.getArtist());
            }
        });
    }

    @FXML
    private void save() {
        Playlist playlist = new Playlist("For You Mix");
        playlist.tracks().addAll(suggested.getItems());
        FXServiceLocator.getPlaylists().add(playlist);
        FXServiceLocator.setCurrentPlaylist(playlist);
        if (FXServiceLocator.getPlaylistController() != null) {
            FXServiceLocator.getPlaylistController().refresh();
        }
    }
}

