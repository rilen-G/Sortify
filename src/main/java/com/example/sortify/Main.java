package com.example.sortify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.sortify.controller.FXServiceLocator;
import com.example.sortify.controller.MainController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxml = new FXMLLoader(
                getClass().getResource("/com/example/sortify/ui/Main.fxml")
        );
        Scene scene = new Scene(fxml.load(), 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/sortify/ui/css/app.css").toExternalForm()
        );
        MainController main = fxml.getController();
        FXServiceLocator.bootstrap(main);
        stage.setTitle("Sortify");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){ launch(args); }
}

