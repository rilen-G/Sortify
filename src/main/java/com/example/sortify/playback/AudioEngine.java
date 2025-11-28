package com.example.sortify.playback;

import com.example.sortify.model.Song;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class AudioEngine {

    private MediaPlayer player;
    private double volume = 0.65;

    public void play(Song s, Runnable onEnd){
        stop();
        String path = s.getFilePath();
        String uri;

        File f = new File(path);
        if (f.exists()) {
            uri = f.toURI().toString();
        } else {
            var res = getClass().getResource("/com/example/sortify/songs/" + path);
            if (res == null){
                System.err.println("Audio file not found: " + path);
                return;
            }
            uri = res.toExternalForm();
        }

        try {
            Media media = new Media(uri);
            player = new MediaPlayer(media);
            player.setVolume(volume);
            player.setOnEndOfMedia(() -> Platform.runLater(onEnd));
            player.play();
        } catch (MediaException e){
            System.err.println("Failed to play media: " + path + " - " + e.getMessage());
        }
    }

    public void pause(){ if (player != null) player.pause(); }
    public void resume(){ if (player != null) player.play(); }

    public void setVolume(double v){
        volume = v;
        if (player != null) player.setVolume(v);
    }

    public void stop(){
        if (player != null){
            player.stop();
            player.dispose();
            player = null;
        }
    }

    public MediaPlayer getPlayer(){ return player; }
}
