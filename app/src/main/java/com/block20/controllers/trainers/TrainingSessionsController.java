/*
 * Block20 Gym Management System
 * Training Sessions Controller - Schedule and Manage Training Sessions
 */
package com.block20.controllers.trainers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Controller for training sessions management:
 * 1. View All Sessions (upcoming, completed, cancelled)
 * 2. Schedule New Session
 * 3. Cancel/Reschedule Session
 * 4. Mark Attendance
 */
public class TrainingSessionsController extends ScrollPane {
    
    private VBox contentContainer;
    private Consumer<String> navigationHandler;
    
    // Current filter state
    private String currentFilter = "Upcoming"; // Upcoming, Completed, Cancelled, All
    
    /**
     * Constructor
     */
    public TrainingSessionsController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        initializeView();
    }
    
    /**
     * Initialize the main view
     */
    private void initializeView() {
        // Configure ScrollPane
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(30));
        contentContainer.getStyleClass().add("content-container");
        
        // Header
        HBox header = createHeader();
        
        // Action bar
        HBox actionBar = createActionBar();
        
        // Filter tabs
        HBox filterTabs = createFilterTabs();
        
        // Stats bar
        HBox statsBar = createStatsBar();
        
        // Sessions table
        VBox tableSection = createSessionsTable();
        
        contentContainer.getChildren().addAll(header, actionBar, filterTabs, statsBar, tableSection);
        
        // Set content
        setContent(contentContainer);
    }
    
    // ==================== MAIN VIEW COMPONENTS ====================
    
    /**
     * Create header
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Training Sessions");
        titleLabel.getStyleClass().add("page-title");
        
        Label subtitleLabel = new Label("Schedule and manage personal training sessions");
        subtitleLabel.getStyleClass().add("page-subtitle");
        
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    /**
     * Create action bar
     */
    private HBox createActionBar() {
        HBox actionBar = new HBox(15);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        
        // Date picker for quick navigation
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Select date");
        
        Label dateLabel = new Label("Jump to date:");
        dateLabel.getStyleClass().add("form-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Schedule session button
        Button scheduleButton = new Button("+ Schedule Session");
        scheduleButton.getStyleClass().add("btn-primary");
        scheduleButton.setOnAction(e -> showScheduleSessionDialog());
        
        actionBar.getChildren().addAll(dateLabel, datePicker, spacer, scheduleButton);
        
        return actionBar;
    }
    
    /**
     * Create filter tabs
     */
    private HBox createFilterTabs() {
        HBox tabsBox = new HBox(0);
        tabsBox.setAlignment(Pos.CENTER_LEFT);
        tabsBox.getStyleClass().add("filter-tabs");
        
        ToggleGroup filterGroup = new ToggleGroup();
        
        ToggleButton upcomingTab = createFilterTab("Upcoming (12)", filterGroup, true);
        upcomingTab.setOnAction(e -> applyFilter("Upcoming"));
        
        ToggleButton todayTab = createFilterTab("Today (5)", filterGroup, false);
        todayTab.setOnAction(e -> applyFilter("Today"));
        
        ToggleButton completedTab = createFilterTab("Completed (48)", filterGroup, false);
        completedTab.setOnAction(e -> applyFilter("Completed"));
        
        ToggleButton cancelledTab = createFilterTab("Cancelled (3)", filterGroup, false);
        cancelledTab.setOnAction(e -> applyFilter("Cancelled"));
        
        tabsBox.getChildren().addAll(upcomingTab, todayTab, completedTab, cancelledTab);
        
        return tabsBox;
    }
    
    /**
     * Create filter tab
     */
    private ToggleButton createFilterTab(String text, ToggleGroup group, boolean selected) {
        ToggleButton tab = new ToggleButton(text);
        tab.setToggleGroup(group);
        tab.setSelected(selected);
        tab.getStyleClass().add("filter-tab");
        return tab;
    }
    
    /**
     * Create stats bar
     */
    private HBox createStatsBar() {
        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(15));
        statsBar.getStyleClass().add("stats-bar");
        
        statsBar.getChildren().addAll(
            createStatItem("Today's Sessions", "5", "#3B82F6"),
            createStatItem("This Week", "18", "#10B981"),
            createStatItem("This Month", "63", "#8B5CF6"),
            createStatItem("Revenue (Month)", "$3,350", "#F59E0B")
        );
        
        return statsBar;
    }
    
    /**
     * Create stat item
     */
    private VBox createStatItem(String label, String value, String color) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(10));
        statBox.getStyleClass().add("stat-item");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        statBox.getChildren().addAll(valueLabel, labelText);
        
        return statBox;
    }
    
    /**
     * Create sessions table
     */
    private VBox createSessionsTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");
        
        // Table header
        HBox tableHeader = new HBox();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(10));
        
        Label dateCol = new Label("Date & Time");
        dateCol.setPrefWidth(150);
        dateCol.getStyleClass().add("table-header-cell");
        
        Label memberCol = new Label("Member");
        memberCol.setPrefWidth(200);
        memberCol.getStyleClass().add("table-header-cell");
        
        Label trainerCol = new Label("Trainer");
        trainerCol.setPrefWidth(180);
        trainerCol.getStyleClass().add("table-header-cell");
        
        Label typeCol = new Label("Session Type");
        typeCol.setPrefWidth(150);
        typeCol.getStyleClass().add("table-header-cell");
        
        Label durationCol = new Label("Duration");
        durationCol.setPrefWidth(100);
        durationCol.getStyleClass().add("table-header-cell");
        
        Label statusCol = new Label("Status");
        statusCol.setPrefWidth(120);
        statusCol.getStyleClass().add("table-header-cell");
        
        Label actionCol = new Label("Actions");
        actionCol.setPrefWidth(200);
        actionCol.getStyleClass().add("table-header-cell");
        
        tableHeader.getChildren().addAll(dateCol, memberCol, trainerCol, typeCol, durationCol, statusCol, actionCol);
        
        // Table rows
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("table-scroll");
        
        VBox tableRows = new VBox(5);
        tableRows.setPadding(new Insets(10));
        
        // Generate mock data
        List<SessionData> sessionsList = generateMockSessions();
        
        for (SessionData session : sessionsList) {
            tableRows.getChildren().add(createTableRow(session));
        }
        
        scrollPane.setContent(tableRows);
        
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }
    
    /**
     * Create table row
     */
    private HBox createTableRow(SessionData session) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Date & Time
        VBox dateTimeBox = new VBox(2);
        dateTimeBox.setPrefWidth(150);
        Label dateLabel = new Label(session.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.setStyle("-fx-font-weight: 500;");
        Label timeLabel = new Label(session.time.format(DateTimeFormatter.ofPattern("hh:mm a")));
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);
        
        // Member
        Label memberLabel = new Label(session.memberName);
        memberLabel.setPrefWidth(200);
        
        // Trainer
        Label trainerLabel = new Label(session.trainerName);
        trainerLabel.setPrefWidth(180);
        trainerLabel.setStyle("-fx-font-weight: 500;");
        
        // Session Type
        Label typeLabel = new Label(session.sessionType);
        typeLabel.setPrefWidth(150);
        
        // Duration
        Label durationLabel = new Label(session.duration + " min");
        durationLabel.setPrefWidth(100);
        
        // Status badge
        Label statusBadge = new Label(session.status);
        statusBadge.setPrefWidth(120);
        statusBadge.getStyleClass().add("badge");
        switch (session.status) {
            case "Scheduled":
                statusBadge.getStyleClass().add("badge-info");
                break;
            case "Completed":
                statusBadge.getStyleClass().add("badge-success");
                break;
            case "Cancelled":
                statusBadge.getStyleClass().add("badge-error");
                break;
            case "In Progress":
                statusBadge.getStyleClass().add("badge-warning");
                break;
        }
        
        // Action buttons
        HBox actionBox = new HBox(8);
        actionBox.setPrefWidth(200);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        if (session.status.equals("Scheduled")) {
            Button viewButton = new Button("View");
            viewButton.getStyleClass().add("btn-primary-small");
            viewButton.setOnAction(e -> viewSession(session));
            
            Button cancelButton = new Button("Cancel");
            cancelButton.getStyleClass().add("btn-secondary-small");
            cancelButton.setOnAction(e -> cancelSession(session));
            
            actionBox.getChildren().addAll(viewButton, cancelButton);
        } else if (session.status.equals("Completed")) {
            Button viewButton = new Button("View");
            viewButton.getStyleClass().add("btn-primary-small");
            viewButton.setOnAction(e -> viewSession(session));
            
            actionBox.getChildren().add(viewButton);
        }
        
        row.getChildren().addAll(dateTimeBox, memberLabel, trainerLabel, typeLabel, durationLabel, statusBadge, actionBox);
        
        return row;
    }
    
    /**
     * Apply filter
     */
    private void applyFilter(String filter) {
        currentFilter = filter;
        System.out.println("Filter applied: " + filter);
        // In real implementation, this would refresh the table with filtered data
    }
    
    // ==================== DIALOG VIEWS ====================
    
    /**
     * Show schedule session dialog
     */
    private void showScheduleSessionDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Schedule Training Session");
        dialog.setHeaderText("Book a new training session");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Member selection
        ComboBox<String> memberBox = new ComboBox<>();
        memberBox.getItems().addAll(
            "M1001 - John Smith",
            "M1002 - Sarah Johnson",
            "M1003 - Mike Chen",
            "M1004 - Emma Davis",
            "M1005 - Lisa Martinez"
        );
        memberBox.setPromptText("Select member");
        memberBox.setPrefWidth(300);
        
        // Trainer selection
        ComboBox<String> trainerBox = new ComboBox<>();
        trainerBox.getItems().addAll(
            "Mike Johnson - Personal Training",
            "Sarah Williams - Yoga",
            "David Chen - CrossFit",
            "Emily Rodriguez - Pilates",
            "James Anderson - Strength Training"
        );
        trainerBox.setPromptText("Select trainer");
        trainerBox.setPrefWidth(300);
        
        // Session type
        ComboBox<String> sessionTypeBox = new ComboBox<>();
        sessionTypeBox.getItems().addAll(
            "Personal Training",
            "Yoga Session",
            "CrossFit Training",
            "Pilates",
            "Strength Training",
            "Cardio Training"
        );
        sessionTypeBox.setPromptText("Select session type");
        sessionTypeBox.setPrefWidth(300);
        
        // Date picker
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setPrefWidth(300);
        
        // Time selection
        ComboBox<String> timeBox = new ComboBox<>();
        for (int hour = 6; hour <= 20; hour++) {
            for (int min = 0; min < 60; min += 30) {
                String time = String.format("%02d:%02d", hour, min);
                timeBox.getItems().add(time);
            }
        }
        timeBox.setPromptText("Select time");
        timeBox.setPrefWidth(300);
        
        // Duration
        ComboBox<String> durationBox = new ComboBox<>();
        durationBox.getItems().addAll("30 minutes", "45 minutes", "60 minutes", "90 minutes");
        durationBox.setValue("60 minutes");
        durationBox.setPrefWidth(300);
        
        // Notes
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Session notes or special instructions...");
        notesArea.setPrefRowCount(3);
        notesArea.setPrefWidth(300);
        
        // Add to grid
        grid.add(new Label("Member:"), 0, 0);
        grid.add(memberBox, 1, 0);
        grid.add(new Label("Trainer:"), 0, 1);
        grid.add(trainerBox, 1, 1);
        grid.add(new Label("Session Type:"), 0, 2);
        grid.add(sessionTypeBox, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Time:"), 0, 4);
        grid.add(timeBox, 1, 4);
        grid.add(new Label("Duration:"), 0, 5);
        grid.add(durationBox, 1, 5);
        grid.add(new Label("Notes:"), 0, 6);
        grid.add(notesArea, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate
                if (memberBox.getValue() == null || trainerBox.getValue() == null || 
                    sessionTypeBox.getValue() == null || timeBox.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Missing Required Fields");
                    alert.setContentText("Please fill in all required fields.");
                    alert.showAndWait();
                    return;
                }
                
                // Generate session ID
                String sessionId = "S" + System.currentTimeMillis();
                
                System.out.println("\n=== Training Session Scheduled ===");
                System.out.println("Session ID: " + sessionId);
                System.out.println("Member: " + memberBox.getValue());
                System.out.println("Trainer: " + trainerBox.getValue());
                System.out.println("Session Type: " + sessionTypeBox.getValue());
                System.out.println("Date: " + datePicker.getValue());
                System.out.println("Time: " + timeBox.getValue());
                System.out.println("Duration: " + durationBox.getValue());
                System.out.println("Notes: " + notesArea.getText());
                System.out.println("=================================\n");
                
                // Show success
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Session Scheduled Successfully");
                success.setContentText("Session ID: " + sessionId + "\n" +
                                      "Date: " + datePicker.getValue() + " at " + timeBox.getValue() + "\n" +
                                      "Confirmation emails sent to member and trainer.");
                success.showAndWait();
            }
        });
    }
    
    /**
     * View session details
     */
    private void viewSession(SessionData session) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Session Details");
        dialog.setHeaderText("Training Session #" + session.sessionId);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        // Session Information
        VBox sessionInfo = new VBox(10);
        sessionInfo.getStyleClass().add("info-card");
        sessionInfo.setPadding(new Insets(15));
        
        Label sessionTitle = new Label("Session Information");
        sessionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane sessionGrid = new GridPane();
        sessionGrid.setHgap(20);
        sessionGrid.setVgap(8);
        
        addDetailRow(sessionGrid, 0, "Session ID:", session.sessionId);
        addDetailRow(sessionGrid, 1, "Date:", session.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addDetailRow(sessionGrid, 2, "Time:", session.time.format(DateTimeFormatter.ofPattern("hh:mm a")));
        addDetailRow(sessionGrid, 3, "Duration:", session.duration + " minutes");
        addDetailRow(sessionGrid, 4, "Session Type:", session.sessionType);
        addDetailRow(sessionGrid, 5, "Status:", session.status);
        
        sessionInfo.getChildren().addAll(sessionTitle, sessionGrid);
        
        // Participant Information
        VBox participantInfo = new VBox(10);
        participantInfo.getStyleClass().add("info-card");
        participantInfo.setPadding(new Insets(15));
        
        Label participantTitle = new Label("Participants");
        participantTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane participantGrid = new GridPane();
        participantGrid.setHgap(20);
        participantGrid.setVgap(8);
        
        addDetailRow(participantGrid, 0, "Member:", session.memberName);
        addDetailRow(participantGrid, 1, "Trainer:", session.trainerName);
        
        participantInfo.getChildren().addAll(participantTitle, participantGrid);
        
        content.getChildren().addAll(sessionInfo, participantInfo);
        
        dialog.getDialogPane().setContent(content);
        
        if (session.status.equals("Scheduled")) {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        }
        
        dialog.showAndWait();
    }
    
    /**
     * Cancel session
     */
    private void cancelSession(SessionData session) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Session");
        alert.setHeaderText("Cancel Training Session");
        alert.setContentText("Are you sure you want to cancel this session?\n\n" +
                             "Member: " + session.memberName + "\n" +
                             "Trainer: " + session.trainerName + "\n" +
                             "Date: " + session.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + 
                             " at " + session.time.format(DateTimeFormatter.ofPattern("hh:mm a")));
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("\n=== Session Cancelled ===");
                System.out.println("Session ID: " + session.sessionId);
                System.out.println("Member: " + session.memberName);
                System.out.println("Trainer: " + session.trainerName);
                System.out.println("========================\n");
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Session Cancelled");
                success.setContentText("The session has been cancelled.\nNotifications sent to member and trainer.");
                success.showAndWait();
                
                // Refresh list (in real implementation)
            }
        });
    }
    
    /**
     * Add detail row
     */
    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelText = new Label(label);
        labelText.getStyleClass().add("form-label");
        labelText.setStyle("-fx-font-weight: 500; -fx-min-width: 120px;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 14px;");
        
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }
    
    // ==================== DATA GENERATION ====================
    
    /**
     * Generate mock session data
     */
    private List<SessionData> generateMockSessions() {
        List<SessionData> sessions = new ArrayList<>();
        Random random = new Random();
        
        String[] members = {"John Smith", "Sarah Johnson", "Mike Chen", "Emma Davis", "Lisa Martinez"};
        String[] trainers = {"Mike Johnson", "Sarah Williams", "David Chen", "Emily Rodriguez"};
        String[] sessionTypes = {"Personal Training", "Yoga Session", "CrossFit Training", "Pilates", "Strength Training"};
        String[] statuses = {"Scheduled", "Scheduled", "Scheduled", "Completed", "Cancelled"};
        
        // Generate 12 sessions
        for (int i = 0; i < 12; i++) {
            SessionData session = new SessionData();
            session.sessionId = "S" + (10001 + i);
            
            // Date distribution
            if (i < 5) {
                session.date = LocalDate.now(); // Today
                session.status = "Scheduled";
            } else if (i < 12) {
                session.date = LocalDate.now().plusDays(random.nextInt(7) + 1); // Next 7 days
                session.status = "Scheduled";
            } else {
                session.date = LocalDate.now().minusDays(random.nextInt(30) + 1); // Past
                session.status = random.nextBoolean() ? "Completed" : "Cancelled";
            }
            
            session.time = LocalTime.of(6 + random.nextInt(14), random.nextBoolean() ? 0 : 30);
            session.memberName = members[random.nextInt(members.length)];
            session.trainerName = trainers[random.nextInt(trainers.length)];
            session.sessionType = sessionTypes[random.nextInt(sessionTypes.length)];
            session.duration = 30 + (random.nextInt(3) * 15); // 30, 45, 60, or 75 minutes
            
            sessions.add(session);
        }
        
        // Sort by date and time
        sessions.sort((s1, s2) -> {
            int dateCompare = s1.date.compareTo(s2.date);
            if (dateCompare != 0) return dateCompare;
            return s1.time.compareTo(s2.time);
        });
        
        return sessions;
    }
    
    // ==================== DATA CLASS ====================
    
    /**
     * Session data class
     */
    private static class SessionData {
        String sessionId;
        LocalDate date;
        LocalTime time;
        String memberName;
        String trainerName;
        String sessionType;
        int duration;
        String status;
    }
}
