package com.example.sortify.controller;

import com.example.sortify.algo.Sorts;
import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.playback.PlaybackController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

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
    private TableColumn<Song, Number> colDuration;

    private final ObservableList<Song> songs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXServiceLocator.setLibraryController(this);

        songs.setAll(FXServiceLocator.getLibraryRepository().getLibrary());
        configureColumns();
        table.setItems(songs);
        table.getSelectionModel().setCellSelectionEnabled(false);
        table.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    private void configureColumns() {
        colNum.setCellValueFactory(cell -> {
            int index = table.getItems().indexOf(cell.getValue());
            return new javafx.beans.property.SimpleIntegerProperty(index + 1);
        });
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colDuration.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getDurationSec()));
    }

    public void filter(String term) {
        List<Song> all = FXServiceLocator.getLibraryRepository().getLibrary();
        if (term == null || term.isBlank()) {
            songs.setAll(all);
            return;
        }
        String lower = term.toLowerCase();
        songs.setAll(all.stream().filter(s ->
                s.getTitle().toLowerCase().contains(lower)
                        || s.getArtist().toLowerCase().contains(lower)
                        || s.getAlbum().toLowerCase().contains(lower)
                        || s.getGenre().toLowerCase().contains(lower)
        ).toList());
    }

    public void sort(Comparator<Song> comparator, boolean useMergeSort) {
        List<Song> working = songs.stream().toList();
        songs.setAll(working);
        if (useMergeSort) {
            Sorts.mergeSort(songs, comparator);
        } else {
            Sorts.quickSort(songs, comparator);
        }
    }

    @FXML
    private void addToQueue() {
        PlaybackController playback = FXServiceLocator.getPlaybackController();
        List<Song> selected = table.getSelectionModel().getSelectedItems();
        if (selected.isEmpty() && !songs.isEmpty()) {
            selected = List.of(songs.getFirst());
        }
        playback.enqueueAll(selected);
        if (FXServiceLocator.getPlayerBarController() != null && FXServiceLocator.getPlayerBarController().isIdle()) {
            FXServiceLocator.getPlayerBarController().playNextInQueue();
        }
    }

    @FXML
    private void addToPlaylist() {
        List<Song> selected = table.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) return;
        Playlist playlist = FXServiceLocator.getCurrentPlaylist();
        if (playlist == null) {
            playlist = new Playlist("Favorites");
            FXServiceLocator.getPlaylists().add(playlist);
            FXServiceLocator.setCurrentPlaylist(playlist);
        }
        selected.forEach(playlist::add);
        if (FXServiceLocator.getPlaylistController() != null) {
            FXServiceLocator.getPlaylistController().refresh();
        }
    }
}

