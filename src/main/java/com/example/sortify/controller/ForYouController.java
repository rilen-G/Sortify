package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ForYouController {

    @FXML private ListView<Song> suggested;

    @FXML
    public void initialize(){
        suggested.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle() + " — " + item.getArtist());
            }
        });
    }

    @FXML
    private void generate() {
        List<Song> all = new ArrayList<>(FXServiceLocator.libraryView());
        if (all.isEmpty()) return;
        all.sort(Comparator.comparingInt(Song::getPlayCount).reversed());
        if (all.stream().allMatch(s -> s.getPlayCount() == 0)){
            java.util.Collections.shuffle(all);
        }
        List<Song> picks = all.stream().limit(6).collect(Collectors.toList());
        suggested.getItems().setAll(picks);
    }

    @FXML
    private void save() {
        if (suggested.getItems().isEmpty()) return;
        Playlist playlist = FXServiceLocator.activePlaylist();
        if (playlist == null){
            playlist = new Playlist("For You");
            FXServiceLocator.playlists().add(playlist);
            FXServiceLocator.setActivePlaylist(playlist);
        }
        suggested.getItems().forEach(playlist::add);
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
        FXServiceLocator.savePlaylists();
    }
}
