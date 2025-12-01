package com.example.sortify.controller;

import com.example.sortify.algo.Search;
import com.example.sortify.algo.Sorts;
import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopBarController {

    @FXML
    private TextField searchField;
    @FXML
    private ChoiceBox<String> sortChoice;
    @FXML
    private ChoiceBox<String> algoChoice;
    @FXML
    private Label runtimeLabel;
    @FXML
    private Button undoBtn;
    @FXML
    private Button redoBtn;

    @FXML
    public void initialize() {
        sortChoice.getItems().setAll("Title", "Artist", "Album", "Genre", "Duration", "Most Played");
        sortChoice.getSelectionModel().selectFirst();
        algoChoice.getItems().setAll("Merge Sort", "Quick Sort");
        algoChoice.getSelectionModel().selectFirst();
        searchField.textProperty().addListener((obs, o, n) -> applySearchToActive(n));
        FXServiceLocator.currentRouteProperty().addListener((obs, oldRoute, newRoute) ->
                applySearchToActive(searchField.getText()));
        updateUndoButtons();
        sort();
    }

    @FXML
    private void sort() {
        Comparator<Song> cmp = comparatorFor(sortChoice.getValue());
        String algo = algoChoice.getValue();
        if (isPlaylistRoute()) {
            sortPlaylist(cmp, algo);
        } else {
            sortLibrary(cmp, algo);
        }
    }

    @FXML
    private void binarySearch() {
        String term = searchField.getText();
        if (term == null || term.isBlank()) return;
        if (isPlaylistRoute()) {
            binarySearchPlaylist(term);
        } else {
            binarySearchLibrary(term);
        }
    }

    private void binarySearchLibrary(String term){
        Comparator<Song> cmp = Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER);
        List<Song> data = new ArrayList<>(FXServiceLocator.library().getLibrary());
        Sorts.mergeSort(data, cmp);
        int idx = Search.binarySearch(data, new Song("", term, "", "", "",0,""), cmp);
        if (idx >= 0) {
            String foundId = data.get(idx).getId();
            int viewIdx = FXServiceLocator.libraryView().indexOf(
                    FXServiceLocator.library().byId().get(foundId));
            FXServiceLocator.libraryController().ifPresent(c -> c.selectByIndex(viewIdx));
            runtimeLabel.setText("Binary search found: " + term);
        } else {
            runtimeLabel.setText("Binary search: not found");
        }
    }

    private void binarySearchPlaylist(String term){
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null) {
            runtimeLabel.setText("No playlist selected");
            return;
        }
        Comparator<Song> cmp = Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER);
        List<Song> data = new ArrayList<>(pl.tracks());
        Sorts.mergeSort(data, cmp);
        int idx = Search.binarySearch(data, new Song("", term, "", "", "",0,""), cmp);
        if (idx >= 0){
            Song found = data.get(idx);
            FXServiceLocator.playlistController().ifPresent(c -> c.selectSong(found));
            runtimeLabel.setText("Playlist search found: " + term);
        } else {
            runtimeLabel.setText("Playlist search: not found");
        }
    }

    @FXML
    private void undo(){
        FXServiceLocator.undo().undo();
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
        FXServiceLocator.savePlaylists();
    }

    @FXML
    private void redo(){
        FXServiceLocator.undo().redo();
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
        FXServiceLocator.savePlaylists();
    }

    private void updateUndoButtons(){
        if (undoBtn != null) undoBtn.setDisable(false);
        if (redoBtn != null) redoBtn.setDisable(false);
    }

    private void applySearchToActive(String text){
        if (isPlaylistRoute()) {
            FXServiceLocator.playlistController().ifPresent(c -> c.applySearch(text));
        } else {
            FXServiceLocator.libraryController().ifPresent(c -> c.applySearch(text));
        }
    }

    private void sortLibrary(Comparator<Song> cmp, String algo){
        List<Song> data = new ArrayList<>(FXServiceLocator.library().getLibrary());
        long elapsed = sortData(data, algo, cmp);
        runtimeLabel.setText(String.format("Last sort: %.2f ms (%s)",
                elapsed / 1_000_000.0, algo));

        FXServiceLocator.library().getLibrary().clear();
        FXServiceLocator.library().getLibrary().addAll(data);
        FXServiceLocator.libraryView().setAll(data);
        FXServiceLocator.libraryController().ifPresent(LibraryController::refresh);
    }

    private void sortPlaylist(Comparator<Song> cmp, String algo){
        Playlist pl = FXServiceLocator.activePlaylist();
        if (pl == null) {
            runtimeLabel.setText("No playlist selected");
            return;
        }
        List<Song> data = new ArrayList<>(pl.tracks());
        long elapsed = sortData(data, algo, cmp);
        pl.tracks().clear();
        pl.tracks().addAll(data);
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
        FXServiceLocator.savePlaylists();
        runtimeLabel.setText(String.format("Playlist sort: %.2f ms (%s)",
                elapsed / 1_000_000.0, algo));
    }

    private long sortData(List<Song> data, String algo, Comparator<Song> cmp){
        long start = System.nanoTime();
        if ("Merge Sort".equals(algo)) {
            Sorts.mergeSort(data, cmp);
        } else {
            Sorts.quickSort(data, cmp);
        }
        return System.nanoTime() - start;
    }

    private boolean isPlaylistRoute(){
        return FXServiceLocator.currentRoute() == MainController.Route.PLAYLIST;
    }

    private Comparator<Song> comparatorFor(String choice){
        return switch (choice) {
            case "Artist" -> Comparator.comparing(Song::getArtist, String.CASE_INSENSITIVE_ORDER);
            case "Album" -> Comparator.comparing(Song::getAlbum, String.CASE_INSENSITIVE_ORDER);
            case "Genre" -> Comparator.comparing(Song::getGenre, String.CASE_INSENSITIVE_ORDER);
            case "Duration" -> Comparator.comparingInt(Song::getDurationSec);
            case "Most Played" -> Comparator.comparingInt(Song::getPlayCount).reversed();
            default -> Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER);
        };
    }
}
