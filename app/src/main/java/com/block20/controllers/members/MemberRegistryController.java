package com.block20.controllers.members;

import com.block20.models.Member;
import com.block20.services.MemberService;
import com.block20.models.AuditLog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.List;

public class MemberRegistryController extends ScrollPane {
    private VBox contentContainer;
    private TableView<Member> membersTable;
    private ObservableList<Member> allMembers;
    private ObservableList<Member> filteredMembers;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> planFilter;
    private HBox statsBar;
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
        refreshStatsBar();
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

VBox historyBox = new VBox(8);
        historyBox.setPadding(new Insets(10, 0, 0, 0));
        Label historyTitle = new Label("Activity History");
        historyTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748B;");
        
        VBox historyList = new VBox(4);
        List<AuditLog> logs = memberService.getMemberHistory(member.getMemberId());
        
        if (logs.isEmpty()) {
            historyList.getChildren().add(new Label("No history recorded."));
        } else {
            for (AuditLog log : logs) {
                Label entry = new Label("• " + log.getTimestampFormatted() + ": " + log.getAction());
                Tooltip tip = new Tooltip(log.getDetails());
                entry.setTooltip(tip); // Hover to see details
                historyList.getChildren().add(entry);
            }
        }
        historyBox.getChildren().addAll(historyTitle, new Separator(), historyList);

        // Action Buttons
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> showEditDialog(member));
        
        String nextStatus = member.getStatus().equalsIgnoreCase("Suspended") ? "Active" : "Suspended";
        Button statusBtn = new Button(nextStatus.equalsIgnoreCase("Active") ? "Activate" : "Suspend");
        statusBtn.getStyleClass().add("btn-warning");
        statusBtn.setOnAction(e -> changeMemberStatus(member, nextStatus, !"Active".equalsIgnoreCase(nextStatus)));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> deleteMember(member));
        
        actions.getChildren().addAll(editBtn, statusBtn, deleteBtn);

        container.getChildren().addAll(header, new Separator(), grid, historyBox, new Separator(), actions);
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

    private void showCreateMemberDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Member");
        dialog.setHeaderText("Add a member in three quick steps");

        ButtonType createBtn = new ButtonType("Create Member", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        TextField phoneField = new TextField();
        phoneField.setPromptText("555-123-0101");

        ComboBox<String> planField = new ComboBox<>();
        planField.getItems().addAll("Basic", "Premium", "Elite", "Student");
        planField.getSelectionModel().selectFirst();
        planField.setPrefWidth(220);

        grid.add(new Label("Full Name"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Membership Plan"), 0, 3);
        grid.add(planField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Node submitButton = dialog.getDialogPane().lookupButton(createBtn);
        submitButton.disableProperty().bind(
            nameField.textProperty().isEmpty()
                .or(emailField.textProperty().isEmpty())
                .or(phoneField.textProperty().isEmpty())
        );

        dialog.showAndWait().ifPresent(result -> {
            if (result == createBtn) {
                try {
                    Member created = memberService.registerMember(
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        planField.getValue()
                    );
                    loadMembersFromBackend();
                    showAlert("Member Created", created.getFullName() + " has been added to the registry.");
                } catch (Exception ex) {
                    showAlert("Unable to create member", ex.getMessage());
                }
            }
        });
    }

    private void changeMemberStatus(Member member, String targetStatus, boolean requireReason) {
        if (member.getStatus() != null && member.getStatus().equalsIgnoreCase(targetStatus)) {
            showAlert("No Change", member.getFullName() + " is already " + targetStatus + ".");
            return;
        }

        String reason = "Manual update";
        if (requireReason) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reason Required");
            dialog.setHeaderText("Provide a quick note for setting status to " + targetStatus);
            dialog.setContentText("Reason:");
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return;
            }
            reason = result.get().isBlank() ? "Not provided" : result.get().trim();
        }

        boolean removedAccess = !"Active".equalsIgnoreCase(targetStatus);
        if (removedAccess && memberService.isMemberCheckedIn(member.getMemberId())) {
            try {
                memberService.checkOutMember(member.getMemberId());
                reason += " | Auto-checkout enforced";
            } catch (Exception ex) {
                System.err.println("Failed to auto checkout: " + ex.getMessage());
            }
        }

        try {
            memberService.changeMemberStatus(member.getMemberId(), targetStatus, reason);
            loadMembersFromBackend();
            showAlert("Status Updated", member.getFullName() + " is now " + targetStatus + ".");
        } catch (Exception ex) {
            showAlert("Unable to update", ex.getMessage());
        }
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
        HBox actions = new HBox(8);
        Button newMemberButton = new Button("+ New Member");
        newMemberButton.getStyleClass().addAll("btn", "btn-secondary");
        newMemberButton.setOnAction(e -> showCreateMemberDialog());

        Button enrollButton = new Button("+ New Member Enrollment");
        enrollButton.getStyleClass().addAll("btn", "btn-primary");
        enrollButton.setOnAction(e -> navigationHandler.accept("enrollment-new"));
        actions.getChildren().addAll(newMemberButton, enrollButton);

        header.getChildren().addAll(titleBox, spacer, actions);
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
        statsBar = new HBox(24);
        statsBar.getStyleClass().add("stats-bar");
        statsBar.setPadding(new Insets(16, 20, 16, 20));
        refreshStatsBar();
        return statsBar;
    }

    private void refreshStatsBar() {
        if (statsBar == null) {
            return;
        }
        statsBar.getChildren().clear();
        long total = allMembers.size();
        long active = allMembers.stream().filter(m -> "Active".equalsIgnoreCase(m.getStatus())).count();
        long frozen = allMembers.stream().filter(m -> "Frozen".equalsIgnoreCase(m.getStatus())).count();
        long inactive = allMembers.stream().filter(m -> "Inactive".equalsIgnoreCase(m.getStatus())).count();
        statsBar.getChildren().addAll(
            createStatItem("Total Members", String.valueOf(total), "#2563EB"),
            createStatItem("Active", String.valueOf(active), "#10B981"),
            createStatItem("Frozen", String.valueOf(frozen), "#FACC15"),
            createStatItem("Inactive", String.valueOf(inactive), "#EF4444")
        );
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