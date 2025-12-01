package com.example.sortify.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contentHost;

    public enum Route { LIBRARY, PLAYLIST, FORYOU, STATS }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXServiceLocator.setMainController(this);
        navigate(Route.LIBRARY);
    }

    public void navigate(Route route) {
        try {
            String fxmlPath = switch (route) {
                case LIBRARY -> "/com/example/sortify/ui/views/library-view.fxml";
                case PLAYLIST -> "/com/example/sortify/ui/views/playlist-view.fxml";
                case FORYOU  -> "/com/example/sortify/ui/views/foryou-view.fxml";
                case STATS   -> "/com/example/sortify/ui/views/stats-view.fxml";
            };


            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            Parent view = loader.load();
            FXServiceLocator.setCurrentRoute(route);
            contentHost.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
