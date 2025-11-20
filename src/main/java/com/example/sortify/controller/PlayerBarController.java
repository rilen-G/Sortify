package com.example.sortify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class PlayerBarController {

    @FXML private Label nowLabel;
    @FXML private Label timeLabel;
    @FXML private Slider seek;
    @FXML private Slider vol;

    @FXML private Button prevBtn;
    @FXML private Button playPauseBtn;
    @FXML private Button nextBtn;

    @FXML
    private void prev() {
        // playback.prev()
    }

    @FXML
    private void toggle() {
        // pause/play
    }

    @FXML
    private void next() {
        // playback.next()
    }
}

