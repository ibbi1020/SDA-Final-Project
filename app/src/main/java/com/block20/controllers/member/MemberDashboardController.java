/*
 * Block20 Gym Management System
 * Member Dashboard Controller
 */
package com.block20.controllers.member;

import com.block20.models.Attendance;
import com.block20.models.Member;
import com.block20.services.MemberService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Member Dashboard - Overview of membership status, quick actions, upcoming sessions
 */
public class MemberDashboardController extends ScrollPane {
    
    private VBox contentContainer;
    private String memberId;
    private String memberName;
    private Consumer<String> navigationHandler;
    
    // NEW: Service Dependency
    private MemberService memberService;
    
    // Data storage
    private Member memberData;
    
    // UPDATED: Constructor accepts MemberService
    public MemberDashboardController(String memberId, String memberName, Consumer<String> navigationHandler, MemberService memberService) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        
        loadRealData(); // <--- NEW: Load from DB
        initializeView();
    }
    
    // NEW: Fetch data from SQLite
    private void loadRealData() {
        this.memberData = memberService.getMemberById(memberId);
        if (this.memberData == null) {
            // Fallback if member deleted but session active
            System.err.println("Error: Member not found in DB");
        }
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
        
        if (memberData != null) {
            contentContainer.getChildren().addAll(
                createHeader(),
                createMembershipStatusCard(),
                createQuickActionsSection(),
                createUpcomingSessionsSection(), // Keeps mock sessions for now (Partner B domain)
                createRecentActivitySection()    // <--- NEW: Real Attendance History
            );
        } else {
            contentContainer.getChildren().add(new Label("Error loading dashboard. Member not found."));
        }
        
        setContent(contentContainer);
    }
    
    /**
     * Create header
     */
    private VBox createHeader() {
        VBox header = new VBox(8);
        
        // Use real name from DB
        Text title = new Text("Welcome, " + memberData.getFullName() + "!");
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
        
        // Real Status
        Label statusBadge = new Label(memberData.getStatus());
        statusBadge.getStyleClass().add("badge");
        if ("Active".equalsIgnoreCase(memberData.getStatus())) {
            statusBadge.getStyleClass().add("badge-success");
            statusBadge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
        } else {
            statusBadge.getStyleClass().add("badge-warning");
            statusBadge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;");
        }
        
        cardHeader.getChildren().addAll(icon, cardTitle, spacer, statusBadge);
        
        // Membership info grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(40);
        infoGrid.setVgap(16);
        
        // Real Data Fields
        infoGrid.add(createInfoItem("Plan", memberData.getPlanType()), 0, 0);
        infoGrid.add(createInfoItem("Member Since", memberData.getJoinDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 0, 1);
        
        infoGrid.add(createInfoItem("Expiry Date", memberData.getExpiryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 1, 0);
        
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), memberData.getExpiryDate());
        String daysRemaining = daysUntil > 0 ? daysUntil + " days remaining" : "Expired";
        infoGrid.add(createInfoItem("Days Remaining", daysRemaining), 1, 1);
        
        card.getChildren().addAll(cardHeader, new Separator(), infoGrid);
        
        // Renewal reminder if expiring soon
        if (daysUntil <= 30) { // Show if expired or expiring soon
            HBox reminderBox = new HBox(12);
            reminderBox.getStyleClass().add("alert-box");
            reminderBox.setAlignment(Pos.CENTER_LEFT);
            reminderBox.setPadding(new Insets(12, 16, 12, 16));
            reminderBox.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 8;");
            
            Label warningIcon = new Label("⚠️");
            warningIcon.setStyle("-fx-font-size: 20px;");
            
            String msg = daysUntil < 0 ? "Your membership has expired." : "Expires in " + daysUntil + " days.";
            Text reminderText = new Text(msg + " Renew now to avoid interruption!");
            
            Region reminderSpacer = new Region();
            HBox.setHgrow(reminderSpacer, Priority.ALWAYS);
            
            Button renewButton = new Button("Renew Now");
            renewButton.getStyleClass().addAll("primary-button");
            renewButton.setOnAction(e -> navigationHandler.accept("membership"));
            
            reminderBox.getChildren().addAll(warningIcon, reminderText, reminderSpacer, renewButton);
            card.getChildren().add(reminderBox);
        }
        
        return card;
    }
    
    private VBox createInfoItem(String label, String value) {
        VBox item = new VBox(4);
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-fill: #64748B;");
        
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
    
    private VBox createQuickActionCard(String icon, String title, String description, String destination) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-cursor: hand;");
        card.setOnMouseClicked(e -> navigationHandler.accept(destination));
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-weight: 600; -fx-font-size: 16px;");
        
        Text descText = new Text(description);
        descText.setStyle("-fx-fill: #64748B;");
        descText.setWrappingWidth(200);
        
        card.getChildren().addAll(iconLabel, titleText, descText);
        return card;
    }
    
    /**
     * NEW: Create Recent Activity Section (Replaces Mock Payment Summary for now)
     * Shows real check-in history from DB.
     */
    private VBox createRecentActivitySection() {
        VBox section = new VBox(16);
        
        HBox sectionHeader = new HBox(12);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        
        Text sectionTitle = new Text("Recent Activity");
        sectionTitle.getStyleClass().add("text-h3");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button viewAllButton = new Button("View Full History");
        viewAllButton.setOnAction(e -> navigationHandler.accept("attendance"));
        
        sectionHeader.getChildren().addAll(sectionTitle, spacer, viewAllButton);
        
        VBox activityList = new VBox(10);
        
        // 1. Get Real History
        List<Attendance> history = memberService.getAttendanceForMember(memberId);
        
        if (history.isEmpty()) {
            Label empty = new Label("No recent activity found.");
            empty.setStyle("-fx-text-fill: #64748B;");
            activityList.getChildren().add(empty);
        } else {
            // Sort newest first
            history.sort(Comparator.comparing(Attendance::getCheckInTime).reversed());
            
            // Show top 3
            for (int i = 0; i < Math.min(3, history.size()); i++) {
                Attendance visit = history.get(i);
                activityList.getChildren().add(createActivityCard(visit));
            }
        }
        
        section.getChildren().addAll(sectionHeader, activityList);
        return section;
    }
    
    private HBox createActivityCard(Attendance visit) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");
        
        Label icon = new Label("📍");
        icon.setStyle("-fx-font-size: 20px;");
        
        VBox details = new VBox(4);
        Text title = new Text("Gym Visit");
        title.setStyle("-fx-font-weight: 600;");
        
        String date = visit.getCheckInTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        String time = visit.getCheckInTime().format(DateTimeFormatter.ofPattern("h:mm a"));
        Text sub = new Text(date + " at " + time);
        sub.setStyle("-fx-fill: #64748B;");
        
        details.getChildren().addAll(title, sub);
        
        card.getChildren().addAll(icon, details);
        return card;
    }

    // Keeps the mock session section for now (Partner B needs to implement real sessions)
    private VBox createUpcomingSessionsSection() {
        VBox section = new VBox(16);
        Text t = new Text("Upcoming Sessions (Mock Data)");
        t.getStyleClass().add("text-h3");
        section.getChildren().add(t);
        // Placeholder logic preserved
        return section;
    }
    
    // Helper for creating Payment Summary (retained structure, but empty for now)
    private VBox createPaymentSummarySection() {
        return new VBox(); // Hidden until we link Payment Service
    }
}