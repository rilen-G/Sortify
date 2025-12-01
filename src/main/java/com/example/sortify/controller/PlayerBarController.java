package com.example.sortify.controller;

import com.example.sortify.model.Song;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Optional;

public class PlayerBarController {

    @FXML private Label nowLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Slider progress;
    @FXML private Slider vol;

    @FXML private Button prevBtn;
    @FXML private Button playPauseBtn;
    @FXML private Button nextBtn;

    private boolean playing = false;
    private MediaPlayer boundPlayer;

    @FXML
    public void initialize() {
        FXServiceLocator.registerPlayerBarController(this);
        vol.valueProperty().addListener((obs, old, val) ->
                FXServiceLocator.audio().setVolume(val.doubleValue() / 100.0));
        vol.setValue(65);

        progress.setDisable(true);

        progress.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) seekToSlider();
        });

        progress.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!progress.isValueChanging()) {
                if (Math.abs(newVal.doubleValue() - oldVal.doubleValue()) > 500) {
                    seekToSlider();
                }
            }
        });
    }

    @FXML
    private void prev() {
        Optional<Song> song = FXServiceLocator.playback().playPrev();
        song.ifPresent(this::playSong);
    }

    @FXML
    private void toggle() {
        Song current = FXServiceLocator.playback().current();
        if (current == null){
            Optional<Song> next = FXServiceLocator.playback().playNext();
            next.ifPresent(this::playSong);
            return;
        }
        if (playing){
            playPauseBtn.setText("▶");
            FXServiceLocator.audio().pause();
            playing = false;
        } else {
            playPauseBtn.setText("⏸");
            FXServiceLocator.audio().resume();
            playing = true;
        }
    }

    @FXML
    private void next() {
        Optional<Song> song = FXServiceLocator.playback().playNext();
        if (song.isPresent()){
            playSong(song.get());
        } else {
            FXServiceLocator.audio().stop();
            nowLabel.setText("Queue empty");
            currentTimeLabel.setText("0:00");
            totalTimeLabel.setText("0:00");
            playPauseBtn.setText("▶");
            playing = false;
            progress.setDisable(true);
            progress.setValue(0);
            attachPlayer(null);
        }
    }

    public void playSong(Song s){
        nowLabel.setText(s.getTitle() + " - " + s.getArtist());
        playPauseBtn.setText("⏸");
        playing = true;
        s.incrementPlayCount();
        FXServiceLocator.saveStats();
        FXServiceLocator.audio().play(s, () -> Platform.runLater(this::next));
        attachPlayer(FXServiceLocator.audio().getPlayer());
    }

    private void attachPlayer(MediaPlayer mp){
        boundPlayer = mp;
        if (mp == null) {
            progress.setDisable(true);
            progress.setValue(0);
            currentTimeLabel.setText("0:00");
            totalTimeLabel.setText("0:00");
            return;
        }
        progress.setDisable(false);
        progress.setValue(0);
        currentTimeLabel.setText("0:00");

        mp.totalDurationProperty().addListener((obs, old, total) -> applyTotal(total));
        mp.setOnReady(() -> applyTotal(mp.getMedia().getDuration()));
        applyTotal(mp.getMedia().getDuration());

        mp.currentTimeProperty().addListener((obs, old, val) -> {
            // Only update slider if user is NOT dragging it
            if (!progress.isValueChanging()) {
                progress.setValue(val.toMillis());
            }
            currentTimeLabel.setText(formatDuration(val));
        });
    }

    private void applyTotal(Duration total){
        if (total == null || total.isUnknown()) return;
        progress.setMax(total.toMillis());
        totalTimeLabel.setText(formatDuration(total));
    }

    private void seekToSlider(){
        if (boundPlayer == null) return;
        boundPlayer.seek(Duration.millis(progress.getValue()));
    }

    private String formatDuration(Duration d){
        if (d == null || d.isUnknown()) return "0:00";
        int totalSeconds = (int)Math.floor(d.toSeconds());
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @FXML
    private void goQueue() {
        FXServiceLocator.getMain().navigate(MainController.Route.QUEUE);
    }
}
