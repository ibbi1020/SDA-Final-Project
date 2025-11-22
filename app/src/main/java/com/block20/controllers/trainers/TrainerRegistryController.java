/*
 * Block20 Gym Management System
 * Trainer Registry Controller - Register and Manage Trainers
 */
package com.block20.controllers.trainers;

import com.block20.models.Trainer;
import com.block20.services.TrainerService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for comprehensive trainer management:
 * 1. Trainer List (search, filter, view all trainers)
 * 2. Register New Trainer
 * 3. View/Edit Trainer Profile
 * 4. Manage Trainer Schedules
 */
public class TrainerRegistryController extends ScrollPane {
    
    private VBox contentContainer;
    private Consumer<String> navigationHandler;
    private final TrainerService trainerService;

    private VBox tableRows;
    private TextField searchField;
    private Label totalTrainersValue;
    private Label activeTrainersValue;
    private Label onLeaveValue;
    private Label sessionsPerMonthValue;
    private List<Trainer> currentTrainers = Collections.emptyList();
    private Trainer selectedTrainer;
    
    /**
     * Constructor
     */
    public TrainerRegistryController(Consumer<String> navigationHandler, TrainerService trainerService) {
        this.navigationHandler = navigationHandler;
        this.trainerService = trainerService;
        initializeView();
    }
    
    /**
     * Initialize the main view
     */
    private void initializeView() {
        // Configure ScrollPane
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(30));
        contentContainer.getStyleClass().add("content-container");
        
        // Header
        HBox header = createHeader();
        
        // Search and action bar
        HBox actionBar = createActionBar();
        
        // Stats bar
        HBox statsBar = createStatsBar();
        
        // Trainers table
        VBox tableSection = createTrainersTable();
        
        contentContainer.getChildren().addAll(header, actionBar, statsBar, tableSection);
        refreshTrainerData();
        
        // Set content
        setContent(contentContainer);
    }
    
    // ==================== MAIN VIEW COMPONENTS ====================
    
    /**
     * Create header
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Trainer Registry");
        titleLabel.getStyleClass().add("page-title");
        
        Label subtitleLabel = new Label("Register and manage gym trainers and their schedules");
        subtitleLabel.getStyleClass().add("page-subtitle");
        
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    /**
     * Create action bar with search and register button
     */
    private HBox createActionBar() {
        HBox actionBar = new HBox(15);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        searchField = new TextField();
        searchField.setPromptText("🔍 Search trainers by name, specialization, or ID...");
        searchField.getStyleClass().add("search-input");
        searchField.setPrefWidth(500);
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshTrainerData());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Register new trainer button
        Button registerButton = new Button("+ Register Trainer");
        registerButton.getStyleClass().add("btn-primary");
        registerButton.setOnAction(e -> showRegisterTrainerDialog());
        
        actionBar.getChildren().addAll(searchField, spacer, registerButton);
        
        return actionBar;
    }
    
    /**
     * Create stats bar
     */
    private HBox createStatsBar() {
        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(15));
        statsBar.getStyleClass().add("stats-bar");
        
        totalTrainersValue = createValueLabel("#3B82F6");
        activeTrainersValue = createValueLabel("#10B981");
        onLeaveValue = createValueLabel("#F59E0B");
        sessionsPerMonthValue = createValueLabel("#8B5CF6");

        statsBar.getChildren().addAll(
            createStatItem("Total Trainers", totalTrainersValue, "#3B82F6"),
            createStatItem("Active", activeTrainersValue, "#10B981"),
            createStatItem("On Leave", onLeaveValue, "#F59E0B"),
            createStatItem("Sessions / Month", sessionsPerMonthValue, "#8B5CF6")
        );
        
        return statsBar;
    }
    
    /**
     * Create stat item
     */
    private VBox createStatItem(String label, Label valueLabel, String color) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(10));
        statBox.getStyleClass().add("stat-item");
        
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        statBox.getChildren().addAll(valueLabel, labelText);
        
        return statBox;
    }

    private Label createValueLabel(String color) {
        Label label = new Label("0");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        return label;
    }
    
    /**
     * Create trainers table
     */
    private VBox createTrainersTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");
        
        // Table header
        HBox tableHeader = new HBox();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(10));
        
        Label idCol = new Label("ID");
        idCol.setPrefWidth(80);
        idCol.getStyleClass().add("table-header-cell");
        
        Label nameCol = new Label("Name");
        nameCol.setPrefWidth(200);
        nameCol.getStyleClass().add("table-header-cell");
        
        Label specializationCol = new Label("Specialization");
        specializationCol.setPrefWidth(200);
        specializationCol.getStyleClass().add("table-header-cell");
        
        Label statusCol = new Label("Status");
        statusCol.setPrefWidth(120);
        statusCol.getStyleClass().add("table-header-cell");
        
        Label certCol = new Label("Certification");
        certCol.setPrefWidth(150);
        certCol.getStyleClass().add("table-header-cell");
        
        Label sessionsCol = new Label("Sessions/Month");
        sessionsCol.setPrefWidth(150);
        sessionsCol.getStyleClass().add("table-header-cell");
        
        Label actionCol = new Label("Actions");
        actionCol.setPrefWidth(200);
        actionCol.getStyleClass().add("table-header-cell");
        
        tableHeader.getChildren().addAll(idCol, nameCol, specializationCol, statusCol, certCol, sessionsCol, actionCol);
        
        // Table rows
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("table-scroll");
        
        tableRows = new VBox(5);
        tableRows.setPadding(new Insets(10));
        scrollPane.setContent(tableRows);
        
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }
    
    /**
     * Create table row
     */
    private HBox createTableRow(Trainer trainer) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Trainer ID
        Label idLabel = new Label(trainer.getTrainerId());
        idLabel.setPrefWidth(80);
        
        // Name
        Label nameLabel = new Label(trainer.getFullName());
        nameLabel.setPrefWidth(200);
        nameLabel.setStyle("-fx-font-weight: 500;");
        
        // Specialization
        Label specializationLabel = new Label(trainer.getSpecialization());
        specializationLabel.setPrefWidth(200);
        
        // Status badge
        Label statusBadge = new Label(trainer.getStatus());
        statusBadge.setPrefWidth(120);
        statusBadge.getStyleClass().add("badge");
        statusBadge.getStyleClass().add("Active".equalsIgnoreCase(trainer.getStatus()) ? "badge-success" : "badge-warning");
        
        // Certification
        Label certLabel = new Label(trainer.getCertification() != null ? trainer.getCertification() : "N/A");
        certLabel.setPrefWidth(150);
        certLabel.setStyle("-fx-font-size: 12px;");
        
        // Sessions count
        Label sessionsLabel = new Label(String.valueOf(Math.max(trainer.getSessionsPerMonth(), 0)));
        sessionsLabel.setPrefWidth(150);
        sessionsLabel.setStyle("-fx-font-weight: 500;");
        
        // Action buttons
        HBox actionBox = new HBox(8);
        actionBox.setPrefWidth(200);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        Button viewButton = new Button("View");
        viewButton.getStyleClass().add("btn-primary-small");
        viewButton.setOnAction(e -> showTrainerProfile(trainer));
        
        actionBox.getChildren().addAll(viewButton);
        
        row.getChildren().addAll(idLabel, nameLabel, specializationLabel, statusBadge, certLabel, sessionsLabel, actionBox);
        
        return row;
    }
    
    // ==================== DIALOG VIEWS ====================
    
    /**
     * Show register trainer dialog
     */
    private void showRegisterTrainerDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register New Trainer");
        dialog.setHeaderText("Enter trainer information");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Form fields
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("John");
        
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Doe");
        
        TextField emailField = new TextField();
        emailField.setPromptText("john.doe@block20gym.com");
        
        TextField phoneField = new TextField();
        phoneField.setPromptText("(555) 123-4567");
        
        ComboBox<String> specializationBox = new ComboBox<>();
        specializationBox.getItems().addAll("Personal Training", "Yoga", "CrossFit", "Pilates", 
                                            "Strength Training", "Cardio", "Boxing", "Swimming");
        specializationBox.setPromptText("Select specialization");
        
        TextField certificationField = new TextField();
        certificationField.setPromptText("e.g., ACE-CPT, NASM-CPT");
        
        DatePicker hireDatePicker = new DatePicker(LocalDate.now());
        
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Additional notes...");
        notesArea.setPrefRowCount(3);
        
        // Add to grid
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Specialization:"), 0, 4);
        grid.add(specializationBox, 1, 4);
        grid.add(new Label("Certification:"), 0, 5);
        grid.add(certificationField, 1, 5);
        grid.add(new Label("Hire Date:"), 0, 6);
        grid.add(hireDatePicker, 1, 6);
        grid.add(new Label("Notes:"), 0, 7);
        grid.add(notesArea, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate and register trainer
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String specialization = specializationBox.getValue();
                
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || specialization == null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
                    return;
                }
                try {
                    Trainer trainer = trainerService.registerTrainer(
                        firstName,
                        lastName,
                        email,
                        phoneField.getText(),
                        specialization,
                        certificationField.getText(),
                        hireDatePicker.getValue(),
                        notesArea.getText()
                    );
                    showAlert(Alert.AlertType.INFORMATION, "Trainer Registered", "Trainer ID: " + trainer.getTrainerId());
                    searchField.clear();
                    refreshTrainerData();
                } catch (IllegalArgumentException ex) {
                    showAlert(Alert.AlertType.ERROR, "Could not register trainer", ex.getMessage());
                }
            }
        });
    }
    
    /**
     * Show trainer profile
     */
    private void showTrainerProfile(Trainer trainer) {
        selectedTrainer = trainer;
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Trainer Profile - " + trainer.getFullName());
        dialog.setHeaderText(trainer.getFullName() + " (" + trainer.getTrainerId() + ")");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);
        
        // Personal Information
        VBox personalInfo = new VBox(10);
        personalInfo.getStyleClass().add("info-card");
        personalInfo.setPadding(new Insets(15));
        
        Label personalTitle = new Label("Personal Information");
        personalTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(20);
        personalGrid.setVgap(8);
        
        addProfileRow(personalGrid, 0, "Trainer ID:", trainer.getTrainerId());
        addProfileRow(personalGrid, 1, "Name:", trainer.getFullName());
        addProfileRow(personalGrid, 2, "Email:", trainer.getEmail());
        addProfileRow(personalGrid, 3, "Phone:", trainer.getPhone());
        addProfileRow(personalGrid, 4, "Specialization:", trainer.getSpecialization());
        addProfileRow(personalGrid, 5, "Certification:", trainer.getCertification());
        
        personalInfo.getChildren().addAll(personalTitle, personalGrid);
        
        // Professional Details
        VBox professionalInfo = new VBox(10);
        professionalInfo.getStyleClass().add("info-card");
        professionalInfo.setPadding(new Insets(15));
        
        Label professionalTitle = new Label("Professional Details");
        professionalTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane professionalGrid = new GridPane();
        professionalGrid.setHgap(20);
        professionalGrid.setVgap(8);
        
        addProfileRow(professionalGrid, 0, "Status:", trainer.getStatus());
        addProfileRow(professionalGrid, 1, "Hire Date:", trainer.getHireDate() != null ? trainer.getHireDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "-");
        addProfileRow(professionalGrid, 2, "Sessions/Month:", String.valueOf(Math.max(trainer.getSessionsPerMonth(), 0)));
        addProfileRow(professionalGrid, 3, "Active Clients:", String.valueOf(Math.max(trainer.getActiveClients(), 0)));
        addProfileRow(professionalGrid, 4, "Total Sessions:", String.valueOf(Math.max(trainer.getTotalSessions(), 0)));
        
        professionalInfo.getChildren().addAll(professionalTitle, professionalGrid);
        
        content.getChildren().addAll(personalInfo, professionalInfo);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }
    
    /**
     * Add profile row
     */
    private void addProfileRow(GridPane grid, int row, String label, String value) {
        Label labelText = new Label(label);
        labelText.getStyleClass().add("form-label");
        labelText.setStyle("-fx-font-weight: 500; -fx-min-width: 150px;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 14px;");
        
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }
    
    private void refreshTrainerData() {
        if (trainerService == null) {
            return;
        }
        String keyword = searchField != null ? searchField.getText().trim() : "";
        currentTrainers = keyword.isEmpty()
                ? trainerService.getAllTrainers()
                : trainerService.searchTrainers(keyword);
        updateStats(currentTrainers);
        refreshTable(currentTrainers);
    }

    private void refreshTable(List<Trainer> trainers) {
        if (tableRows == null) {
            return;
        }
        tableRows.getChildren().clear();
        if (trainers.isEmpty()) {
            Label emptyState = new Label("No trainers found. Register a new trainer to get started.");
            emptyState.getStyleClass().add("text-muted");
            emptyState.setPadding(new Insets(16));
            tableRows.getChildren().add(emptyState);
            return;
        }
        for (Trainer trainer : trainers) {
            tableRows.getChildren().add(createTableRow(trainer));
        }
    }

    private void updateStats(List<Trainer> trainers) {
        int total = trainers.size();
        long active = trainers.stream().filter(t -> "Active".equalsIgnoreCase(t.getStatus())).count();
        long onLeave = total - active;
        int sessions = trainers.stream().mapToInt(Trainer::getSessionsPerMonth).sum();
        totalTrainersValue.setText(String.valueOf(total));
        activeTrainersValue.setText(String.valueOf(active));
        onLeaveValue.setText(String.valueOf(onLeave));
        sessionsPerMonthValue.setText(String.valueOf(sessions));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
