package com.example.sortify.controller;

import com.example.sortify.model.Playlist;
import com.example.sortify.model.Song;
import com.example.sortify.util.Actions;
import com.example.sortify.util.PlaylistCsvStore;
import com.example.sortify.util.UndoManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class SideBarController {

    @FXML
    private ListView<Playlist> playlistsList;

    private final UndoManager undoManager = new UndoManager();

    private final Path playlistsFile = Paths.get("data/playlists.csv");

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
        if (active != null) {
            playlistsList.getSelectionModel().select(active);
        }
        FXServiceLocator.activePlaylistProperty().addListener((obs, old, pl) -> {
            if (pl != null) playlistsList.getSelectionModel().select(pl);
        });
        playlistsList.getSelectionModel().selectedItemProperty().addListener((obs, old, chosen) -> {
            if (chosen != null) {
                FXServiceLocator.setActivePlaylist(chosen);
                FXServiceLocator.getMain().navigate(MainController.Route.PLAYLIST);
                FXServiceLocator.playlistController().ifPresent(PlaylistController::refresh);
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
    private void importFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Import music folder");
        Window win = playlistsList.getScene().getWindow();
        File folder = chooser.showDialog(win);
        if (folder == null) return;
        try {
            FXServiceLocator.library().importFromFolder(Path.of(folder.toURI()));
            FXServiceLocator.libraryView().setAll(FXServiceLocator.library().getLibrary());
            FXServiceLocator.libraryView().sort(
                    java.util.Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER));
            FXServiceLocator.libraryController().ifPresent(LibraryController::refresh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePlaylist() {
        Playlist selected = playlistsList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int idx = FXServiceLocator.playlists().indexOf(selected);

        Actions.DeletePlaylist action = new Actions.DeletePlaylist(
                FXServiceLocator.playlists(), // always master list
                selected,
                idx,
                playlistsFile                  // CSV path
        );

        undoManager.execute(action);
    }
}
