package com.example.sortify.controller;

import com.example.sortify.model.Song;
import com.example.sortify.controller.FXServiceLocator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StatsController {

    @FXML private BarChart<String, Number> topPlays;
    @FXML private CategoryAxis topAxis;
    @FXML private NumberAxis topYAxis;
    @FXML private PieChart genrePie;
    @FXML private ListView<Song> recentList;

    @FXML
    public void initialize() {
        recentList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle() + " - " + item.getArtist());
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

    @FXML
    private void resetStats() {
        FXServiceLocator.library().getLibrary().forEach(song -> song.setPlayCount(0));
        FXServiceLocator.saveStats();
        refresh();
    }

    private void loadTopPlayed(){
        List<Song> all = FXServiceLocator.libraryView();
        List<Song> top = FXServiceLocator.stats().topN(all, 5);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Song s : top){
            series.getData().add(new XYChart.Data<>(wrapTitle(s.getTitle()), s.getPlayCount()));
        }
        int maxPlays = top.stream().mapToInt(Song::getPlayCount).max().orElse(0);
        int tickUnit = maxPlays <= 5 ? 1 : (maxPlays <= 20 ? 2 : 5);
        int upper = Math.max(tickUnit * 2,
                ((maxPlays + tickUnit) / tickUnit) * tickUnit);
        topYAxis.setAutoRanging(false);
        topYAxis.setLowerBound(0);
        topYAxis.setUpperBound(upper);
        topYAxis.setTickUnit(tickUnit);
        topYAxis.setMinorTickCount(0);
        topAxis.setCategories(FXCollections.observableArrayList(
                top.stream().map(s -> wrapTitle(s.getTitle())).collect(Collectors.toList())));
        topPlays.getData().setAll(series);
    }

    private void loadGenres(){
        Map<String, Long> totals = new HashMap<>();
        for (Song s : FXServiceLocator.libraryView()){
            long listen = (long) Math.max(1, s.getDurationSec()) * Math.max(0, s.getPlayCount());
            if (listen <= 0) continue;
            String raw = s.getGenre();
            if (raw == null || raw.isBlank()) {
                totals.merge("Unknown", listen, Long::sum);
                continue;
            }
            String[] parts = raw.split("/");
            for (String part : parts){
                String g = part == null ? "" : part.trim();
                if (g.isEmpty()) g = "Unknown";
                g = titleCase(g);
                totals.merge(g, listen, Long::sum);
            }
        }

        if (totals.isEmpty()){
            genrePie.setData(FXCollections.observableArrayList());
            return;
        }

        List<Map.Entry<String, Long>> sorted = totals.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        List<PieChart.Data> slices = new ArrayList<>();
        Map<PieChart.Data, String> baseNames = new HashMap<>();
        long other = 0;
        for (int i = 0; i < sorted.size(); i++){
            var entry = sorted.get(i);
            if (i < 5) {
                PieChart.Data d = new PieChart.Data(entry.getKey(), entry.getValue());
                baseNames.put(d, entry.getKey());
                slices.add(d);
            } else {
                other += entry.getValue();
            }
        }
        if (other > 0){
            PieChart.Data d = new PieChart.Data("Other", other);
            baseNames.put(d, "Other");
            slices.add(d);
        }

        double total = slices.stream().mapToDouble(PieChart.Data::getPieValue).sum();
        for (PieChart.Data d : slices){
            String name = baseNames.getOrDefault(d, d.getName());
            double pct = total == 0 ? 0 : (d.getPieValue() / total) * 100.0;
            d.setName(String.format("%s (%.1f%%)", name, pct));
        }

        var observable = FXCollections.observableArrayList(slices);
        genrePie.setData(observable);
    }

    private void loadRecent(){
        var historyStack = FXServiceLocator.playback().getHistory();
        List<Song> latestFirst = new ArrayList<>();
        for (int i = historyStack.size() - 1; i >= 0 && latestFirst.size() < 10; i--){
            latestFirst.add(historyStack.get(i));
        }
        recentList.getItems().setAll(latestFirst);
    }

    private String titleCase(String value){
        if (value == null || value.isBlank()) return "Unknown";
        String[] words = value.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++){
            String w = words[i];
            if (w.isEmpty()) continue;
            sb.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) sb.append(w.substring(1));
            if (i < words.length - 1) sb.append(" ");
        }
        String out = sb.toString().trim();
        return out.isEmpty() ? "Unknown" : out;
    }

    private String wrapTitle(String title){
        if (title == null) return "";
        String trimmed = title.trim();
        int maxLen = 14;
        if (trimmed.length() <= maxLen) return trimmed;
        StringBuilder out = new StringBuilder();
        StringBuilder line = new StringBuilder();
        for (String word : trimmed.split("\\s+")){
            if (line.length() + word.length() + 1 > maxLen){
                if (!out.isEmpty()) out.append("\n");
                out.append(line.toString().trim());
                line.setLength(0);
            }
            if (line.length() > 0) line.append(" ");
            line.append(word);
        }
        if (line.length() > 0){
            if (!out.isEmpty()) out.append("\n");
            out.append(line.toString().trim());
        }
        return out.toString();
    }
}
