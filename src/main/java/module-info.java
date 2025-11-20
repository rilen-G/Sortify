module com.example.sortify {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.example.sortify to javafx.fxml;
    opens com.example.sortify.controller to javafx.fxml;
    opens com.example.sortify.model to javafx.fxml;

    exports com.example.sortify;
    exports com.example.sortify.controller;
}