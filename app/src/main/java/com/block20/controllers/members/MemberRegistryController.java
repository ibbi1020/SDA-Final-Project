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

    // Service Dependency
    private MemberService memberService;

    public MemberRegistryController(Consumer<String> navigationHandler, MemberService memberService) {
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        
        this.allMembers = FXCollections.observableArrayList();
        this.filteredMembers = FXCollections.observableArrayList();
        
        initialize();
        loadMembersFromBackend();
    }

    private void initialize() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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

    private void loadMembersFromBackend() {
        allMembers.clear();
        if (memberService != null) {
            allMembers.addAll(memberService.getAllMembers());
        }
        filterMembers();
    }

    // --- 1. PROFILE VIEW LOGIC (The missing part) ---

    private void showMemberProfile(Member member) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Member Profile");
        dialog.setHeaderText(null);

        VBox content = createMemberProfileContent(member);
        content.setPrefWidth(600);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private VBox createMemberProfileContent(Member member) {
        VBox container = new VBox(24);
        container.setPadding(new Insets(24));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox photoBox = new VBox();
        photoBox.setPrefSize(80, 80);
        photoBox.setStyle("-fx-background-color: #E0E7FF; -fx-background-radius: 50%; -fx-alignment: center;");
        Text initials = new Text(member.getFullName().substring(0, 1));
        initials.setStyle("-fx-font-size: 32px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        photoBox.getChildren().add(initials);

        VBox infoBox = new VBox(8);
        Text name = new Text(member.getFullName());
        name.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        HBox statusBox = new HBox(8);
        Label statusBadge = new Label(member.getStatus());
        statusBadge.setStyle("-fx-background-color: " + ("Active".equals(member.getStatus()) ? "#D1FAE5" : "#FEE2E2") + "; -fx-padding: 4 8; -fx-background-radius: 4;");
        statusBox.getChildren().addAll(statusBadge, new Text("ID: " + member.getMemberId()));

        infoBox.getChildren().addAll(name, statusBox);
        header.getChildren().addAll(photoBox, infoBox);

        // Details Grid
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(12);
        addProfileRow(grid, 0, "Email:", member.getEmail());
        addProfileRow(grid, 1, "Phone:", member.getPhone());
        addProfileRow(grid, 2, "Plan:", member.getPlanType());
        addProfileRow(grid, 3, "Expires:", member.getExpiryDate().toString());

        // Action Buttons
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> showEditDialog(member));
        
        Button statusBtn = new Button(member.getStatus().equals("Suspended") ? "Activate" : "Suspend");
        statusBtn.getStyleClass().add("btn-warning");
        statusBtn.setOnAction(e -> toggleMemberStatus(member));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> deleteMember(member));
        
        actions.getChildren().addAll(editBtn, statusBtn, deleteBtn);

        container.getChildren().addAll(header, new Separator(), grid, new Separator(), actions);
        return container;
    }

    private void addProfileRow(GridPane grid, int row, String label, String value) {
        Text l = new Text(label); l.setStyle("-fx-font-weight: bold; -fx-fill: #64748B;");
        Text v = new Text(value);
        grid.add(l, 0, row); grid.add(v, 1, row);
    }

    // --- 2. EDIT / SUSPEND / DELETE LOGIC ---

    private void showEditDialog(Member member) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Editing " + member.getFullName());

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(member.getFullName());
        TextField emailField = new TextField(member.getEmail());
        TextField phoneField = new TextField(member.getPhone());

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2); grid.add(phoneField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveBtn) {
                try {
                    memberService.updateMemberDetails(member.getMemberId(), nameField.getText(), emailField.getText(), phoneField.getText(), "");
                    loadMembersFromBackend(); // Refresh UI
                } catch (Exception e) {
                    showAlert("Update Failed", e.getMessage());
                }
            }
        });
    }

private void toggleMemberStatus(Member member) {
        String newStatus = member.getStatus().equals("Suspended") ? "Active" : "Suspended";
        String message = "Member is now " + newStatus;

        // --- NEW SECURITY LOGIC ---
        // If we are suspending them, force a check-out if they are currently in the gym
        if ("Suspended".equals(newStatus)) {
            if (memberService.isMemberCheckedIn(member.getMemberId())) {
                try {
                    memberService.checkOutMember(member.getMemberId());
                    message += "\n(Auto-checked out from facility)";
                    System.out.println("Security: Force checked-out " + member.getFullName() + " due to suspension.");
                } catch (Exception e) {
                    System.err.println("Failed to force checkout: " + e.getMessage());
                }
            }
        }
        // ---------------------------

        memberService.changeMemberStatus(member.getMemberId(), newStatus);
        loadMembersFromBackend(); // Refresh the table to show Red badge
        showAlert("Status Updated", message);
    }

    private void deleteMember(Member member) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete " + member.getFullName() + "?");
        confirm.setContentText("This cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                memberService.deleteMember(member.getMemberId());
                loadMembersFromBackend();
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- 3. STANDARD UI COMPONENTS (Header, Filter, Table) ---

    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4);
        Text title = new Text("Member Registry"); title.getStyleClass().add("text-h2");
        Text subtitle = new Text("Search, view, and manage all gym members"); subtitle.getStyleClass().add("text-muted");
        titleBox.getChildren().addAll(title, subtitle);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
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
        
        HBox searchBox = new HBox(12);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Search by name, email, phone...");
        searchField.setPrefWidth(500);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMembers());
        searchBox.getChildren().addAll(new Label("🔍"), searchField);

        HBox filtersBox = new HBox(16);
        filtersBox.setAlignment(Pos.CENTER_LEFT);
        
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "Active", "Expired", "Suspended");
        statusFilter.setValue("All Statuses");
        statusFilter.setOnAction(e -> filterMembers());
        
        planFilter = new ComboBox<>();
        planFilter.getItems().addAll("All Plans", "Basic", "Premium", "Elite", "Student");
        planFilter.setValue("All Plans");
        planFilter.setOnAction(e -> filterMembers());
        
        Button clearBtn = new Button("Clear Filters");
        clearBtn.setOnAction(e -> { searchField.clear(); statusFilter.setValue("All Statuses"); planFilter.setValue("All Plans"); });
        
        filtersBox.getChildren().addAll(new Label("Filters:"), statusFilter, planFilter, clearBtn);
        container.getChildren().addAll(searchBox, new Separator(), filtersBox);
        return container;
    }

    private HBox createStatsBar() {
        HBox statsBar = new HBox(24);
        statsBar.getStyleClass().add("stats-bar");
        statsBar.setPadding(new Insets(16, 20, 16, 20));
        
        long total = allMembers.size();
        long active = allMembers.stream().filter(m -> "Active".equals(m.getStatus())).count();
        long suspended = allMembers.stream().filter(m -> "Suspended".equals(m.getStatus())).count();
        
        statsBar.getChildren().addAll(
            createStatItem("Total Members", String.valueOf(total), "#2563EB"),
            createStatItem("Active", String.valueOf(active), "#10B981"),
            createStatItem("Suspended", String.valueOf(suspended), "#EF4444")
        );
        return statsBar;
    }

    private VBox createStatItem(String label, String value, String color) {
        VBox item = new VBox(4);
        Text val = new Text(value); val.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-fill: " + color + ";");
        Text lbl = new Text(label); lbl.getStyleClass().add("text-caption");
        item.getChildren().addAll(val, lbl);
        return item;
    }

    private VBox createMembersTable() {
        VBox container = new VBox(12);
        container.getStyleClass().add("card");
        container.setPadding(new Insets(20));

        membersTable = new TableView<>();
        membersTable.setItems(filteredMembers);
        membersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Member, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMemberId()));

        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFullName()));

        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEmail()));

        TableColumn<Member, String> planCol = new TableColumn<>("Plan");
        planCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPlanType()));

        TableColumn<Member, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<Member, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    if ("Active".equals(item)) badge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
                    else badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<Member, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Member, Void>() {
            private final Button btn = new Button("View");
            {
                btn.getStyleClass().addAll("btn", "btn-sm", "btn-primary");
                btn.setOnAction(e -> showMemberProfile(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null); else setGraphic(btn);
            }
        });

        membersTable.getColumns().addAll(idCol, nameCol, emailCol, planCol, statusCol, actionCol);
        container.getChildren().add(membersTable);
        VBox.setVgrow(membersTable, Priority.ALWAYS);
        return container;
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
}