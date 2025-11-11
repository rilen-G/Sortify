module com.example.sortify {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sortify to javafx.fxml;
    exports com.example.sortify;
}