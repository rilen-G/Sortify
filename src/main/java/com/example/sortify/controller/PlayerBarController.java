package com.example.sortify.controller;

import com.example.sortify.model.Song;
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

    private Song activeSong;
    private boolean paused = false;

    @FXML
    private void initialize() {
        FXServiceLocator.setPlayerBarController(this);
        vol.setMin(0);
        vol.setMax(1);
        vol.setValue(0.7);
        vol.valueProperty().addListener((obs, old, val) -> {
            FXServiceLocator.getAudioEngine().setVolume(val.doubleValue());
        });
    }

    @FXML
    private void prev() {
        FXServiceLocator.getPlaybackController().playPrev().ifPresent(this::startSong);
    }

    @FXML
    private void toggle() {
        if (activeSong == null) {
            playNextInQueue();
            return;
        }
        if (paused) {
            FXServiceLocator.getAudioEngine().resume();
            playPauseBtn.setText("Pause");
        } else {
            FXServiceLocator.getAudioEngine().pause();
            playPauseBtn.setText("Play");
        }
        paused = !paused;
    }

    @FXML
    private void next() {
        FXServiceLocator.getPlaybackController().playNext().ifPresentOrElse(this::startSong, this::stopPlayback);
    }

    public boolean isIdle() {
        return activeSong == null;
    }

    public void playNextInQueue() {
        FXServiceLocator.getPlaybackController().playNext().ifPresentOrElse(this::startSong, this::stopPlayback);
    }

    private void startSong(Song song) {
        if (song == null) return;
        activeSong = song;
        paused = false;
        nowLabel.setText(song.getTitle() + " • " + song.getArtist());
        timeLabel.setText("Playing");
        playPauseBtn.setText("Pause");
        FXServiceLocator.getAudioEngine().play(song, this::handleEndOfTrack);
    }

    private void stopPlayback() {
        activeSong = null;
        paused = false;
        FXServiceLocator.getAudioEngine().stop();
        nowLabel.setText("No song");
        timeLabel.setText("0:00 / 0:00");
        playPauseBtn.setText("Play");
    }

    private void handleEndOfTrack() {
        FXServiceLocator.getPlaybackController().playNext().ifPresentOrElse(this::startSong, this::stopPlayback);
    }
}

