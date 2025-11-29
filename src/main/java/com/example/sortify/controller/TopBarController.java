package com.example.sortify.controller;

import com.example.sortify.algo.Search;
import com.example.sortify.algo.Sorts;
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
        searchField.textProperty().addListener((obs, o, n) ->
                FXServiceLocator.libraryController().ifPresent(c -> c.applySearch(n)));
        updateUndoButtons();
        sort();
    }

    @FXML
    private void sort() {
        Comparator<Song> cmp = comparatorFor(sortChoice.getValue());
        List<Song> data = new ArrayList<>(FXServiceLocator.library().getLibrary());
        long start = System.nanoTime();
        if ("Merge Sort".equals(algoChoice.getValue())) {
            Sorts.mergeSort(data, cmp);
        } else {
            Sorts.quickSort(data, cmp);
        }
        long elapsed = System.nanoTime() - start;
        runtimeLabel.setText(String.format("Last sort: %.2f ms (%s)",
                elapsed / 1_000_000.0, algoChoice.getValue()));

        FXServiceLocator.library().getLibrary().clear();
        FXServiceLocator.library().getLibrary().addAll(data);
        FXServiceLocator.libraryView().setAll(data);
        FXServiceLocator.libraryController().ifPresent(LibraryController::refresh);
    }

    @FXML
    private void binarySearch() {
        String term = searchField.getText();
        if (term == null || term.isBlank()) return;
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

    @FXML
    private void undo(){
        FXServiceLocator.undo().undo();
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
    }

    @FXML
    private void redo(){
        FXServiceLocator.undo().redo();
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
    }

    private void updateUndoButtons(){
        if (undoBtn != null) undoBtn.setDisable(false);
        if (redoBtn != null) redoBtn.setDisable(false);
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
