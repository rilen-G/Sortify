package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class SideBarController {

    @FXML
    private ListView<Playlist> playlistsList;

    @FXML
    public void initialize() {
        playlistsList.setItems(FXServiceLocator.playlists());
        playlistsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        Playlist active = FXServiceLocator.activePlaylist();
        if (active != null){
            playlistsList.getSelectionModel().select(active);
        }
        FXServiceLocator.activePlaylistProperty().addListener((obs, old, pl) -> {
            if (pl != null) playlistsList.getSelectionModel().select(pl);
        });
        playlistsList.getSelectionModel().selectedItemProperty().addListener((obs, old, chosen) -> openPlaylist(chosen));
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
    private void goStats() {
        FXServiceLocator.getMain().navigate(MainController.Route.STATS);
    }

    @FXML
    private void newPlaylist() {
        TextInputDialog dialog = new TextInputDialog("New Playlist");
        dialog.setHeaderText("Name your playlist");
        Optional<String> input = dialog.showAndWait();
        input.map(String::trim).filter(s -> !s.isEmpty()).ifPresent(name -> {
            Playlist p = new Playlist(name);
            FXServiceLocator.playlists().add(p);
            FXServiceLocator.setActivePlaylist(p);
            playlistsList.getSelectionModel().select(p);
            FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
            FXServiceLocator.savePlaylists();
        });
    }

    @FXML
    private void deletePlaylist() {
        Playlist selected = playlistsList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete playlist?");
        confirm.setContentText("Remove \"" + selected.getName() + "\" from your playlists?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        int previousIndex = playlistsList.getSelectionModel().getSelectedIndex();
        FXServiceLocator.playlists().remove(selected);

        if (FXServiceLocator.playlists().isEmpty()) {
            FXServiceLocator.setActivePlaylist(null);
            playlistsList.getSelectionModel().clearSelection();
        } else {
            int nextIndex = Math.min(previousIndex, FXServiceLocator.playlists().size() - 1);
            playlistsList.getSelectionModel().select(nextIndex);
        }
        FXServiceLocator.savePlaylists();
    }

    private void openPlaylist(Playlist chosen){
        if (chosen == null) return;
        FXServiceLocator.setActivePlaylist(chosen);
        FXServiceLocator.getMain().navigate(MainController.Route.PLAYLIST);
        FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
    }
}
