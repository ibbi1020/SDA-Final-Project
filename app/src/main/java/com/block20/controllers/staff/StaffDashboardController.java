/*
 * Block20 Gym Management System
 * Staff Portal - Dashboard Controller
 */
package com.block20.controllers.staff;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Staff Dashboard view
 * Displays key metrics, quick actions, and today's alerts
 */
public class StaffDashboardController {
    
    private VBox rootView;
    private final String staffName;
    
    public StaffDashboardController(String staffName) {
        this.staffName = staffName;
        initializeView();
    }
    
    /**
     * Initialize the dashboard view with all components
     */
    private void initializeView() {
        rootView = new VBox(24);
        rootView.getStyleClass().add("content-area");
        
        // Create scroll pane for content
        ScrollPane scrollPane = new ScrollPane(rootView);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        // Add all dashboard sections
        rootView.getChildren().addAll(
            createHeader(),
            createStatsSection(),
            createQuickActionsSection(),
            createAlertsSection(),
            createOccupancySection()
        );
    }
    
    /**
     * Create the dashboard header with welcome message and date
     */
    private VBox createHeader() {
        VBox header = new VBox(8);
        header.getStyleClass().add("content-header");
        
        // Page title
        Label title = new Label("STAFF DASHBOARD");
        title.getStyleClass().add("text-h2");
        
        // Welcome message with date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy");
        String dateStr = today.format(formatter);
        
        Label welcome = new Label(String.format("Welcome, %s! — %s", staffName, dateStr));
        welcome.getStyleClass().add("welcome-text");
        
        header.getChildren().addAll(title, welcome);
        return header;
    }
    
    /**
     * Create the statistics cards section
     */
    private VBox createStatsSection() {
        VBox section = new VBox(16);
        
        // Create grid for stat cards
        GridPane grid = new GridPane();
        grid.getStyleClass().add("stats-grid");
        grid.setHgap(24);
        grid.setVgap(24);
        
        // Configure columns for responsive layout
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }
        
        // Add stat cards
        grid.add(createStatCard("Active Members", "1,245", "↑ 23 this month", "success"), 0, 0);
        grid.add(createStatCard("Collections This Month", "$18,500", "↑ $2,300 vs last month", "success"), 1, 0);
        grid.add(createStatCard("Pending Renewals", "47", "⚠ Expiring in 7 days", "warning"), 2, 0);
        
        section.getChildren().add(grid);
        return section;
    }
    
    /**
     * Create a single statistics card
     */
    private VBox createStatCard(String label, String value, String footer, String type) {
        VBox card = new VBox();
        card.getStyleClass().addAll("stat-card");
        card.setAlignment(Pos.TOP_LEFT);
        
        // Card label
        Label cardLabel = new Label(label.toUpperCase());
        cardLabel.getStyleClass().add("card-label");
        
        // Card value
        Label cardValue = new Label(value);
        cardValue.getStyleClass().add("card-value");
        
        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Card footer
        HBox footerBox = new HBox(4);
        footerBox.setAlignment(Pos.CENTER_LEFT);
        footerBox.getStyleClass().add("card-footer");
        
        Label footerLabel = new Label(footer);
        footerBox.getChildren().add(footerLabel);
        
        card.getChildren().addAll(cardLabel, cardValue, spacer, footerBox);
        
        // Add hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-cursor: default;"));
        
        return card;
    }
    
    /**
     * Create the quick actions section
     */
    private VBox createQuickActionsSection() {
        VBox section = new VBox(16);
        
        // Section header
        Label sectionLabel = new Label("🚀 QUICK ACTIONS");
        sectionLabel.getStyleClass().add("text-h4");
        
        // Create card container
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        
        // Create grid for action buttons
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        
        // Add quick action buttons
        grid.add(createQuickActionButton("✏️", "Create Member"), 0, 0);
        grid.add(createQuickActionButton("✓", "Check-In"), 1, 0);
        grid.add(createQuickActionButton("💳", "Payment"), 2, 0);
        grid.add(createQuickActionButton("🔄", "Renew Member"), 0, 1);
        grid.add(createQuickActionButton("📋", "Sessions"), 1, 1);
        grid.add(createQuickActionButton("📊", "Reports"), 2, 1);
        
        card.getChildren().add(grid);
        section.getChildren().addAll(sectionLabel, card);
        return section;
    }
    
    /**
     * Create a quick action button
     */
    private HBox createQuickActionButton(String icon, String text) {
        HBox button = new HBox(8);
        button.getStyleClass().add("quick-action-button");
        button.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("icon");
        
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("label");
        
        button.getChildren().addAll(iconLabel, textLabel);
        
        // Add click handler
        button.setOnMouseClicked(e -> handleQuickAction(text));
        
        return button;
    }
    
    /**
     * Create the alerts section
     */
    private VBox createAlertsSection() {
        VBox section = new VBox(16);
        
        // Section header
        Label sectionLabel = new Label("⚠️ TODAY'S ALERTS");
        sectionLabel.getStyleClass().add("text-h4");
        
        // Create card container
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        
        // Add alert items
        card.getChildren().addAll(
            createAlertItem("• 47 pending renewals (12 overdue, 35 expiring within 7 days)"),
            createAlertItem("• 2 maintenance tasks scheduled today"),
            createAlertItem("• 5 training sessions scheduled")
        );
        
        section.getChildren().addAll(sectionLabel, card);
        return section;
    }
    
    /**
     * Create a single alert item
     */
    private Label createAlertItem(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-body");
        return label;
    }
    
    /**
     * Create the occupancy section
     */
    private VBox createOccupancySection() {
        VBox section = new VBox(16);
        
        // Section header
        Label sectionLabel = new Label("📅 TODAY'S OCCUPANCY");
        sectionLabel.getStyleClass().add("text-h4");
        
        // Create card container
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        
        // Occupancy info
        Label infoLabel = new Label("Current: 87 members | Capacity: 200 (43%)");
        infoLabel.getStyleClass().add("text-body");
        
        // Progress bar
        ProgressBar progressBar = createProgressBar(0.43);
        
        card.getChildren().addAll(infoLabel, progressBar);
        section.getChildren().addAll(sectionLabel, card);
        return section;
    }
    
    /**
     * Create a custom progress bar
     */
    private ProgressBar createProgressBar(double progress) {
        ProgressBar bar = new ProgressBar(progress);
        bar.setPrefHeight(8);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: #2563EB;");
        return bar;
    }
    
    /**
     * Handle quick action button clicks
     */
    private void handleQuickAction(String action) {
        System.out.println("Quick action clicked: " + action);
        // TODO: Implement navigation to respective screens
    }
    
    /**
     * Get the root view for this controller
     */
    public VBox getView() {
        return rootView;
    }
}
