package com.block20.controllers.members;

import com.block20.models.Member;
import com.block20.services.MemberService;
import com.block20.models.AuditLog;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private ComboBox<String> expiryFilter;
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
        addProfileRow(grid, 4, "Address:", member.getAddress());
        addProfileRow(grid, 5, "Emergency Contact:", member.getEmergencyContactName());
        addProfileRow(grid, 6, "Emergency Phone:", member.getEmergencyContactPhone());
        addProfileRow(grid, 7, "Relationship:", member.getEmergencyContactRelationship());

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
        
        Button statusBtn = new Button("Change Status");
        statusBtn.getStyleClass().add("btn-warning");
        statusBtn.setOnAction(e -> showStatusChangeDialog(member));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> deleteMember(member));
        
        actions.getChildren().addAll(editBtn, statusBtn, deleteBtn);

        container.getChildren().addAll(header, new Separator(), grid, historyBox, new Separator(), actions);
        return container;
    }

    private void addProfileRow(GridPane grid, int row, String label, String value) {
        Text l = new Text(label); l.setStyle("-fx-font-weight: bold; -fx-fill: #64748B;");
        String displayValue = (value == null || value.isBlank()) ? "—" : value;
        Text v = new Text(displayValue);
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
        TextField addressField = new TextField(member.getAddress() == null ? "" : member.getAddress());
        TextField emergencyNameField = new TextField(member.getEmergencyContactName() == null ? "" : member.getEmergencyContactName());
        TextField emergencyPhoneField = new TextField(member.getEmergencyContactPhone() == null ? "" : member.getEmergencyContactPhone());
        TextField emergencyRelationshipField = new TextField(member.getEmergencyContactRelationship() == null ? "" : member.getEmergencyContactRelationship());

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2); grid.add(phoneField, 1, 2);
        grid.add(new Label("Address:"), 0, 3); grid.add(addressField, 1, 3);
        grid.add(new Label("Emergency Contact"), 0, 4); grid.add(emergencyNameField, 1, 4);
        grid.add(new Label("Emergency Phone"), 0, 5); grid.add(emergencyPhoneField, 1, 5);
        grid.add(new Label("Relationship"), 0, 6); grid.add(emergencyRelationshipField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveBtn) {
                try {
                    memberService.updateMemberDetails(
                        member.getMemberId(),
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText(),
                        emergencyNameField.getText(),
                        emergencyPhoneField.getText(),
                        emergencyRelationshipField.getText()
                    );
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
        TextField addressField = new TextField();
        addressField.setPromptText("123 Fitness Ave");
        TextField emergencyNameField = new TextField();
        emergencyNameField.setPromptText("Emergency contact name");
        TextField emergencyPhoneField = new TextField();
        emergencyPhoneField.setPromptText("Contact phone");
        TextField emergencyRelationshipField = new TextField();
        emergencyRelationshipField.setPromptText("Relationship");

        ComboBox<String> planField = new ComboBox<>();
        planField.getItems().addAll("Basic", "Premium", "Elite", "Student");
        planField.getSelectionModel().selectFirst();
        planField.setPrefWidth(220);

        Label duplicateWarning = new Label("Duplicate email detected. Open the existing profile instead.");
        duplicateWarning.setStyle("-fx-text-fill: #DC2626;");
        duplicateWarning.setVisible(false);
        BooleanProperty duplicateEmail = new SimpleBooleanProperty(false);

        grid.add(new Label("Full Name"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Address"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("Emergency Contact"), 0, 4);
        grid.add(emergencyNameField, 1, 4);
        grid.add(new Label("Emergency Phone"), 0, 5);
        grid.add(emergencyPhoneField, 1, 5);
        grid.add(new Label("Relationship"), 0, 6);
        grid.add(emergencyRelationshipField, 1, 6);
        grid.add(new Label("Membership Plan"), 0, 7);
        grid.add(planField, 1, 7);
        GridPane.setColumnSpan(duplicateWarning, 2);
        grid.add(duplicateWarning, 0, 8);

        dialog.getDialogPane().setContent(grid);

        Node submitButton = dialog.getDialogPane().lookupButton(createBtn);
        submitButton.disableProperty().bind(
            nameField.textProperty().isEmpty()
                .or(emailField.textProperty().isEmpty())
                .or(phoneField.textProperty().isEmpty())
                .or(emergencyNameField.textProperty().isEmpty())
                .or(emergencyPhoneField.textProperty().isEmpty())
                .or(emergencyRelationshipField.textProperty().isEmpty())
                .or(duplicateEmail)
        );

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean duplicate = emailExists(newVal);
            duplicateWarning.setVisible(duplicate);
            duplicateEmail.set(duplicate);
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == createBtn) {
                try {
                    Member created = memberService.registerMember(
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        planField.getValue(),
                        addressField.getText().trim(),
                        emergencyNameField.getText().trim(),
                        emergencyPhoneField.getText().trim(),
                        emergencyRelationshipField.getText().trim()
                    );
                    loadMembersFromBackend();
                    showAlert("Member Created", created.getFullName() + " has been added to the registry.");
                } catch (Exception ex) {
                    showAlert("Unable to create member", ex.getMessage());
                }
            }
        });
    }

    private void showStatusChangeDialog(Member member) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Member Status");
        dialog.setHeaderText("Current status: " + member.getStatus());

        ButtonType updateBtn = new ButtonType("Update Status", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateBtn, ButtonType.CANCEL);

        ComboBox<String> statusField = new ComboBox<>();
        statusField.getItems().addAll("Active", "Frozen", "Suspended", "Inactive");
        if (member.getStatus() != null && !statusField.getItems().contains(member.getStatus())) {
            statusField.getItems().add(member.getStatus());
        }
        statusField.setValue(member.getStatus());

        ComboBox<String> reasonField = new ComboBox<>();
        reasonField.getItems().addAll("Billing Issue", "Medical Hold", "Travel", "Member Request", "Policy Violation", "Other");
        reasonField.getSelectionModel().selectFirst();

        TextArea notesField = new TextArea();
        notesField.setPromptText("Optional note or ticket #");
        notesField.setPrefRowCount(3);

        double outstanding = memberService.getOutstandingBalance(member.getMemberId());
        Label outstandingLabel = new Label(String.format("Outstanding Balance: $%.2f", outstanding));
        outstandingLabel.setStyle(outstanding > 0 ? "-fx-text-fill: #DC2626;" : "-fx-text-fill: #059669;");

        CheckBox duesConfirmed = new CheckBox("Confirm dues are collected or waived");
        duesConfirmed.setVisible(outstanding > 0);
        duesConfirmed.setManaged(outstanding > 0);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));
        grid.add(new Label("New Status"), 0, 0);
        grid.add(statusField, 1, 0);
        grid.add(new Label("Reason"), 0, 1);
        grid.add(reasonField, 1, 1);
        grid.add(new Label("Notes"), 0, 2);
        grid.add(notesField, 1, 2);
        grid.add(outstandingLabel, 0, 3);
        grid.add(duesConfirmed, 1, 3);

        dialog.getDialogPane().setContent(grid);

        BooleanProperty requiresNote = new SimpleBooleanProperty(false);
        BooleanProperty requiresOutstandingAck = new SimpleBooleanProperty(outstanding > 0 && isAccessRestrictingStatus(statusField.getValue()));
        duesConfirmed.setDisable(!requiresOutstandingAck.get());

        reasonField.valueProperty().addListener((obs, oldVal, newVal) ->
            requiresNote.set("Other".equals(newVal))
        );

        statusField.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean needsAck = outstanding > 0 && isAccessRestrictingStatus(newVal);
            requiresOutstandingAck.set(needsAck);
            if (!needsAck) {
                duesConfirmed.setSelected(false);
            }
            duesConfirmed.setDisable(!needsAck);
        });

        BooleanBinding noteMissing = Bindings.createBooleanBinding(
            () -> notesField.getText().trim().isEmpty(),
            notesField.textProperty()
        );

        Node updateNode = dialog.getDialogPane().lookupButton(updateBtn);
        updateNode.disableProperty().bind(
            statusField.valueProperty().isNull()
                .or(statusField.valueProperty().isEqualTo(member.getStatus()))
                .or(reasonField.valueProperty().isNull())
                .or(requiresNote.and(noteMissing))
                .or(requiresOutstandingAck.and(duesConfirmed.selectedProperty().not()))
        );

        dialog.showAndWait().ifPresent(response -> {
            if (response == updateBtn) {
                String targetStatus = statusField.getValue();
                StringBuilder reasonBuilder = new StringBuilder(reasonField.getValue());
                if (!notesField.getText().isBlank()) {
                    reasonBuilder.append(" | ").append(notesField.getText().trim());
                }
                changeMemberStatus(member, targetStatus, reasonBuilder.toString());
            }
        });
    }

    private boolean isAccessRestrictingStatus(String status) {
        if (status == null) {
            return false;
        }
        return !"Active".equalsIgnoreCase(status);
    }

    private void changeMemberStatus(Member member, String targetStatus, String reason) {
        if (member.getStatus() != null && member.getStatus().equalsIgnoreCase(targetStatus)) {
            showAlert("No Change", member.getFullName() + " is already " + targetStatus + ".");
            return;
        }

        String finalReason = (reason == null || reason.isBlank()) ? "Manual update" : reason.trim();
        boolean removedAccess = isAccessRestrictingStatus(targetStatus);
        if (removedAccess && memberService.isMemberCheckedIn(member.getMemberId())) {
            try {
                memberService.checkOutMember(member.getMemberId());
                finalReason += " | Auto-checkout enforced";
            } catch (Exception ex) {
                System.err.println("Failed to auto checkout: " + ex.getMessage());
            }
        }

        try {
            memberService.changeMemberStatus(member.getMemberId(), targetStatus, finalReason);
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
        searchField.setPromptText("Search by name, ID, email, phone...");
        searchField.setPrefWidth(500);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMembers());
        searchBox.getChildren().addAll(new Label("🔍"), searchField);

        HBox filtersBox = new HBox(16);
        filtersBox.setAlignment(Pos.CENTER_LEFT);
        
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "Active", "Expired", "Suspended", "Frozen", "Inactive");
        statusFilter.setValue("All Statuses");
        statusFilter.setOnAction(e -> filterMembers());
        
        planFilter = new ComboBox<>();
        planFilter.getItems().addAll("All Plans", "Basic", "Premium", "Elite", "Student");
        planFilter.setValue("All Plans");
        planFilter.setOnAction(e -> filterMembers());

        expiryFilter = new ComboBox<>();
        expiryFilter.getItems().addAll("Any Expiration", "Expiring in 7 days", "Expiring in 30 days", "Expired");
        expiryFilter.setValue("Any Expiration");
        expiryFilter.setOnAction(e -> filterMembers());
        
        Button clearBtn = new Button("Clear Filters");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All Statuses");
            planFilter.setValue("All Plans");
            expiryFilter.setValue("Any Expiration");
        });
        
        filtersBox.getChildren().addAll(new Label("Filters:"), statusFilter, planFilter, expiryFilter, clearBtn);
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
        String expiryValue = expiryFilter != null ? expiryFilter.getValue() : "Any Expiration";
        LocalDate today = LocalDate.now();

        for (Member member : allMembers) {
            boolean matchesSearch = searchText.isEmpty() ||
                (member.getFullName() != null && member.getFullName().toLowerCase().contains(searchText)) ||
                (member.getEmail() != null && member.getEmail().toLowerCase().contains(searchText)) ||
                (member.getPhone() != null && member.getPhone().toLowerCase().contains(searchText)) ||
                (member.getMemberId() != null && member.getMemberId().toLowerCase().contains(searchText));

            boolean matchesStatus = statusValue.equals("All Statuses") ||
                (member.getStatus() != null && member.getStatus().equalsIgnoreCase(statusValue));

            boolean matchesPlan = planValue.equals("All Plans") ||
                (member.getPlanType() != null && member.getPlanType().equalsIgnoreCase(planValue));

            boolean matchesExpiry;
            if (member.getExpiryDate() == null) {
                matchesExpiry = expiryValue.equals("Any Expiration");
            } else {
                long daysUntilExpiry = ChronoUnit.DAYS.between(today, member.getExpiryDate());
                switch (expiryValue) {
                    case "Expiring in 7 days" -> matchesExpiry = daysUntilExpiry >= 0 && daysUntilExpiry <= 7;
                    case "Expiring in 30 days" -> matchesExpiry = daysUntilExpiry >= 0 && daysUntilExpiry <= 30;
                    case "Expired" -> matchesExpiry = member.getExpiryDate().isBefore(today);
                    default -> matchesExpiry = true;
                }
            }

            if (matchesSearch && matchesStatus && matchesPlan && matchesExpiry) {
                filteredMembers.add(member);
            }
        }
    }

    private boolean emailExists(String email) {
        if (memberService == null || email == null || email.isBlank()) {
            return false;
        }
        String normalized = email.trim().toLowerCase();
        return memberService.getAllMembers().stream()
            .anyMatch(existing -> existing.getEmail() != null && existing.getEmail().toLowerCase().equals(normalized));
    }
}