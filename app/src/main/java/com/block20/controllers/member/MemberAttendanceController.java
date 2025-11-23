/*
 * Block20 Gym Management System
 * Member Attendance Controller - Real Data Integration
 */
package com.block20.controllers.member;

import com.block20.models.Attendance;
import com.block20.services.MemberService;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class MemberAttendanceController extends ScrollPane {
    
    private VBox contentContainer;
    private final String memberId;
    private final MemberService memberService;
    
    // UI Containers for dynamic updates
    private HBox statsRow;
    private VBox visitsList;
    
    public MemberAttendanceController(String memberId, MemberService memberService) {
        this.memberId = memberId;
        this.memberService = memberService;
        initializeView();
        loadRealData(); // Fetch data immediately
    }
    
    private void initializeView() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.setPadding(new Insets(32));
        contentContainer.getStyleClass().add("main-content");
        
        Text title = new Text("Attendance History");
        title.getStyleClass().add("text-h2");
        
        Text subtitle = new Text("Track your gym visits and activity");
        subtitle.getStyleClass().add("text-muted");
        
        // Stats cards container (will be populated by loadRealData)
        statsRow = new HBox(16);
        
        // Recent visits container
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        Text cardTitle = new Text("Recent Check-Ins");
        cardTitle.getStyleClass().add("text-h3");
        
        visitsList = new VBox(12); // Container for list items
        
        card.getChildren().addAll(cardTitle, visitsList);
        contentContainer.getChildren().addAll(title, subtitle, statsRow, card);
        setContent(contentContainer);
    }
    
    private void loadRealData() {
        // 1. Fetch History
        List<Attendance> history = memberService.getAttendanceForMember(memberId);
        
        // 2. Calculate Stats
        long thisMonth = history.stream()
            .filter(a -> a.getCheckInTime().getMonth() == LocalDate.now().getMonth() && 
                         a.getCheckInTime().getYear() == LocalDate.now().getYear())
            .count();
            
        long thisYear = history.stream()
            .filter(a -> a.getCheckInTime().getYear() == LocalDate.now().getYear())
            .count();
            
        long totalVisits = history.size();

        // 3. Populate Stats Cards
        statsRow.getChildren().clear();
        statsRow.getChildren().addAll(
            createStatCard("This Month", thisMonth + " visits"),
            createStatCard("This Year", thisYear + " visits"),
            createStatCard("Total All Time", totalVisits + " visits")
        );

        // 4. Populate Visits List
        visitsList.getChildren().clear();
        
        if (history.isEmpty()) {
            Label empty = new Label("No visits recorded yet.");
            empty.getStyleClass().add("text-muted");
            visitsList.getChildren().add(empty);
        } else {
            // Sort by newest first
            history.sort(Comparator.comparing(Attendance::getCheckInTime).reversed());
            
            for (Attendance visit : history) {
                visitsList.getChildren().add(createVisitRow(visit));
            }
        }
    }
    
    private VBox createStatCard(String label, String value) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        
        Text valueText = new Text(value);
        valueText.getStyleClass().add("text-h3");
        valueText.setStyle("-fx-fill: -fx-primary-500;");
        
        card.getChildren().addAll(labelText, valueText);
        return card;
    }
    
    private HBox createVisitRow(Attendance visit) {
        HBox row = new HBox(16);
        row.setStyle("-fx-padding: 12; -fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E2E8F0;");
        
        // Date Column
        Text dateText = new Text(visit.getCheckInTime().format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")));
        dateText.getStyleClass().add("text-body");
        dateText.setWrappingWidth(200);
        
        // Time Column
        String timeStr = visit.getCheckInTime().format(DateTimeFormatter.ofPattern("h:mm a"));
        Text timeInOut = new Text("In: " + timeStr);
        timeInOut.getStyleClass().add("text-body-sm");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Duration / Status Column
        Text durationText;
        if (visit.getCheckOutTime() == null) {
            durationText = new Text("● Active Now");
            durationText.setStyle("-fx-fill: #10B981; -fx-font-weight: bold;");
        } else {
            long minutes = Duration.between(visit.getCheckInTime(), visit.getCheckOutTime()).toMinutes();
            long h = minutes / 60;
            long m = minutes % 60;
            String durationStr = (h > 0 ? h + "h " : "") + m + "m";
            
            durationText = new Text(durationStr);
            durationText.getStyleClass().add("text-body-sm");
            durationText.setStyle("-fx-fill: #64748B;");
        }
        
        row.getChildren().addAll(dateText, timeInOut, spacer, durationText);
        return row;
    }
}