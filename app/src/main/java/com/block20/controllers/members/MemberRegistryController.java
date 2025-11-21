package com.block20.controllers.members;

import com.block20.models.Member;
import com.block20.services.MemberService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.function.Consumer;

public class MemberRegistryController extends ScrollPane {
    private VBox contentContainer;
    private TableView<Member> membersTable;
    private ObservableList<Member> allMembers;
    private ObservableList<Member> filteredMembers;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> planFilter;
    private Consumer<String> navigationHandler;

    // NEW: The connection to the backend
    private MemberService memberService;

    // UPDATED CONSTRUCTOR: Now accepts MemberService
    public MemberRegistryController(Consumer<String> navigationHandler, MemberService memberService) {
        this.navigationHandler = navigationHandler;
        this.memberService = memberService; // Save the service
        
        this.allMembers = FXCollections.observableArrayList();
        this.filteredMembers = FXCollections.observableArrayList();
        
        initialize();
        loadMembersFromBackend(); // Load real data!
    }

    private void initialize() {
        // Configure ScrollPane
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
            createSearchAndFilters(),
            createStatsBar(),
            createMembersTable()
        );
        
        setContent(contentContainer);
    }

    // NEW: Fetch data from the Service (Layer 3)
    private void loadMembersFromBackend() {
        allMembers.clear();
        if (memberService != null) {
            // This calls the backend!
            allMembers.addAll(memberService.getAllMembers());
        }
        filterMembers();
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
        // Note: In a real app, we would calculate these stats dynamically from 'allMembers'
        HBox statsBar = new HBox(24);
        statsBar.getStyleClass().add("stats-bar");
        statsBar.setPadding(new Insets(16, 20, 16, 20));
        statsBar.setAlignment(Pos.CENTER_LEFT);

        statsBar.getChildren().addAll(
            createStatItem("Total Members", String.valueOf(allMembers.size()), "#2563EB"),
            createStatItem("Active", "0", "#10B981"), // Placeholder
            createStatItem("Expired", "0", "#F59E0B") // Placeholder
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

        // --- UPDATED COLUMN MAPPINGS TO MATCH BACKEND MODEL ---

        // Member ID
        TableColumn<Member, String> idCol = new TableColumn<>("Member ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMemberId()));

        // Name (Mapped to getFullName in backend)
        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));

        // Email
        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        // Phone
        TableColumn<Member, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone()));

        // Plan
        TableColumn<Member, String> planCol = new TableColumn<>("Plan");
        planCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlanType()));

        // Status
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

        // Actions
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
                if (empty) setGraphic(null);
                else setGraphic(viewButton);
            }
        });

        membersTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, planCol, statusCol, actionsCol);
        membersTable.setPlaceholder(new Label("No members found."));

        container.getChildren().addAll(tableHeader, membersTable);
        VBox.setVgrow(membersTable, Priority.ALWAYS);
        return container;
    }

    private void showMemberProfile(Member member) {
        // Simple Alert for now (since our backend model is basic)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Member Profile");
        alert.setHeaderText(member.getFullName());
        alert.setContentText("ID: " + member.getMemberId() + "\nEmail: " + member.getEmail() + "\nPlan: " + member.getPlanType());
        alert.showAndWait();
    }

    private void filterMembers() {
        filteredMembers.clear();
        String searchText = searchField.getText().toLowerCase().trim();
        String statusValue = statusFilter.getValue();
        String planValue = planFilter.getValue();

        for (Member member : allMembers) {
            boolean matchesSearch = searchText.isEmpty() ||
                (member.getFullName() != null && member.getFullName().toLowerCase().contains(searchText)) ||
                (member.getEmail() != null && member.getEmail().toLowerCase().contains(searchText));

            boolean matchesStatus = statusValue.equals("All Statuses") ||
                (member.getStatus() != null && member.getStatus().equals(statusValue));

            boolean matchesPlan = planValue.equals("All Plans") ||
                (member.getPlanType() != null && member.getPlanType().equals(planValue));

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
}