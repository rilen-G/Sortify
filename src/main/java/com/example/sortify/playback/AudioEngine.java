package com.example.sortify.playback;

import com.example.sortify.model.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AudioEngine {

    private MediaPlayer player;
    private double volume = 0.65;
    private Path tempCopy;

    public void play(Song s, Runnable onEnd){
        stop();
        String path = s.getFilePath();
        String uri = resolveUri(path, false);
        if (uri == null){
            System.err.println("Audio file not found: " + path);
            return;
        }
        Media media;
        try {
            media = new Media(uri);
        } catch (Exception firstFailure){
            // Disk path may be malformed; try classpath copy as a fallback
            uri = resolveUri(path, true);
            if (uri == null){
                firstFailure.printStackTrace();
                return;
            }
            media = new Media(uri);
        }
        player = new MediaPlayer(media);
        player.setVolume(volume);
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

    public void setVolume(double v){
        volume = v;
        if (player != null){
            player.setVolume(v);
        }
    }

    public void stop(){
        if (player != null){
            player.stop();
            player.dispose();
            player=null;
        }
        if (tempCopy != null){
            try { Files.deleteIfExists(tempCopy); } catch (IOException ignored){}
            tempCopy = null;
        }
    }

    public MediaPlayer getPlayer(){
        return player;
    }

    private String resolveUri(String path, boolean forceClasspath){
        File f = new File(path);
        if (!forceClasspath && f.exists()){
            return f.toURI().toString();
        }
        URL res = getClass().getResource("/com/example/sortify/songs/" + path);
        if (res == null){
            return null;
        }
        try (InputStream in = res.openStream()){
            String suffix = ".tmp";
            int dot = path.lastIndexOf('.');
            if (dot >= 0 && dot < path.length() - 1){
                suffix = path.substring(dot);
            }
            tempCopy = Files.createTempFile("sortify_media_", suffix);
            Files.copy(in, tempCopy, StandardCopyOption.REPLACE_EXISTING);
            tempCopy.toFile().deleteOnExit();
            return tempCopy.toUri().toString();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
