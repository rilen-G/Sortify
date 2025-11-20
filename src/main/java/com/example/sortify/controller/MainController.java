package com.example.sortify.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contentHost;

    public enum Route { LIBRARY, PLAYLIST, FORYOU }

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
            };


            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            Parent view = loader.load();
            swapView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swapView(Parent newView) {
        if (contentHost.getChildren().isEmpty()) {
            contentHost.getChildren().setAll(newView);
            fadeIn(newView);
            return;
        }

        Parent old = (Parent) contentHost.getChildren().getFirst();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), old);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(evt -> {
            contentHost.getChildren().setAll(newView);
            fadeIn(newView);
        });
        fadeOut.play();
    }

    private void fadeIn(Parent node) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
}
