package com.example.sortify.controller;

import com.example.sortify.model.Song;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
        vol.valueProperty().addListener((obs, old, val) ->
                FXServiceLocator.audio().setVolume(val.doubleValue() / 100.0));
        vol.setValue(65);

        progress.setDisable(true);
        progress.valueChangingProperty().addListener((obs, was, is) -> {
            if (!is) seekToSlider();
        });
        progress.setOnMouseReleased(e -> seekToSlider());
        progress.setOnMouseClicked(e -> seekToSlider());
        progress.valueProperty().addListener((obs, old, val) -> updateProgressFill());
        progress.skinProperty().addListener((obs, old, skin) -> Platform.runLater(this::updateProgressFill));
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
            playPauseBtn.setText("Play");
            FXServiceLocator.audio().pause();
            playing = false;
        } else {
            playPauseBtn.setText("Pause");
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
            nowLabel.setText("Queue empty");
            currentTimeLabel.setText("0:00");
            totalTimeLabel.setText("0:00");
            playPauseBtn.setText("Play");
            playing = false;
            progress.setDisable(true);
            progress.setValue(0);
        }
    }

    private void playSong(Song s){
        nowLabel.setText(s.getTitle() + " — " + s.getArtist());
        playPauseBtn.setText("Pause");
        playing = true;
        s.incrementPlayCount();
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
        mp.setOnReady(() -> {
            applyTotal(mp.getMedia().getDuration());
        });
        applyTotal(mp.getMedia().getDuration());

        mp.currentTimeProperty().addListener((obs, old, val) -> {
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
        updateProgressFill();
    }

    private void seekToSlider(){
        if (boundPlayer == null) return;
        boundPlayer.seek(Duration.millis(progress.getValue()));
    }

    private void updateProgressFill(){
        var track = progress.lookup(".track");
        if (track == null) {
            Platform.runLater(this::updateProgressFill);
            return;
        }
        double max = progress.getMax();
        double val = progress.getValue();
        double pct = max <= 0 ? 0 : Math.max(0, Math.min(1.0, val / max));
        double percent = pct * 100.0;
        String style = String.format("-fx-background-color: linear-gradient(to right, #b370ff 0%%, #b370ff %.2f%%, rgba(255,255,255,0.12) %.2f%%, rgba(255,255,255,0.12) 100%%); -fx-background-radius: 50; -fx-pref-height: 6;", percent, percent);
        track.setStyle(style);
    }

    private String formatDuration(Duration d){
        if (d == null || d.isUnknown()) return "0:00";
        int totalSeconds = (int)Math.floor(d.toSeconds());
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
