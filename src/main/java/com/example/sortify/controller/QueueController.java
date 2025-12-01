package com.example.sortify.controller;

import com.example.sortify.model.Song;
import com.example.sortify.playback.PlaybackController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.SelectionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class QueueController {

    @FXML
    private TableView<Song> queueTable;
    @FXML
    private TableColumn<Song, Number> colNum;
    @FXML
    private TableColumn<Song, String> colTitle;
    @FXML
    private TableColumn<Song, String> colArtist;
    @FXML
    private TableColumn<Song, String> colAlbum;
    @FXML
    private TableColumn<Song, String> colDuration;

    private PlaybackController playbackController;

    private final ObservableList<Song> localQueue = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        FXServiceLocator.registerQueueController(this);
        playbackController = FXServiceLocator.playback();
        queueTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        colNum.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(queueTable.getItems().indexOf(cellData.getValue()) + 1));

        colTitle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));
        colArtist.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getArtist()));
        colAlbum.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAlbum()));

        colDuration.setCellValueFactory(cell ->
                new SimpleStringProperty(formatDuration(cell.getValue().getDurationSec())));

        queueTable.setItems(localQueue);

        refreshView();

        // Set a listener to trigger manual refresh when the backend queue changes.
        playbackController.setOnChange(this::refreshView);
    }

    public void refreshView() {
        localQueue.setAll(playbackController.getQueue());
        queueTable.refresh();
    }

    // Clears all songs from the playback queue.
    @FXML
    private void clearQueue() {
        if (!playbackController.getQueue().isEmpty()) {
            playbackController.resetQueue(null);
            FXServiceLocator.savePlaybackState();
        } else {
            showStatusMessage("Queue is already empty.", AlertType.WARNING);
        }
    }

    //Removes the selected song from the queue.
    @FXML
    private void playSelected() {
        Song selectedSong = queueTable.getSelectionModel().getSelectedItem();
        if (selectedSong == null) {
            showStatusMessage("Please select a song to play.", AlertType.WARNING);
            return;
        }

        Queue<Song> backendQueue = playbackController.getQueue();
        List<Song> queueAsList = new ArrayList<>(backendQueue);
        int selectedIndex = queueTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) return;

        // Remove all songs before the selected song
        for (int i = 0; i < selectedIndex; i++) {
            queueAsList.remove(0);
        }

        // Reset the queue: selected song is now first
        playbackController.resetQueue(queueAsList);
        playbackController.playNext();
        FXServiceLocator.savePlaybackState();
    }

    //Removes the selected song from the queue.
    @FXML
    private void removeSelected() {
        Song selectedSong = queueTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            playbackController.getQueue().remove(selectedSong);
            FXServiceLocator.savePlaybackState();
        } else {
            showStatusMessage("Please select a song to remove.", AlertType.WARNING);
        }
    }

    //Moves the selected song up one position.
    @FXML
    private void moveUp() {
        int selectedIndex = queueTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex <= 0) return;

        List<Song> queueAsList = new ArrayList<>(playbackController.getQueue());

        Song songToMove = queueAsList.get(selectedIndex);
        queueAsList.remove(selectedIndex);
        queueAsList.add(selectedIndex - 1, songToMove);

        playbackController.resetQueue(queueAsList);
        queueTable.getSelectionModel().select(selectedIndex - 1);
        FXServiceLocator.savePlaybackState();
    }

    // Moves the selected song down one position.
    @FXML
    private void moveDown() {
        int selectedIndex = queueTable.getSelectionModel().getSelectedIndex();
        List<Song> queueAsList = new ArrayList<>(playbackController.getQueue());

        if (selectedIndex == -1 || selectedIndex >= queueAsList.size() - 1) return;

        Song songToMove = queueAsList.get(selectedIndex);
        queueAsList.remove(selectedIndex);
        queueAsList.add(selectedIndex + 1, songToMove);

        playbackController.resetQueue(queueAsList);
        queueTable.getSelectionModel().select(selectedIndex + 1);
        FXServiceLocator.savePlaybackState();
    }

    private String formatDuration(int sec){
        int minutes = sec / 60;
        int seconds = sec % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    //Helper method to show an alert message.
    private void showStatusMessage(String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Queue");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}