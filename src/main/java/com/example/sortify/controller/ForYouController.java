package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.stats.RecommenderService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class ForYouController {

    @FXML private TableView<Song> suggested;
    @FXML private TableColumn<Song, String> colTitle;
    @FXML private TableColumn<Song, String> colArtist;
    @FXML private TableColumn<Song, String> colAlbum;

    private final RecommenderService recommender = new RecommenderService();

    @FXML
    public void initialize(){
        suggested.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colTitle.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));
        colArtist.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getArtist()));
        colAlbum.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAlbum()));
    }

    @FXML
    private void generate() {
        List<Song> all = new ArrayList<>(FXServiceLocator.libraryView());
        if (all.isEmpty()) return;
        List<Song> recent = recentHistoryMostRecentFirst();
        List<Song> picks = recommender.recommend(all, recent, 20);
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

    private List<Song> recentHistoryMostRecentFirst(){
        var history = FXServiceLocator.playback().getHistory();
        List<Song> recent = new ArrayList<>();
        for (int i = history.size() - 1; i >= 0; i--){
            recent.add(history.get(i));
        }
        return recent;
    }
}
