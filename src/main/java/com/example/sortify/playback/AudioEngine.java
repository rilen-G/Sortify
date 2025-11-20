package com.example.sortify.playback;

import com.example.sortify.model.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class AudioEngine {

    private MediaPlayer player;

    public void play(Song s, Runnable onEnd){
        stop();
        String uri = new File(s.getFilePath()).toURI().toString();
        Media media = new Media(uri);
        player = new MediaPlayer(media);
        player.setOnEndOfMedia(onEnd);
        player.play();
    }

    public void pause(){
        if (player != null){
            player.pause();
        }
    }

    public void resume(){
        if (player != null){
            player.play();
        }
    }

    public void stop(){
        if (player != null){
            player.stop();
            player.dispose();
            player=null;
        }
    }

    public void setVolume(double volume) {
        if (player != null) {
            player.setVolume(Math.max(0, Math.min(1, volume)));
        }
    }
}

