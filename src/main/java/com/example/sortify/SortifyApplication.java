package com.example.sortify;

import com.example.sortify.controller.FXServiceLocator;
import com.example.sortify.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SortifyApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXServiceLocator.bootstrap();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sortify/ui/Main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        FXServiceLocator.setMainController(controller);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/com/example/sortify/ui/css/app.css").toExternalForm());

        stage.setTitle("Sortify");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
