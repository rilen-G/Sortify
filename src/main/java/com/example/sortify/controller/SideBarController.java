package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.repo.LibraryRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Optional;

public class SideBarController {

    @FXML
    private ListView<String> playlistsList;

    private final ObservableList<String> playlistNames = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        refreshPlaylists();
        playlistsList.setOnMouseClicked(evt -> {
            int idx = playlistsList.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < FXServiceLocator.getPlaylists().size()) {
                Playlist p = FXServiceLocator.getPlaylists().get(idx);
                FXServiceLocator.setCurrentPlaylist(p);
                FXServiceLocator.getMain().navigate(MainController.Route.PLAYLIST);
            }
        });
    }

    @FXML
    private void goLibrary() {
        FXServiceLocator.getMain().navigate(MainController.Route.LIBRARY);
    }

    @FXML
    private void goForYou() {
        FXServiceLocator.getMain().navigate(MainController.Route.FORYOU);
    }

    @FXML
    private void goPlaylist() {
        FXServiceLocator.getMain().navigate(MainController.Route.PLAYLIST);
    }

    @FXML
    private void newPlaylist() {
        TextInputDialog dialog = new TextInputDialog("New Playlist");
        dialog.setHeaderText("Create playlist");
        Optional<String> name = dialog.showAndWait();
        name.ifPresent(n -> {
            Playlist playlist = new Playlist(n.trim().isEmpty() ? "Untitled" : n.trim());
            FXServiceLocator.getPlaylists().add(playlist);
            FXServiceLocator.setCurrentPlaylist(playlist);
            refreshPlaylists();
            playlistsList.getSelectionModel().select(playlist.getName());
            FXServiceLocator.getMain().navigate(MainController.Route.PLAYLIST);
        });
    }

    @FXML
    private void importFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Import music folder");
        Window window = playlistsList.getScene() != null ? playlistsList.getScene().getWindow() : null;
        File chosen = chooser.showDialog(window);
        if (chosen != null) {
            LibraryRepository repo = FXServiceLocator.getLibraryRepository();
            try {
                repo.importFromFolder(chosen.toPath());
                FXServiceLocator.getMain().navigate(MainController.Route.LIBRARY);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not import folder: " + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    private void refreshPlaylists() {
        playlistNames.setAll(FXServiceLocator.getPlaylists().stream().map(Playlist::getName).toList());
        playlistsList.setItems(playlistNames);
    }
}

