/*
 * Block20 Gym Management System
 * Staff Portal - Dashboard Controller
 */
package com.block20.controllers.staff;

import com.block20.models.Transaction;
import com.block20.services.EquipmentService;
import com.block20.services.MemberService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class StaffDashboardController extends ScrollPane {
    
    private VBox contentContainer;
    private final String staffName;
    
    // Dependencies
    private MemberService memberService;
    private EquipmentService equipmentService;
    private final Consumer<String> navigationHandler;
    
    public StaffDashboardController(String staffName,
                                    MemberService memberService,
                                    EquipmentService equipmentService,
                                    Consumer<String> navigationHandler) {
        this.staffName = staffName;
        this.memberService = memberService;
        this.equipmentService = equipmentService;
        this.navigationHandler = navigationHandler;
        initializeView();
    }
    
    private void initializeView() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.getStyleClass().add("content-area");
        
        contentContainer.getChildren().addAll(
            createHeader(),
            createStatsSection(),
            createQuickActionsSection(),
            createAlertsSection(),
            createOccupancySection()
        );
        
        setContent(contentContainer);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(8);
        header.getStyleClass().add("content-header");
        
        Label title = new Label("STAFF DASHBOARD");
        title.getStyleClass().add("text-h2");
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy");
        String dateStr = today.format(formatter);
        
        Label welcome = new Label(String.format("Welcome, %s! — %s", staffName, dateStr));
        welcome.getStyleClass().add("welcome-text");
        
        header.getChildren().addAll(title, welcome);
        return header;
    }
    
    // --- REAL DATA METRICS ---
    private VBox createStatsSection() {
        VBox section = new VBox(16);
        GridPane grid = new GridPane();
        grid.getStyleClass().add("stats-grid");
        grid.setHgap(24);
        grid.setVgap(24);
        
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }
        
        // 1. Count Active Members
        long activeCount = memberService.getAllMembers().stream()
            .filter(m -> "Active".equalsIgnoreCase(m.getStatus()))
            .count();
            
        // 2. Calculate This Month's Collections
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        double collections = memberService.getAllTransactions().stream()
            .filter(t -> !t.getDate().isBefore(startOfMonth))
            .mapToDouble(Transaction::getAmount)
            .sum();
            
        // 3. Count Pending Maintenance
        long brokenCount = equipmentService.getInventory().stream()
            .filter(e -> !e.getStatus().equals("Functional"))
            .count();
        
        grid.add(createStatCard("Active Members", String.valueOf(activeCount), "Live Count", "success"), 0, 0);
        grid.add(createStatCard("Revenue (Month)", String.format("$%.2f", collections), "Enrollments & Renewals", "success"), 1, 0);
        grid.add(createStatCard("Equipment Issues", String.valueOf(brokenCount), "Need Maintenance", "warning"), 2, 0);
        
        section.getChildren().add(grid);
        return section;
    }
    
    private VBox createStatCard(String label, String value, String footer, String type) {
        VBox card = new VBox();
        card.getStyleClass().addAll("stat-card");
        card.setAlignment(Pos.TOP_LEFT);
        
        Label cardLabel = new Label(label.toUpperCase());
        cardLabel.getStyleClass().add("card-label");
        
        Label cardValue = new Label(value);
        cardValue.getStyleClass().add("card-value");
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        HBox footerBox = new HBox(4);
        footerBox.setAlignment(Pos.CENTER_LEFT);
        footerBox.getStyleClass().add("card-footer");
        
        Label footerLabel = new Label(footer);
        footerBox.getChildren().add(footerLabel);
        
        card.getChildren().addAll(cardLabel, cardValue, spacer, footerBox);
        return card;
    }
    
    private VBox createQuickActionsSection() {
        VBox section = new VBox(16);
        Label sectionLabel = new Label("🚀 QUICK ACTIONS");
        sectionLabel.getStyleClass().add("text-h4");
        
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        
        FlowPane grid = new FlowPane(16, 16);
        grid.setPrefWrapLength(640);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.getChildren().addAll(
            createQuickActionButton("✏️", "Create Member", "enrollment-new"),
            createQuickActionButton("✓", "Check-In", "members-checkin"),
            createQuickActionButton("🔄", "Renew Member", "renewals"),
            createQuickActionButton("🏋️", "Equipment", "equipment-inventory"),
            createQuickActionButton("📊", "Reports", "reports-operational")
        );
        
        card.getChildren().add(grid);
        section.getChildren().addAll(sectionLabel, card);
        return section;
    }
    
    private HBox createQuickActionButton(String icon, String text, String destination) {
        HBox button = new HBox(8);
        button.getStyleClass().add("quick-action-button");
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new javafx.geometry.Insets(14));
        button.setMinWidth(180);
        button.setPrefWidth(220);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setCursor(Cursor.HAND);
        
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("icon");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("label");
        
        button.getChildren().addAll(iconLabel, textLabel);

        button.setOnMouseClicked(e -> {
            if (navigationHandler != null && destination != null) {
                navigationHandler.accept(destination);
            }
        });
        return button;
    }
    
    private VBox createAlertsSection() {
        VBox section = new VBox(16);
        Label sectionLabel = new Label("⚠️ SYSTEM ALERTS");
        sectionLabel.getStyleClass().add("text-h4");
        
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        
        // Real Alerts
        long expiringSoon = memberService.getAllMembers().stream()
            .filter(m -> m.getExpiryDate().isBefore(LocalDate.now().plusDays(7)))
            .count();
            
        long brokenItems = equipmentService.getInventory().stream()
            .filter(e -> !e.getStatus().equals("Functional"))
            .count();
        
        if (expiringSoon > 0) card.getChildren().add(createAlertItem("• " + expiringSoon + " memberships expiring this week"));
        if (brokenItems > 0) card.getChildren().add(createAlertItem("• " + brokenItems + " equipment items require maintenance"));
        if (expiringSoon == 0 && brokenItems == 0) card.getChildren().add(createAlertItem("• System healthy. No alerts."));
        
        section.getChildren().addAll(sectionLabel, card);
        return section;
    }
    
    private Label createAlertItem(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-body");
        return label;
    }
    
    private VBox createOccupancySection() {
        VBox section = new VBox(16);
        Label sectionLabel = new Label("📅 TODAY'S OCCUPANCY");
        sectionLabel.getStyleClass().add("text-h4");
        
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        
        // Real Data
        int current = memberService.getCurrentOccupancyCount();
        int capacity = 200; // Hardcoded capacity for now
        double percent = (double) current / capacity;
        
        Label infoLabel = new Label("Current: " + current + " members | Capacity: " + capacity + " (" + String.format("%.0f", percent * 100) + "%)");
        infoLabel.getStyleClass().add("text-body");
        
        ProgressBar progressBar = createProgressBar(percent);
        
        card.getChildren().addAll(infoLabel, progressBar);
        section.getChildren().addAll(sectionLabel, card);
        return section;
    }
    
    private ProgressBar createProgressBar(double progress) {
        ProgressBar bar = new ProgressBar(progress);
        bar.setPrefHeight(8);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: #2563EB;");
        return bar;
    }
}