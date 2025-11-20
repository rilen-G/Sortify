package com.example.sortify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class SideBarController {

    @FXML
    private ListView<String> playlistsList;

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
        // add logic later
    }

    @FXML
    private void importFolder() {
        // add logic later
    }
}

