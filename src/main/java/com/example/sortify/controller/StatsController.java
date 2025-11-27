package com.example.sortify.controller;

import com.example.sortify.model.Song;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsController {

    @FXML private BarChart<String, Number> topPlays;
    @FXML private CategoryAxis topAxis;
    @FXML private PieChart genrePie;
    @FXML private ListView<Song> recentList;

    @FXML
    public void initialize() {
        recentList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle() + " — " + item.getArtist());
            }
        });
        refresh();
    }

    @FXML
    private void refresh() {
        loadTopPlayed();
        loadGenres();
        loadRecent();
    }

    private void loadTopPlayed(){
        List<Song> all = FXServiceLocator.libraryView();
        List<Song> top = FXServiceLocator.stats().topN(all, 5);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Song s : top){
            series.getData().add(new XYChart.Data<>(s.getTitle(), s.getPlayCount()));
        }
        topAxis.setCategories(FXCollections.observableArrayList(
                top.stream().map(Song::getTitle).collect(Collectors.toList())));
        topPlays.getData().setAll(series);
    }

    private void loadGenres(){
        Map<String, Integer> counts = new HashMap<>();
        for (Song s : FXServiceLocator.libraryView()){
            String g = s.getGenre() == null || s.getGenre().isBlank() ? "Unknown" : s.getGenre();
            counts.merge(g, 1, Integer::sum);
        }
        genrePie.setData(counts.entrySet().stream()
                .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private void loadRecent(){
        List<Song> history = FXServiceLocator.playback().getHistory().stream()
                .limit(10)
                .collect(Collectors.toList());
        recentList.getItems().setAll(history);
    }
}
