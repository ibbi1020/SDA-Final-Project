package com.block20.controllers.members;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckInController {
    private VBox mainContainer;
    private TextField searchField;
    private VBox searchResultsBox;
    private VBox recentActivityBox;
    private ObservableList<CheckInRecord> recentActivity;
    private Label occupancyLabel;
    private ProgressBar occupancyBar;
    private int currentOccupancy = 47;
    private int maxCapacity = 150;

    public CheckInController() {
        this.recentActivity = FXCollections.observableArrayList();
        loadMockRecentActivity();
        initialize();
    }

    private void initialize() {
        mainContainer = new VBox(24);
        mainContainer.getStyleClass().add("main-content");
        mainContainer.setPadding(new Insets(32));

        mainContainer.getChildren().addAll(
            createHeader(),
            createOccupancyCard(),
            createSearchSection(),
            createRecentActivitySection()
        );
    }

    private VBox createHeader() {
        VBox header = new VBox(8);

        Text title = new Text("Check-In / Check-Out");
        title.getStyleClass().add("text-h2");

        Text subtitle = new Text("Quickly check members in and out of the facility");
        subtitle.getStyleClass().add("text-muted");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createOccupancyCard() {
        HBox card = new HBox(32);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER_LEFT);

        // Current occupancy display
        VBox occupancyInfo = new VBox(12);
        occupancyInfo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(occupancyInfo, Priority.ALWAYS);

        HBox occupancyNumbers = new HBox(8);
        occupancyNumbers.setAlignment(Pos.BASELINE_LEFT);

        Text currentCount = new Text(String.valueOf(currentOccupancy));
        currentCount.setStyle("-fx-font-size: 48px; -fx-font-weight: 700; -fx-fill: #2563EB;");

        Text separator = new Text("/");
        separator.setStyle("-fx-font-size: 32px; -fx-fill: #94A3B8;");

        Text maxCount = new Text(String.valueOf(maxCapacity));
        maxCount.setStyle("-fx-font-size: 32px; -fx-font-weight: 600; -fx-fill: #64748B;");

        occupancyNumbers.getChildren().addAll(currentCount, separator, maxCount);

        Text occupancyLabel = new Text("Current Occupancy");
        occupancyLabel.getStyleClass().add("text-body");
        occupancyLabel.setStyle("-fx-fill: #475569;");

        // Progress bar
        VBox progressBox = new VBox(8);
        progressBox.setPrefWidth(400);

        occupancyBar = new ProgressBar();
        occupancyBar.setPrefWidth(400);
        occupancyBar.setPrefHeight(12);
        occupancyBar.setProgress((double) currentOccupancy / maxCapacity);
        occupancyBar.getStyleClass().add("occupancy-progress");

        Text percentageText = new Text(String.format("%.0f%% Capacity", (double) currentOccupancy / maxCapacity * 100));
        percentageText.getStyleClass().add("text-caption");
        percentageText.setStyle("-fx-fill: #64748B;");

        progressBox.getChildren().addAll(occupancyBar, percentageText);

        occupancyInfo.getChildren().addAll(occupancyNumbers, occupancyLabel, progressBox);

        // Status indicator
        VBox statusBox = new VBox(12);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(16));
        statusBox.setStyle("-fx-background-color: #F0FDF4; -fx-background-radius: 8px; -fx-min-width: 150px;");

        Text statusIcon = new Text("✓");
        statusIcon.setStyle("-fx-font-size: 32px; -fx-fill: #10B981;");

        Text statusText = new Text("Normal");
        statusText.getStyleClass().add("text-h5");
        statusText.setStyle("-fx-fill: #10B981;");

        Text statusSubtext = new Text("Facility Status");
        statusSubtext.getStyleClass().add("text-caption");
        statusSubtext.setStyle("-fx-fill: #059669;");

        statusBox.getChildren().addAll(statusIcon, statusText, statusSubtext);

        card.getChildren().addAll(occupancyInfo, statusBox);
        return card;
    }

    private VBox createSearchSection() {
        VBox section = new VBox(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));

        Text sectionTitle = new Text("Member Search");
        sectionTitle.getStyleClass().add("text-h4");

        // Large search input
        VBox searchBox = new VBox(12);
        
        HBox searchInputBox = new HBox(16);
        searchInputBox.setAlignment(Pos.CENTER);

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 28px;");

        searchField = new TextField();
        searchField.setPromptText("Enter Member ID, Name, Phone, or Email...");
        searchField.getStyleClass().add("search-input-large");
        searchField.setPrefHeight(60);
        searchField.setStyle("-fx-font-size: 18px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Auto-search with debounce
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            pause.setOnFinished(e -> searchMembers(newVal));
            pause.playFromStart();
        });

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().addAll("btn", "btn-ghost");
        clearButton.setPrefHeight(60);
        clearButton.setOnAction(e -> {
            searchField.clear();
            searchResultsBox.getChildren().clear();
        });

        searchInputBox.getChildren().addAll(searchIcon, searchField, clearButton);

        Text searchHint = new Text("💡 Tip: Start typing to search. Results appear instantly.");
        searchHint.getStyleClass().add("text-caption");
        searchHint.setStyle("-fx-fill: #64748B;");

        // Search results area
        searchResultsBox = new VBox(8);
        searchResultsBox.setStyle("-fx-padding: 12 0 0 0;");

        searchBox.getChildren().addAll(searchInputBox, searchHint, searchResultsBox);

        section.getChildren().addAll(sectionTitle, searchBox);
        return section;
    }

    private void searchMembers(String query) {
        searchResultsBox.getChildren().clear();

        if (query == null || query.trim().isEmpty()) {
            return;
        }

        query = query.toLowerCase().trim();

        // Mock search results
        ObservableList<MemberSearchResult> results = FXCollections.observableArrayList();
        
        if (query.contains("john") || query.contains("m001")) {
            results.add(new MemberSearchResult("M001", "John Smith", "Premium", "Active", true));
        }
        if (query.contains("sarah") || query.contains("m002")) {
            results.add(new MemberSearchResult("M002", "Sarah Johnson", "Basic", "Active", false));
        }
        if (query.contains("emily") || query.contains("m004")) {
            results.add(new MemberSearchResult("M004", "Emily Davis", "Student", "Expired", false));
        }
        if (query.contains("david") || query.contains("m005")) {
            results.add(new MemberSearchResult("M005", "David Wilson", "Premium", "Active", true));
        }
        if (query.contains("robert") || query.contains("m007")) {
            results.add(new MemberSearchResult("M007", "Robert Taylor", "Premium", "Suspended", false));
        }

        if (results.isEmpty()) {
            Text noResults = new Text("No members found matching \"" + query + "\"");
            noResults.getStyleClass().add("text-muted");
            searchResultsBox.getChildren().add(noResults);
        } else {
            Text resultsHeader = new Text(results.size() + " member(s) found:");
            resultsHeader.getStyleClass().add("text-body");
            resultsHeader.setStyle("-fx-font-weight: 600;");
            searchResultsBox.getChildren().add(resultsHeader);

            for (MemberSearchResult result : results) {
                searchResultsBox.getChildren().add(createMemberResultCard(result));
            }
        }
    }

    private HBox createMemberResultCard(MemberSearchResult member) {
        HBox card = new HBox(16);
        card.getStyleClass().add("member-result-card");
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-border-width: 1px;");

        // Member photo/avatar
        VBox avatar = new VBox();
        avatar.setPrefSize(60, 60);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("-fx-background-color: #DBEAFE; -fx-background-radius: 30px;");
        Text initials = new Text(getInitials(member.getName()));
        initials.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        avatar.getChildren().add(initials);

        // Member info
        VBox infoBox = new VBox(6);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Text name = new Text(member.getName());
        name.getStyleClass().add("text-h5");

        Label statusBadge = new Label(member.getStatus());
        statusBadge.getStyleClass().addAll("badge", "badge-" + member.getStatus().toLowerCase());

        nameRow.getChildren().addAll(name, statusBadge);

        HBox detailsRow = new HBox(16);
        Text memberId = new Text("ID: " + member.getMemberId());
        memberId.getStyleClass().add("text-caption");
        
        Text plan = new Text("Plan: " + member.getPlanType());
        plan.getStyleClass().add("text-caption");

        Text checkInStatus = new Text(member.isCheckedIn() ? "✓ Currently Checked In" : "• Not Checked In");
        checkInStatus.getStyleClass().add("text-caption");
        checkInStatus.setStyle("-fx-fill: " + (member.isCheckedIn() ? "#10B981" : "#64748B") + ";");

        detailsRow.getChildren().addAll(memberId, plan, checkInStatus);

        infoBox.getChildren().addAll(nameRow, detailsRow);

        // Action buttons
        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        if (member.getStatus().equals("Active")) {
            if (member.isCheckedIn()) {
                Button checkOutBtn = new Button("Check Out");
                checkOutBtn.getStyleClass().addAll("btn", "btn-warning");
                checkOutBtn.setPrefWidth(120);
                checkOutBtn.setOnAction(e -> handleCheckOut(member));
                actionBox.getChildren().add(checkOutBtn);
            } else {
                Button checkInBtn = new Button("Check In");
                checkInBtn.getStyleClass().addAll("btn", "btn-success");
                checkInBtn.setPrefWidth(120);
                checkInBtn.setOnAction(e -> handleCheckIn(member));
                actionBox.getChildren().add(checkInBtn);
            }
        } else {
            Text inactiveText = new Text("Cannot check in");
            inactiveText.getStyleClass().add("text-caption");
            inactiveText.setStyle("-fx-fill: #EF4444;");
            actionBox.getChildren().add(inactiveText);
        }

        card.getChildren().addAll(avatar, infoBox, actionBox);
        return card;
    }

    private void handleCheckIn(MemberSearchResult member) {
        currentOccupancy++;
        occupancyBar.setProgress((double) currentOccupancy / maxCapacity);

        // Add to recent activity
        CheckInRecord record = new CheckInRecord(
            member.getMemberId(),
            member.getName(),
            "Check In",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        );
        recentActivity.add(0, record);
        if (recentActivity.size() > 10) {
            recentActivity.remove(10);
        }

        member.setCheckedIn(true);
        refreshRecentActivity();

        // Show success notification
        showNotification("✓ " + member.getName() + " checked in successfully", "success");

        // Re-search to update UI
        searchMembers(searchField.getText());
    }

    private void handleCheckOut(MemberSearchResult member) {
        currentOccupancy--;
        occupancyBar.setProgress((double) currentOccupancy / maxCapacity);

        // Add to recent activity
        CheckInRecord record = new CheckInRecord(
            member.getMemberId(),
            member.getName(),
            "Check Out",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        );
        recentActivity.add(0, record);
        if (recentActivity.size() > 10) {
            recentActivity.remove(10);
        }

        member.setCheckedIn(false);
        refreshRecentActivity();

        // Show success notification
        showNotification("✓ " + member.getName() + " checked out successfully", "success");

        // Re-search to update UI
        searchMembers(searchField.getText());
    }

    private void showNotification(String message, String type) {
        // Simple console notification for now
        System.out.println(message);
        // TODO: Implement toast notification UI component
    }

    private VBox createRecentActivitySection() {
        VBox section = new VBox(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Recent Activity");
        title.getStyleClass().add("text-h4");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text liveIndicator = new Text("🔴 Live");
        liveIndicator.getStyleClass().add("text-caption");
        liveIndicator.setStyle("-fx-fill: #EF4444; -fx-font-weight: 600;");

        header.getChildren().addAll(title, spacer, liveIndicator);

        // Activity list
        recentActivityBox = new VBox(8);
        ScrollPane scrollPane = new ScrollPane(recentActivityBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background-color: transparent;");

        refreshRecentActivity();

        section.getChildren().addAll(header, new Separator(), scrollPane);
        return section;
    }

    private void refreshRecentActivity() {
        recentActivityBox.getChildren().clear();

        if (recentActivity.isEmpty()) {
            Text emptyText = new Text("No recent activity");
            emptyText.getStyleClass().add("text-muted");
            recentActivityBox.getChildren().add(emptyText);
            return;
        }

        for (CheckInRecord record : recentActivity) {
            recentActivityBox.getChildren().add(createActivityItem(record));
        }
    }

    private HBox createActivityItem(CheckInRecord record) {
        HBox item = new HBox(16);
        item.setPadding(new Insets(12));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6px;");

        // Action icon
        Label icon = new Label(record.getAction().equals("Check In") ? "→" : "←");
        icon.setStyle("-fx-font-size: 20px; -fx-text-fill: " + 
            (record.getAction().equals("Check In") ? "#10B981" : "#F59E0B") + ";");

        // Member info
        VBox infoBox = new VBox(4);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Text memberName = new Text(record.getMemberName());
        memberName.getStyleClass().add("text-body");
        memberName.setStyle("-fx-font-weight: 600;");

        Text details = new Text(record.getMemberId() + " • " + record.getAction());
        details.getStyleClass().add("text-caption");

        infoBox.getChildren().addAll(memberName, details);

        // Timestamp
        Text timestamp = new Text(record.getTimestamp());
        timestamp.getStyleClass().add("text-caption");
        timestamp.setStyle("-fx-fill: #64748B;");

        item.getChildren().addAll(icon, infoBox, timestamp);
        return item;
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return parts[0].substring(0, 1) + parts[1].substring(0, 1);
        }
        return name.substring(0, Math.min(2, name.length()));
    }

    private void loadMockRecentActivity() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        recentActivity.addAll(
            new CheckInRecord("M005", "David Wilson", "Check In", LocalDateTime.now().minusMinutes(5).format(formatter)),
            new CheckInRecord("M003", "Michael Brown", "Check Out", LocalDateTime.now().minusMinutes(12).format(formatter)),
            new CheckInRecord("M008", "Amanda Anderson", "Check In", LocalDateTime.now().minusMinutes(23).format(formatter)),
            new CheckInRecord("M002", "Sarah Johnson", "Check In", LocalDateTime.now().minusMinutes(35).format(formatter)),
            new CheckInRecord("M010", "Jennifer White", "Check Out", LocalDateTime.now().minusMinutes(47).format(formatter))
        );
    }

    public VBox getView() {
        return mainContainer;
    }

    // Member search result class
    public static class MemberSearchResult {
        private final String memberId;
        private final String name;
        private final String planType;
        private final String status;
        private boolean checkedIn;

        public MemberSearchResult(String memberId, String name, String planType, String status, boolean checkedIn) {
            this.memberId = memberId;
            this.name = name;
            this.planType = planType;
            this.status = status;
            this.checkedIn = checkedIn;
        }

        public String getMemberId() { return memberId; }
        public String getName() { return name; }
        public String getPlanType() { return planType; }
        public String getStatus() { return status; }
        public boolean isCheckedIn() { return checkedIn; }
        public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }
    }

    // Check-in record class
    public static class CheckInRecord {
        private final String memberId;
        private final String memberName;
        private final String action;
        private final String timestamp;

        public CheckInRecord(String memberId, String memberName, String action, String timestamp) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.action = action;
            this.timestamp = timestamp;
        }

        public String getMemberId() { return memberId; }
        public String getMemberName() { return memberName; }
        public String getAction() { return action; }
        public String getTimestamp() { return timestamp; }
    }
}
