package com.block20.controllers;

import com.block20.models.Attendance;
import com.block20.services.MemberService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OperationalReportsController extends ScrollPane {
    
    private VBox contentContainer;
    private Consumer<String> navigationHandler;
    private MemberService memberService;

    public OperationalReportsController(Consumer<String> navigationHandler, MemberService memberService) {
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        initialize();
    }

    private void initialize() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.getStyleClass().add("main-content");
        contentContainer.setPadding(new Insets(32));

        contentContainer.getChildren().addAll(
            createHeader(),
            createStatsCards(),
            createPeakHoursTable(),
            createRecentLogTable()
        );
        
        setContent(contentContainer);
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        Text title = new Text("Operational Reports");
        title.getStyleClass().add("text-h2");
        Text subtitle = new Text("Analyze facility usage, peak hours, and attendance trends");
        subtitle.getStyleClass().add("text-muted");
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createStatsCards() {
        HBox cards = new HBox(20);
        List<Attendance> logs = memberService.getAllAttendanceRecords();
        
        // Metric 1: Total Visits (All time)
        int totalVisits = logs.size();
        
        // Metric 2: Average Daily Visits (Mock logic: assume data spans 30 days if small)
        double avgVisits = totalVisits > 0 ? (double) totalVisits / Math.max(1, logs.stream().map(a -> a.getCheckInTime().toLocalDate()).distinct().count()) : 0;
        
        // Metric 3: Busiest Day
        String busiestDay = "N/A";
        if (!logs.isEmpty()) {
            Map<String, Long> dayCounts = logs.stream()
                .collect(Collectors.groupingBy(a -> a.getCheckInTime().getDayOfWeek().toString(), Collectors.counting()));
            busiestDay = Collections.max(dayCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
        }

        cards.getChildren().addAll(
            createCard("Total Visits", String.valueOf(totalVisits), "#2563EB"),
            createCard("Avg Daily Visits", String.format("%.1f", avgVisits), "#10B981"),
            createCard("Busiest Day", busiestDay, "#F59E0B")
        );
        return cards;
    }

    private VBox createCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setPrefWidth(200);
        
        Text valText = new Text(value);
        valText.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: " + color + ";");
        
        Text titleText = new Text(title);
        titleText.getStyleClass().add("text-caption");
        
        card.getChildren().addAll(valText, titleText);
        return card;
    }

    private VBox createPeakHoursTable() {
        VBox container = new VBox(12);
        container.getStyleClass().add("card");
        container.setPadding(new Insets(20));
        
        Text title = new Text("Visits by Hour (Peak Times)");
        title.getStyleClass().add("text-h4");
        
        TableView<HourStat> table = new TableView<>();
        table.setPrefHeight(200);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<HourStat, String> hourCol = new TableColumn<>("Hour");
        hourCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().hour));
        
        TableColumn<HourStat, String> countCol = new TableColumn<>("Total Check-Ins");
        countCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().count)));
        
        table.getColumns().addAll(hourCol, countCol);
        
        // Calculate Data
        Map<Integer, Long> hourlyCounts = memberService.getAllAttendanceRecords().stream()
            .collect(Collectors.groupingBy(a -> a.getCheckInTime().getHour(), Collectors.counting()));
            
        ObservableList<HourStat> data = FXCollections.observableArrayList();
        // Fill 24 hours
        for (int i = 6; i < 22; i++) { // 6 AM to 10 PM
            String timeLabel = String.format("%02d:00 - %02d:00", i, i+1);
            data.add(new HourStat(timeLabel, hourlyCounts.getOrDefault(i, 0L)));
        }
        table.setItems(data);
        
        container.getChildren().addAll(title, table);
        return container;
    }

    private VBox createRecentLogTable() {
        VBox container = new VBox(12);
        container.getStyleClass().add("card");
        container.setPadding(new Insets(20));
        
        Text title = new Text("Raw Attendance Log");
        title.getStyleClass().add("text-h4");
        
        TableView<Attendance> table = new TableView<>();
        table.setPrefHeight(250);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Attendance, String> nameCol = new TableColumn<>("Member");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMemberName()));
        
        TableColumn<Attendance, String> inCol = new TableColumn<>("Check In");
        inCol.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getCheckInTime().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
        ));
        
        TableColumn<Attendance, String> outCol = new TableColumn<>("Check Out");
        outCol.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getCheckOutTime() == null ? "-" : 
            d.getValue().getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        ));
        
        table.getColumns().addAll(nameCol, inCol, outCol);
        
        List<Attendance> logs = new ArrayList<>(memberService.getAllAttendanceRecords());
        Collections.reverse(logs); // Newest first
        table.setItems(FXCollections.observableArrayList(logs));
        
        container.getChildren().addAll(title, table);
        return container;
    }
    
    // Helper class for table
    private static class HourStat {
        String hour;
        long count;
        public HourStat(String h, long c) { this.hour = h; this.count = c; }
    }
}