/*
 * Block20 Gym Management System
 * Member Dashboard Controller
 */
package com.block20.controllers.member;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

/**
 * Member Dashboard - Overview of membership status, quick actions, upcoming sessions
 */
public class MemberDashboardController extends ScrollPane {
    
    private VBox contentContainer;
    private String memberId;
    private String memberName;
    private Consumer<String> navigationHandler;
    
    // Mock member data
    private MemberData memberData;
    
    public MemberDashboardController(String memberId, String memberName, Consumer<String> navigationHandler) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.navigationHandler = navigationHandler;
        this.memberData = generateMockMemberData();
        initializeView();
    }
    
    private void initializeView() {
        // Configure ScrollPane
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.setPadding(new Insets(32));
        contentContainer.getStyleClass().add("main-content");
        
        contentContainer.getChildren().addAll(
            createHeader(),
            createMembershipStatusCard(),
            createQuickActionsSection(),
            createUpcomingSessionsSection(),
            createPaymentSummarySection()
        );
        
        setContent(contentContainer);
    }
    
    /**
     * Create header
     */
    private VBox createHeader() {
        VBox header = new VBox(8);
        
        Text title = new Text("Welcome, " + memberName + "!");
        title.getStyleClass().add("text-h2");
        
        Text subtitle = new Text(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        subtitle.getStyleClass().add("text-muted");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    /**
     * Create membership status card
     */
    private VBox createMembershipStatusCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        // Header
        HBox cardHeader = new HBox(12);
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label("💳");
        icon.setStyle("-fx-font-size: 24px;");
        
        Text cardTitle = new Text("Membership Status");
        cardTitle.getStyleClass().add("text-h3");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusBadge = new Label(memberData.status);
        statusBadge.getStyleClass().add("badge");
        if (memberData.status.equals("Active")) {
            statusBadge.getStyleClass().add("badge-success");
        } else if (memberData.status.equals("Expiring Soon")) {
            statusBadge.getStyleClass().add("badge-warning");
        }
        
        cardHeader.getChildren().addAll(icon, cardTitle, spacer, statusBadge);
        
        // Membership info grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(40);
        infoGrid.setVgap(16);
        
        // Column 1
        infoGrid.add(createInfoItem("Plan", memberData.plan), 0, 0);
        infoGrid.add(createInfoItem("Member Since", memberData.memberSince.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 0, 1);
        
        // Column 2
        infoGrid.add(createInfoItem("Expiry Date", memberData.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 1, 0);
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), memberData.expiryDate);
        String daysRemaining = daysUntil > 0 ? daysUntil + " days remaining" : "Expired";
        infoGrid.add(createInfoItem("Days Remaining", daysRemaining), 1, 1);
        
        card.getChildren().addAll(cardHeader, new Separator(), infoGrid);
        
        // Renewal reminder if expiring soon
        if (daysUntil <= 30 && daysUntil > 0) {
            HBox reminderBox = new HBox(12);
            reminderBox.getStyleClass().add("alert-box");
            reminderBox.getStyleClass().add("alert-warning");
            reminderBox.setAlignment(Pos.CENTER_LEFT);
            reminderBox.setPadding(new Insets(12, 16, 12, 16));
            
            Label warningIcon = new Label("⚠️");
            warningIcon.setStyle("-fx-font-size: 20px;");
            
            Text reminderText = new Text("Your membership expires in " + daysUntil + " days. Renew now to avoid interruption!");
            reminderText.getStyleClass().add("text-body-sm");
            
            Region reminderSpacer = new Region();
            HBox.setHgrow(reminderSpacer, Priority.ALWAYS);
            
            Button renewButton = new Button("Renew Now");
            renewButton.getStyleClass().addAll("primary-button", "button-small");
            renewButton.setOnAction(e -> navigationHandler.accept("membership"));
            
            reminderBox.getChildren().addAll(warningIcon, reminderText, reminderSpacer, renewButton);
            card.getChildren().add(reminderBox);
        }
        
        return card;
    }
    
    /**
     * Create info item for grid
     */
    private VBox createInfoItem(String label, String value) {
        VBox item = new VBox(4);
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-fill: -fx-gray-600;");
        
        Text valueText = new Text(value);
        valueText.getStyleClass().add("text-body");
        valueText.setStyle("-fx-font-weight: 600;");
        
        item.getChildren().addAll(labelText, valueText);
        return item;
    }
    
    /**
     * Create quick actions section
     */
    private VBox createQuickActionsSection() {
        VBox section = new VBox(16);
        
        Text sectionTitle = new Text("Quick Actions");
        sectionTitle.getStyleClass().add("text-h3");
        
        FlowPane actionsGrid = new FlowPane(16, 16);
        
        actionsGrid.getChildren().addAll(
            createQuickActionCard("🔄", "Renew Membership", "Extend your gym access", "membership"),
            createQuickActionCard("🏃", "Book Training", "Schedule a session with a trainer", "training"),
            createQuickActionCard("📅", "View Attendance", "Check your gym visit history", "attendance")
        );
        
        section.getChildren().addAll(sectionTitle, actionsGrid);
        return section;
    }
    
    /**
     * Create quick action card
     */
    private VBox createQuickActionCard(String icon, String title, String description, String destination) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setMaxWidth(280);
        card.setMinWidth(200);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-cursor: hand;");
        card.setOnMouseClicked(e -> navigationHandler.accept(destination));
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Text titleText = new Text(title);
        titleText.getStyleClass().add("text-body");
        titleText.setStyle("-fx-font-weight: 600;");
        
        Text descText = new Text(description);
        descText.getStyleClass().add("text-caption");
        descText.setStyle("-fx-fill: -fx-gray-600;");
        descText.setWrappingWidth(240);
        
        card.getChildren().addAll(iconLabel, titleText, descText);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-cursor: hand; -fx-background-color: -fx-gray-50;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-cursor: hand; -fx-background-color: white;"));
        
        return card;
    }
    
    /**
     * Create upcoming sessions section
     */
    private VBox createUpcomingSessionsSection() {
        VBox section = new VBox(16);
        
        HBox sectionHeader = new HBox(12);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        
        Text sectionTitle = new Text("Upcoming Training Sessions");
        sectionTitle.getStyleClass().add("text-h3");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button viewAllButton = new Button("View All");
        viewAllButton.getStyleClass().addAll("secondary-button", "button-small");
        viewAllButton.setOnAction(e -> navigationHandler.accept("training"));
        
        sectionHeader.getChildren().addAll(sectionTitle, spacer, viewAllButton);
        
        VBox sessionsList = new VBox(12);
        
        // Mock upcoming sessions
        sessionsList.getChildren().addAll(
            createSessionCard("Personal Training", "John Smith", LocalDateTime.now().plusDays(2).withHour(14).withMinute(0)),
            createSessionCard("Yoga Class", "Sarah Johnson", LocalDateTime.now().plusDays(5).withHour(10).withMinute(30)),
            createSessionCard("HIIT Session", "Mike Davis", LocalDateTime.now().plusDays(7).withHour(18).withMinute(0))
        );
        
        section.getChildren().addAll(sectionHeader, sessionsList);
        return section;
    }
    
    /**
     * Create session card
     */
    private HBox createSessionCard(String sessionType, String trainerName, LocalDateTime dateTime) {
        HBox card = new HBox(16);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        
        // Date box
        VBox dateBox = new VBox(4);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPrefWidth(60);
        dateBox.setStyle("-fx-background-color: -fx-primary-50; -fx-background-radius: 8; -fx-padding: 8;");
        
        Text month = new Text(dateTime.format(DateTimeFormatter.ofPattern("MMM")));
        month.getStyleClass().add("text-caption");
        month.setStyle("-fx-fill: -fx-primary-600; -fx-font-weight: 600;");
        
        Text day = new Text(dateTime.format(DateTimeFormatter.ofPattern("dd")));
        day.getStyleClass().add("text-h3");
        day.setStyle("-fx-fill: -fx-primary-600;");
        
        dateBox.getChildren().addAll(month, day);
        
        // Session info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Text sessionName = new Text(sessionType);
        sessionName.getStyleClass().add("text-body");
        sessionName.setStyle("-fx-font-weight: 600;");
        
        HBox detailsBox = new HBox(12);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        
        Text trainer = new Text("👤 " + trainerName);
        trainer.getStyleClass().add("text-body-sm");
        trainer.setStyle("-fx-fill: -fx-gray-600;");
        
        Text time = new Text("🕐 " + dateTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        time.getStyleClass().add("text-body-sm");
        time.setStyle("-fx-fill: -fx-gray-600;");
        
        detailsBox.getChildren().addAll(trainer, time);
        
        info.getChildren().addAll(sessionName, detailsBox);
        
        card.getChildren().addAll(dateBox, info);
        return card;
    }
    
    /**
     * Create payment summary section
     */
    private VBox createPaymentSummarySection() {
        VBox section = new VBox(16);
        
        HBox sectionHeader = new HBox(12);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        
        Text sectionTitle = new Text("Payment Summary");
        sectionTitle.getStyleClass().add("text-h3");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button viewAllButton = new Button("View Details");
        viewAllButton.getStyleClass().addAll("secondary-button", "button-small");
        viewAllButton.setOnAction(e -> navigationHandler.accept("payments"));
        
        sectionHeader.getChildren().addAll(sectionTitle, spacer, viewAllButton);
        
        FlowPane summaryCards = new FlowPane(16, 16);
        
        summaryCards.getChildren().addAll(
            createPaymentSummaryCard("Current Balance", "$0.00", "badge-success"),
            createPaymentSummaryCard("Next Payment Due", memberData.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd")), "badge-info"),
            createPaymentSummaryCard("Last Payment", "$29.99 - " + LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MMM dd")), "badge-neutral")
        );
        
        section.getChildren().addAll(sectionHeader, summaryCards);
        return section;
    }
    
    /**
     * Create payment summary card
     */
    private VBox createPaymentSummaryCard(String label, String value, String badgeClass) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setMaxWidth(280);
        card.setMinWidth(200);
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-fill: -fx-gray-600;");
        
        Text valueText = new Text(value);
        valueText.getStyleClass().add("text-body");
        valueText.setStyle("-fx-font-weight: 600; -fx-font-size: 18px;");
        valueText.setWrappingWidth(240);
        
        card.getChildren().addAll(labelText, valueText);
        return card;
    }
    
    /**
     * Generate mock member data
     */
    private MemberData generateMockMemberData() {
        MemberData data = new MemberData();
        data.memberId = memberId;
        data.memberName = memberName;
        data.plan = "Premium Monthly";
        data.status = "Active";
        data.memberSince = LocalDate.now().minusYears(2).minusMonths(3);
        data.expiryDate = LocalDate.now().plusDays(15); // Expiring soon
        return data;
    }
    
    /**
     * Member data class
     */
    private static class MemberData {
        String memberId;
        String memberName;
        String plan;
        String status;
        LocalDate memberSince;
        LocalDate expiryDate;
    }
}
