package com.example.sortify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import java.util.Comparator;

public class TopBarController {

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> sortChoice;

    @FXML
    private void initialize() {
        sortChoice.getItems().addAll(
                "Title (merge)",
                "Artist (merge)",
                "Album (quick)",
                "Duration (quick)"
        );
        sortChoice.getSelectionModel().selectFirst();
        searchField.textProperty().addListener((obs, old, val) -> {
            if (FXServiceLocator.getLibraryController() != null) {
                FXServiceLocator.getLibraryController().filter(val);
            }
        });
    }

    @FXML
    private void sort() {
        if (FXServiceLocator.getLibraryController() == null) return;
        String choice = sortChoice.getSelectionModel().getSelectedItem();
        boolean useMerge = choice != null && choice.contains("merge");
        Comparator<com.example.sortify.model.Song> comparator;
        if (choice != null && choice.startsWith("Artist")) {
            comparator = Comparator.comparing(com.example.sortify.model.Song::getArtist, String.CASE_INSENSITIVE_ORDER);
        } else if (choice != null && choice.startsWith("Album")) {
            comparator = Comparator.comparing(com.example.sortify.model.Song::getAlbum, String.CASE_INSENSITIVE_ORDER);
        } else if (choice != null && choice.startsWith("Duration")) {
            comparator = Comparator.comparingInt(com.example.sortify.model.Song::getDurationSec);
        } else {
            comparator = Comparator.comparing(com.example.sortify.model.Song::getTitle, String.CASE_INSENSITIVE_ORDER);
        }
        FXServiceLocator.getLibraryController().sort(comparator, useMerge);
    }
}

