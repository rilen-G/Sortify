package com.example.sortify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LibraryController {

    @FXML
    private TableView<?> table;
    @FXML
    private TableColumn<?, ?> colNum;
    @FXML
    private TableColumn<?, ?> colTitle;
    @FXML
    private TableColumn<?, ?> colArtist;
    @FXML
    private TableColumn<?, ?> colAlbum;
    @FXML
    private TableColumn<?, ?> colGenre;
    @FXML
    private TableColumn<?, ?> colDuration;

    @FXML
    private void addToQueue() {
        // logic here
    }

    @FXML
    private void addToPlaylist() {
        // logic here
    }
}

