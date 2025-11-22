package com.block20.controllers.members;

import com.block20.models.Attendance;
import com.block20.models.Member;
import com.block20.services.MemberService;

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
import java.util.List;

public class CheckInController extends ScrollPane {
    private VBox contentContainer;
    private TextField searchField;
    private VBox searchResultsBox;
    private VBox recentActivityBox;
    private ObservableList<CheckInRecord> recentActivity;
    
    // UI Components that need updating
    private Text currentCountText; // <--- NEW: Class level reference
    private ProgressBar occupancyBar;
    
    private int currentOccupancy = 0;
    private int maxCapacity = 150;
    
    private MemberService memberService;

    public CheckInController(MemberService memberService) {
        this.memberService = memberService;
        this.recentActivity = FXCollections.observableArrayList();
        initialize();
    }

    private void initialize() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.getStyleClass().add("main-content");
        contentContainer.setPadding(new Insets(32));

        contentContainer.getChildren().addAll(
            createHeader(),
            createOccupancyCard(),
            createSearchSection(),
            createRecentActivitySection()
        );
        
        setContent(contentContainer);

        // 1. Get the REAL count from the database
        this.currentOccupancy = memberService.getCurrentOccupancyCount();
        
        // 2. Update the UI immediately to match
        updateOccupancyUI();
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

        VBox occupancyInfo = new VBox(12);
        occupancyInfo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(occupancyInfo, Priority.ALWAYS);

        HBox occupancyNumbers = new HBox(8);
        occupancyNumbers.setAlignment(Pos.BASELINE_LEFT);

        // FIX: Assign to class variable so we can update it later
        currentCountText = new Text(String.valueOf(currentOccupancy));
        currentCountText.setStyle("-fx-font-size: 48px; -fx-font-weight: 700; -fx-fill: #2563EB;");

        Text separator = new Text("/");
        separator.setStyle("-fx-font-size: 32px; -fx-fill: #94A3B8;");
        Text maxCount = new Text(String.valueOf(maxCapacity));
        maxCount.setStyle("-fx-font-size: 32px; -fx-font-weight: 600; -fx-fill: #64748B;");

        occupancyNumbers.getChildren().addAll(currentCountText, separator, maxCount);

        Text occupancyLabel = new Text("Current Occupancy");
        occupancyLabel.getStyleClass().add("text-body");
        occupancyLabel.setStyle("-fx-fill: #475569;");

        VBox progressBox = new VBox(8);
        progressBox.setPrefWidth(400);

        occupancyBar = new ProgressBar();
        occupancyBar.setPrefWidth(400);
        occupancyBar.setPrefHeight(12);
        occupancyBar.setProgress(0.0);
        occupancyBar.getStyleClass().add("occupancy-progress");

        progressBox.getChildren().addAll(occupancyBar);
        occupancyInfo.getChildren().addAll(occupancyNumbers, occupancyLabel, progressBox);

        // Status Box
        VBox statusBox = new VBox(12);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(16));
        statusBox.setStyle("-fx-background-color: #F0FDF4; -fx-background-radius: 8px; -fx-min-width: 150px;");
        Text statusIcon = new Text("✓");
        statusIcon.setStyle("-fx-font-size: 32px; -fx-fill: #10B981;");
        Text statusText = new Text("Normal");
        statusText.getStyleClass().add("text-h5");
        statusText.setStyle("-fx-fill: #10B981;");
        statusBox.getChildren().addAll(statusIcon, statusText);

        card.getChildren().addAll(occupancyInfo, statusBox);
        return card;
    }

    private VBox createSearchSection() {
        VBox section = new VBox(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));

        Text sectionTitle = new Text("Member Search");
        sectionTitle.getStyleClass().add("text-h4");

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

        PauseTransition pause = new PauseTransition(Duration.millis(300));
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
        Text searchHint = new Text("💡 Tip: Start typing to search real members.");
        searchHint.getStyleClass().add("text-caption");
        searchHint.setStyle("-fx-fill: #64748B;");

        searchResultsBox = new VBox(8);
        searchResultsBox.setStyle("-fx-padding: 12 0 0 0;");

        searchBox.getChildren().addAll(searchInputBox, searchHint, searchResultsBox);
        section.getChildren().addAll(sectionTitle, searchBox);
        return section;
    }

    // --- CORE LOGIC ---

    private void searchMembers(String query) {
        searchResultsBox.getChildren().clear();

        if (query == null || query.trim().isEmpty()) return;
        String lowerQuery = query.toLowerCase().trim();

        List<Member> allMembers = memberService.getAllMembers();
        
        List<Member> results = allMembers.stream()
            .filter(m -> m.getFullName().toLowerCase().contains(lowerQuery) || 
                         m.getMemberId().toLowerCase().contains(lowerQuery) ||
                         m.getEmail().toLowerCase().contains(lowerQuery))
            .limit(5)
            .toList();

        if (results.isEmpty()) {
            Text noResults = new Text("No members found matching \"" + query + "\"");
            noResults.getStyleClass().add("text-muted");
            searchResultsBox.getChildren().add(noResults);
        } else {
            for (Member m : results) {
                // Ask backend for REAL status
                boolean isCheckedIn = memberService.isMemberCheckedIn(m.getMemberId());
                System.out.println("DEBUG UI: Member " + m.getFullName() + " Checked In? " + isCheckedIn);

                MemberSearchResult result = new MemberSearchResult(
                    m.getMemberId(), 
                    m.getFullName(), 
                    m.getPlanType(), 
                    m.getStatus(), 
                    isCheckedIn 
                );
                searchResultsBox.getChildren().add(createMemberResultCard(result));
            }
        }
    }

    private HBox createMemberResultCard(MemberSearchResult member) {
        HBox card = new HBox(16);
        card.getStyleClass().add("member-result-card");
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        VBox avatar = new VBox();
        avatar.setPrefSize(60, 60);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("-fx-background-color: #DBEAFE; -fx-background-radius: 30px;");
        Text initials = new Text(getInitials(member.getName()));
        initials.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        avatar.getChildren().add(initials);

        VBox infoBox = new VBox(6);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Text name = new Text(member.getName());
        name.getStyleClass().add("text-h5");
        
        Label statusBadge = new Label(member.getStatus());
        statusBadge.getStyleClass().addAll("badge", "badge-" + member.getStatus().toLowerCase());
        nameRow.getChildren().addAll(name, statusBadge);

        Text details = new Text("ID: " + member.getMemberId() + " • Plan: " + member.getPlanType());
        details.getStyleClass().add("text-caption");
        
        infoBox.getChildren().addAll(nameRow, details);

        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        // FIX: Case-insensitive check for "Active"
        if ("Active".equalsIgnoreCase(member.getStatus())) {
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
            inactiveText.setStyle("-fx-fill: #EF4444;");
            actionBox.getChildren().add(inactiveText);
        }

        card.getChildren().addAll(avatar, infoBox, actionBox);
        return card;
    }

    private void handleCheckIn(MemberSearchResult member) {
        try {
            Attendance visit = memberService.checkInMember(member.getMemberId());
            
            // 1. Update Occupancy UI
            currentOccupancy++;
            updateOccupancyUI();

            // 2. Add to Activity Log
            CheckInRecord record = new CheckInRecord(
                visit.getMemberId(), visit.getMemberName(), "Check In", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            recentActivity.add(0, record);
            refreshRecentActivity();

            // 3. Force Refresh of List
            refreshSearchResults();

        } catch (Exception e) {
            showAlert("Check-In Failed", e.getMessage());
        }
    }

    private void handleCheckOut(MemberSearchResult member) {
        try {
            memberService.checkOutMember(member.getMemberId());
            
            // 1. Update Occupancy UI
            currentOccupancy--;
            if (currentOccupancy < 0) currentOccupancy = 0;
            updateOccupancyUI();

            // 2. Add to Activity Log
            CheckInRecord record = new CheckInRecord(
                member.getMemberId(), member.getName(), "Check Out",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            recentActivity.add(0, record);
            refreshRecentActivity();

            // 3. Force Refresh of List
            refreshSearchResults();

        } catch (Exception e) {
            showAlert("Check-Out Failed", e.getMessage());
        }
    }

    private void updateOccupancyUI() {
        // FIX: Update the text displayed on screen
        currentCountText.setText(String.valueOf(currentOccupancy));
        occupancyBar.setProgress((double) currentOccupancy / maxCapacity);
    }

    private void refreshSearchResults() {
        // Use the actual text currently in the box to re-run search
        String currentText = searchField.getText();
        if (!currentText.isEmpty()) {
            searchMembers(currentText);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ... (createRecentActivitySection, refreshRecentActivity, createActivityItem, getInitials remain same) ...
    private VBox createRecentActivitySection() {
        VBox section = new VBox(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));
        Text title = new Text("Recent Activity");
        title.getStyleClass().add("text-h4");
        recentActivityBox = new VBox(8);
        ScrollPane scrollPane = new ScrollPane(recentActivityBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background-color: transparent;");
        refreshRecentActivity();
        section.getChildren().addAll(title, new Separator(), scrollPane);
        return section;
    }

    private void refreshRecentActivity() {
        recentActivityBox.getChildren().clear();
        for (CheckInRecord record : recentActivity) {
            recentActivityBox.getChildren().add(createActivityItem(record));
        }
    }

    private HBox createActivityItem(CheckInRecord record) {
        HBox item = new HBox(16);
        item.setPadding(new Insets(12));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6px;");
        Text memberName = new Text(record.getMemberName());
        memberName.setStyle("-fx-font-weight: 600;");
        Text details = new Text(record.getAction() + " • " + record.getTimestamp());
        item.getChildren().addAll(memberName, details);
        return item;
    }

    private String getInitials(String name) {
        if (name == null) return "??";
        String[] parts = name.split(" ");
        return (parts.length >= 2) ? parts[0].substring(0,1)+parts[1].substring(0,1) : name.substring(0,Math.min(2,name.length()));
    }

    public static class MemberSearchResult {
        private final String memberId, name, planType, status;
        private final boolean checkedIn;
        public MemberSearchResult(String id, String n, String p, String s, boolean c) {
            this.memberId=id; this.name=n; this.planType=p; this.status=s; this.checkedIn=c;
        }
        public String getMemberId() { return memberId; }
        public String getName() { return name; }
        public String getPlanType() { return planType; }
        public String getStatus() { return status; }
        public boolean isCheckedIn() { return checkedIn; }
    }

    public static class CheckInRecord {
        private final String memberId, memberName, action, timestamp;
        public CheckInRecord(String id, String n, String a, String t) {
            this.memberId=id; this.memberName=n; this.action=a; this.timestamp=t;
        }
        public String getMemberId() { return memberId; }
        public String getMemberName() { return memberName; }
        public String getAction() { return action; }
        public String getTimestamp() { return timestamp; }
    }
}