/*
 * Block20 Gym Management System
 * Member Attendance Controller
 */
package com.block20.controllers.member;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberAttendanceController extends ScrollPane {
    
    private VBox contentContainer;
    
    public MemberAttendanceController(String memberId) {
        initializeView();
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
        
        // Stats cards
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            createStatCard("This Month", "12 visits"),
            createStatCard("This Year", "87 visits"),
            createStatCard("Current Streak", "5 days")
        );
        
        // Recent visits
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        Text cardTitle = new Text("Recent Check-Ins");
        cardTitle.getStyleClass().add("text-h3");
        
        VBox visitsList = new VBox(12);
        for (int i = 1; i <= 10; i++) {
            visitsList.getChildren().add(createVisitRow(LocalDate.now().minusDays(i)));
        }
        
        card.getChildren().addAll(cardTitle, visitsList);
        contentContainer.getChildren().addAll(title, subtitle, statsRow, card);
        setContent(contentContainer);
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
    
    private HBox createVisitRow(LocalDate date) {
        HBox row = new HBox(16);
        row.setStyle("-fx-padding: 12; -fx-background-color: -fx-gray-50; -fx-background-radius: 8;");
        
        Text dateText = new Text(date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")));
        dateText.getStyleClass().add("text-body");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Text timeText = new Text("2h 15m");
        timeText.getStyleClass().add("text-body-sm");
        timeText.setStyle("-fx-fill: -fx-gray-600;");
        
        row.getChildren().addAll(dateText, spacer, timeText);
        return row;
    }
}
