package com.block20.controllers.members;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.function.Consumer;

public class MemberRegistryController {
    private VBox mainContainer;
    private TableView<Member> membersTable;
    private ObservableList<Member> allMembers;
    private ObservableList<Member> filteredMembers;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> planFilter;
    private Consumer<String> navigationHandler;

    public MemberRegistryController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        this.allMembers = FXCollections.observableArrayList();
        this.filteredMembers = FXCollections.observableArrayList();
        loadMockData();
        initialize();
    }

    private void initialize() {
        mainContainer = new VBox(24);
        mainContainer.getStyleClass().add("main-content");
        mainContainer.setPadding(new Insets(32));

        mainContainer.getChildren().addAll(
            createHeader(),
            createSearchAndFilters(),
            createStatsBar(),
            createMembersTable()
        );
    }

    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Text title = new Text("Member Registry");
        title.getStyleClass().add("text-h2");
        Text subtitle = new Text("Search, view, and manage all gym members");
        subtitle.getStyleClass().add("text-muted");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button enrollButton = new Button("+ New Member Enrollment");
        enrollButton.getStyleClass().addAll("btn", "btn-primary");
        enrollButton.setOnAction(e -> navigationHandler.accept("enrollment-new"));

        header.getChildren().addAll(titleBox, spacer, enrollButton);
        return header;
    }

    private VBox createSearchAndFilters() {
        VBox container = new VBox(16);
        container.getStyleClass().add("card");
        container.setPadding(new Insets(20));

        // Search bar
        HBox searchBox = new HBox(12);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = new Label("🔍");
        searchLabel.setStyle("-fx-font-size: 18px;");

        searchField = new TextField();
        searchField.setPromptText("Search by name, email, phone, or membership ID...");
        searchField.getStyleClass().add("search-input");
        searchField.setPrefWidth(500);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMembers());
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().addAll("btn", "btn-primary");
        searchButton.setOnAction(e -> filterMembers());

        searchBox.getChildren().addAll(searchLabel, searchField, searchButton);

        // Filters row
        HBox filtersBox = new HBox(16);
        filtersBox.setAlignment(Pos.CENTER_LEFT);

        Text filtersLabel = new Text("Filters:");
        filtersLabel.getStyleClass().add("text-body");

        // Status filter
        VBox statusBox = new VBox(4);
        Label statusLabel = new Label("Status");
        statusLabel.getStyleClass().add("text-caption");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "Active", "Expired", "Pending", "Suspended");
        statusFilter.setValue("All Statuses");
        statusFilter.setOnAction(e -> filterMembers());
        statusBox.getChildren().addAll(statusLabel, statusFilter);

        // Plan filter
        VBox planBox = new VBox(4);
        Label planLabel = new Label("Plan Type");
        planLabel.getStyleClass().add("text-caption");
        planFilter = new ComboBox<>();
        planFilter.getItems().addAll("All Plans", "Basic", "Premium", "Elite", "Student");
        planFilter.setValue("All Plans");
        planFilter.setOnAction(e -> filterMembers());
        planBox.getChildren().addAll(planLabel, planFilter);

        Button clearButton = new Button("Clear Filters");
        clearButton.getStyleClass().addAll("btn", "btn-ghost");
        clearButton.setOnAction(e -> clearFilters());

        filtersBox.getChildren().addAll(filtersLabel, statusBox, planBox, clearButton);

        container.getChildren().addAll(searchBox, new Separator(), filtersBox);
        return container;
    }

    private HBox createStatsBar() {
        HBox statsBar = new HBox(24);
        statsBar.getStyleClass().add("stats-bar");
        statsBar.setPadding(new Insets(16, 20, 16, 20));
        statsBar.setAlignment(Pos.CENTER_LEFT);

        statsBar.getChildren().addAll(
            createStatItem("Total Members", String.valueOf(allMembers.size()), "#2563EB"),
            createStatItem("Active", "1,089", "#10B981"),
            createStatItem("Expired", "47", "#F59E0B"),
            createStatItem("Pending Renewal", "35", "#F59E0B"),
            createStatItem("Suspended", "3", "#EF4444")
        );

        return statsBar;
    }

    private VBox createStatItem(String label, String value, String color) {
        VBox item = new VBox(4);
        item.setAlignment(Pos.CENTER_LEFT);

        Text valueText = new Text(value);
        valueText.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-fill: " + color + ";");

        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-fill: #64748B;");

        item.getChildren().addAll(valueText, labelText);
        return item;
    }

    private VBox createMembersTable() {
        VBox container = new VBox(12);
        container.getStyleClass().add("card");
        container.setPadding(new Insets(20));

        HBox tableHeader = new HBox(16);
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        Text tableTitle = new Text("Members List");
        tableTitle.getStyleClass().add("text-h4");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text resultsCount = new Text(filteredMembers.size() + " results");
        resultsCount.getStyleClass().add("text-muted");

        tableHeader.getChildren().addAll(tableTitle, spacer, resultsCount);

        // Create table
        membersTable = new TableView<>();
        membersTable.setItems(filteredMembers);
        membersTable.getStyleClass().add("data-table");
        membersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Member ID column
        TableColumn<Member, String> idCol = new TableColumn<>("Member ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMemberId()));
        idCol.setPrefWidth(100);

        // Name column
        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(180);

        // Email column
        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        emailCol.setPrefWidth(200);

        // Phone column
        TableColumn<Member, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone()));
        phoneCol.setPrefWidth(120);

        // Plan column
        TableColumn<Member, String> planCol = new TableColumn<>("Plan");
        planCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlanType()));
        planCol.setPrefWidth(100);

        // Status column with colored badges
        TableColumn<Member, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<Member, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("badge");
                    badge.getStyleClass().add("badge-" + status.toLowerCase());
                    setGraphic(badge);
                }
            }
        });
        statusCol.setPrefWidth(100);

        // Actions column
        TableColumn<Member, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<Member, Void>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.getStyleClass().addAll("btn", "btn-sm", "btn-primary");
                viewButton.setOnAction(e -> {
                    Member member = getTableView().getItems().get(getIndex());
                    showMemberProfile(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
        actionsCol.setPrefWidth(100);

        membersTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, planCol, statusCol, actionsCol);
        membersTable.setPlaceholder(new Label("No members found. Try adjusting your search or filters."));

        container.getChildren().addAll(tableHeader, membersTable);
        VBox.setVgrow(membersTable, Priority.ALWAYS);
        return container;
    }

    private void showMemberProfile(Member member) {
        // Create modal dialog for member profile
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Member Profile");
        dialog.setHeaderText(null);

        VBox content = createMemberProfileContent(member);
        content.setPrefWidth(700);
        content.setPrefHeight(600);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        // Style the dialog
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/com/block20/styles/main.css").toExternalForm()
        );

        dialog.showAndWait();
    }

    private VBox createMemberProfileContent(Member member) {
        VBox container = new VBox(24);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("member-profile-modal");

        // Header with member photo and basic info
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        // Photo placeholder
        VBox photoBox = new VBox();
        photoBox.setPrefSize(100, 100);
        photoBox.setStyle("-fx-background-color: #E0E7FF; -fx-background-radius: 50%; -fx-alignment: center;");
        Text initials = new Text(getInitials(member.getName()));
        initials.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        photoBox.getChildren().add(initials);

        VBox infoBox = new VBox(8);
        Text name = new Text(member.getName());
        name.getStyleClass().add("text-h3");
        
        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = new Label(member.getStatus());
        statusBadge.getStyleClass().addAll("badge", "badge-" + member.getStatus().toLowerCase());
        Text memberSince = new Text("Member since " + member.getJoinDate());
        memberSince.getStyleClass().add("text-muted");
        statusBox.getChildren().addAll(statusBadge, memberSince);

        infoBox.getChildren().addAll(name, new Text("ID: " + member.getMemberId()), statusBox);

        header.getChildren().addAll(photoBox, infoBox);

        // Contact Information
        VBox contactSection = createProfileSection("Contact Information", new String[][]{
            {"Email", member.getEmail()},
            {"Phone", member.getPhone()},
            {"Emergency Contact", member.getEmergencyContact()},
            {"Address", member.getAddress()}
        });

        // Membership Details
        VBox membershipSection = createProfileSection("Membership Details", new String[][]{
            {"Plan Type", member.getPlanType()},
            {"Start Date", member.getStartDate()},
            {"Expiry Date", member.getExpiryDate()},
            {"Monthly Fee", "$" + member.getMonthlyFee()},
            {"Last Payment", member.getLastPayment()}
        });

        // Activity Summary
        VBox activitySection = createProfileSection("Activity Summary", new String[][]{
            {"Total Check-Ins", String.valueOf(member.getTotalCheckIns())},
            {"Check-Ins This Month", String.valueOf(member.getCheckInsThisMonth())},
            {"Last Check-In", member.getLastCheckIn()},
            {"Average Weekly Visits", String.valueOf(member.getAvgWeeklyVisits())}
        });

        // Action buttons
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.setPadding(new Insets(16, 0, 0, 0));

        Button editButton = new Button("Edit Profile");
        editButton.getStyleClass().addAll("btn", "btn-primary");
        editButton.setOnAction(e -> editMemberProfile(member));

        Button renewButton = new Button("Process Renewal");
        renewButton.getStyleClass().addAll("btn", "btn-success");
        renewButton.setOnAction(e -> navigationHandler.accept("renewal-process"));

        Button suspendButton = new Button("Suspend");
        suspendButton.getStyleClass().addAll("btn", "btn-warning");

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("btn", "btn-danger");
        deleteButton.setOnAction(e -> deleteMember(member));

        actionButtons.getChildren().addAll(editButton, renewButton, suspendButton, deleteButton);

        ScrollPane scrollPane = new ScrollPane();
        VBox scrollContent = new VBox(24);
        scrollContent.getChildren().addAll(contactSection, membershipSection, activitySection);
        scrollPane.setContent(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().addAll(header, new Separator(), scrollPane, new Separator(), actionButtons);
        return container;
    }

    private VBox createProfileSection(String title, String[][] data) {
        VBox section = new VBox(12);
        section.getStyleClass().add("profile-section");

        Text sectionTitle = new Text(title);
        sectionTitle.getStyleClass().add("text-h5");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);

        for (int i = 0; i < data.length; i++) {
            Text label = new Text(data[i][0] + ":");
            label.getStyleClass().add("text-muted");
            label.setStyle("-fx-font-weight: 600;");

            Text value = new Text(data[i][1]);
            value.getStyleClass().add("text-body");

            grid.add(label, 0, i);
            grid.add(value, 1, i);
        }

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return parts[0].substring(0, 1) + parts[1].substring(0, 1);
        }
        return name.substring(0, Math.min(2, name.length()));
    }

    private void editMemberProfile(Member member) {
        // TODO: Implement edit functionality
        System.out.println("Edit member: " + member.getName());
    }

    private void deleteMember(Member member) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Member");
        alert.setHeaderText("Delete " + member.getName() + "?");
        alert.setContentText("This action cannot be undone. All member data will be permanently deleted.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                allMembers.remove(member);
                filteredMembers.remove(member);
                System.out.println("Deleted member: " + member.getName());
            }
        });
    }

    private void filterMembers() {
        filteredMembers.clear();
        
        String searchText = searchField.getText().toLowerCase().trim();
        String statusValue = statusFilter.getValue();
        String planValue = planFilter.getValue();

        for (Member member : allMembers) {
            boolean matchesSearch = searchText.isEmpty() ||
                member.getName().toLowerCase().contains(searchText) ||
                member.getEmail().toLowerCase().contains(searchText) ||
                member.getPhone().contains(searchText) ||
                member.getMemberId().toLowerCase().contains(searchText);

            boolean matchesStatus = statusValue.equals("All Statuses") ||
                member.getStatus().equals(statusValue);

            boolean matchesPlan = planValue.equals("All Plans") ||
                member.getPlanType().equals(planValue);

            if (matchesSearch && matchesStatus && matchesPlan) {
                filteredMembers.add(member);
            }
        }
    }

    private void clearFilters() {
        searchField.clear();
        statusFilter.setValue("All Statuses");
        planFilter.setValue("All Plans");
        filterMembers();
    }

    private void loadMockData() {
        // Mock data for demonstration
        allMembers.addAll(
            new Member("M001", "John Smith", "john.smith@email.com", "555-0101", "Premium", "Active", 
                "2024-01-15", "2025-01-15", "2024-11-05", "85.00", "2024-11-01", 
                "Jane Smith - 555-0102", "123 Main St, City", 156, 18, "2024-11-10", 4.2),
            new Member("M002", "Sarah Johnson", "sarah.j@email.com", "555-0102", "Basic", "Active",
                "2024-03-20", "2025-03-20", "2024-10-20", "50.00", "2024-10-20",
                "Mike Johnson - 555-0103", "456 Oak Ave, City", 89, 12, "2024-11-11", 3.1),
            new Member("M003", "Michael Brown", "m.brown@email.com", "555-0103", "Elite", "Active",
                "2023-06-10", "2025-06-10", "2024-06-10", "120.00", "2024-11-01",
                "Lisa Brown - 555-0104", "789 Pine Rd, City", 342, 22, "2024-11-09", 5.5),
            new Member("M004", "Emily Davis", "emily.d@email.com", "555-0104", "Student", "Expired",
                "2024-01-05", "2024-11-05", "2024-08-15", "35.00", "2024-08-15",
                "Robert Davis - 555-0105", "321 Elm St, City", 67, 8, "2024-10-28", 2.3),
            new Member("M005", "David Wilson", "d.wilson@email.com", "555-0105", "Premium", "Active",
                "2024-02-14", "2025-02-14", "2024-11-01", "85.00", "2024-11-01",
                "Anna Wilson - 555-0106", "654 Maple Dr, City", 123, 16, "2024-11-12", 3.8),
            new Member("M006", "Jessica Martinez", "jess.m@email.com", "555-0106", "Basic", "Pending",
                "2024-10-01", "2024-11-20", "2024-11-10", "50.00", "2024-10-01",
                "Carlos Martinez - 555-0107", "987 Birch Ln, City", 12, 4, "2024-11-08", 1.2),
            new Member("M007", "Robert Taylor", "r.taylor@email.com", "555-0107", "Premium", "Suspended",
                "2023-12-01", "2024-12-01", "2024-11-01", "85.00", "2024-09-01",
                "Emma Taylor - 555-0108", "147 Cedar Ct, City", 234, 0, "2024-09-15", 0.0),
            new Member("M008", "Amanda Anderson", "amanda.a@email.com", "555-0108", "Elite", "Active",
                "2023-08-22", "2025-08-22", "2024-08-22", "120.00", "2024-11-01",
                "Tom Anderson - 555-0109", "258 Spruce Way, City", 298, 20, "2024-11-11", 4.8),
            new Member("M009", "Christopher Lee", "chris.lee@email.com", "555-0109", "Basic", "Expired",
                "2024-04-10", "2024-10-10", "2024-09-01", "50.00", "2024-09-01",
                "Nancy Lee - 555-0110", "369 Ash Blvd, City", 45, 2, "2024-09-30", 1.5),
            new Member("M010", "Jennifer White", "jen.white@email.com", "555-0110", "Premium", "Active",
                "2024-05-18", "2025-05-18", "2024-11-05", "85.00", "2024-11-05",
                "Kevin White - 555-0111", "741 Walnut Ave, City", 102, 14, "2024-11-12", 3.5)
        );

        filteredMembers.addAll(allMembers);
    }

    public VBox getView() {
        return mainContainer;
    }

    // Member data class
    public static class Member {
        private final String memberId;
        private final String name;
        private final String email;
        private final String phone;
        private final String planType;
        private final String status;
        private final String joinDate;
        private final String expiryDate;
        private final String startDate;
        private final String monthlyFee;
        private final String lastPayment;
        private final String emergencyContact;
        private final String address;
        private final int totalCheckIns;
        private final int checkInsThisMonth;
        private final String lastCheckIn;
        private final double avgWeeklyVisits;

        public Member(String memberId, String name, String email, String phone, String planType, 
                     String status, String joinDate, String expiryDate, String startDate,
                     String monthlyFee, String lastPayment, String emergencyContact, String address,
                     int totalCheckIns, int checkInsThisMonth, String lastCheckIn, double avgWeeklyVisits) {
            this.memberId = memberId;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.planType = planType;
            this.status = status;
            this.joinDate = joinDate;
            this.expiryDate = expiryDate;
            this.startDate = startDate;
            this.monthlyFee = monthlyFee;
            this.lastPayment = lastPayment;
            this.emergencyContact = emergencyContact;
            this.address = address;
            this.totalCheckIns = totalCheckIns;
            this.checkInsThisMonth = checkInsThisMonth;
            this.lastCheckIn = lastCheckIn;
            this.avgWeeklyVisits = avgWeeklyVisits;
        }

        // Getters
        public String getMemberId() { return memberId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getPlanType() { return planType; }
        public String getStatus() { return status; }
        public String getJoinDate() { return joinDate; }
        public String getExpiryDate() { return expiryDate; }
        public String getStartDate() { return startDate; }
        public String getMonthlyFee() { return monthlyFee; }
        public String getLastPayment() { return lastPayment; }
        public String getEmergencyContact() { return emergencyContact; }
        public String getAddress() { return address; }
        public int getTotalCheckIns() { return totalCheckIns; }
        public int getCheckInsThisMonth() { return checkInsThisMonth; }
        public String getLastCheckIn() { return lastCheckIn; }
        public double getAvgWeeklyVisits() { return avgWeeklyVisits; }
    }
}
