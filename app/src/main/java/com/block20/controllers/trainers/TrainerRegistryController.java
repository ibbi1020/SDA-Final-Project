/*
 * Block20 Gym Management System
 * Trainer Registry Controller - Register and Manage Trainers
 */
package com.block20.controllers.trainers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    
    // Current view state
    private TrainerData selectedTrainer;
    
    /**
     * Constructor
     */
    public TrainerRegistryController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
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
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search trainers by name, specialization, or ID...");
        searchField.getStyleClass().add("search-input");
        searchField.setPrefWidth(500);
        
        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Searching for: " + newVal);
            // In real implementation, filter the trainer list
        });
        
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
        
        statsBar.getChildren().addAll(
            createStatItem("Total Trainers", "8", "#3B82F6"),
            createStatItem("Active", "7", "#10B981"),
            createStatItem("On Leave", "1", "#F59E0B"),
            createStatItem("Sessions Today", "12", "#8B5CF6")
        );
        
        return statsBar;
    }
    
    /**
     * Create stat item
     */
    private VBox createStatItem(String label, String value, String color) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(10));
        statBox.getStyleClass().add("stat-item");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        statBox.getChildren().addAll(valueLabel, labelText);
        
        return statBox;
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
        
        VBox tableRows = new VBox(5);
        tableRows.setPadding(new Insets(10));
        
        // Generate mock data
        List<TrainerData> trainersList = generateMockTrainers();
        
        for (TrainerData trainer : trainersList) {
            tableRows.getChildren().add(createTableRow(trainer));
        }
        
        scrollPane.setContent(tableRows);
        
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }
    
    /**
     * Create table row
     */
    private HBox createTableRow(TrainerData trainer) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Trainer ID
        Label idLabel = new Label(trainer.trainerId);
        idLabel.setPrefWidth(80);
        
        // Name
        Label nameLabel = new Label(trainer.name);
        nameLabel.setPrefWidth(200);
        nameLabel.setStyle("-fx-font-weight: 500;");
        
        // Specialization
        Label specializationLabel = new Label(trainer.specialization);
        specializationLabel.setPrefWidth(200);
        
        // Status badge
        Label statusBadge = new Label(trainer.status);
        statusBadge.setPrefWidth(120);
        statusBadge.getStyleClass().add("badge");
        statusBadge.getStyleClass().add(trainer.status.equals("Active") ? "badge-success" : "badge-warning");
        
        // Certification
        Label certLabel = new Label(trainer.certification);
        certLabel.setPrefWidth(150);
        certLabel.setStyle("-fx-font-size: 12px;");
        
        // Sessions count
        Label sessionsLabel = new Label(String.valueOf(trainer.sessionsPerMonth));
        sessionsLabel.setPrefWidth(150);
        sessionsLabel.setStyle("-fx-font-weight: 500;");
        
        // Action buttons
        HBox actionBox = new HBox(8);
        actionBox.setPrefWidth(200);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        Button viewButton = new Button("View");
        viewButton.getStyleClass().add("btn-primary-small");
        viewButton.setOnAction(e -> showTrainerProfile(trainer));
        
        Button scheduleButton = new Button("Schedule");
        scheduleButton.getStyleClass().add("btn-secondary-small");
        scheduleButton.setOnAction(e -> manageSchedule(trainer));
        
        actionBox.getChildren().addAll(viewButton, scheduleButton);
        
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
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Missing Required Fields");
                    alert.setContentText("Please fill in all required fields (Name, Email, Specialization).");
                    alert.showAndWait();
                    return;
                }
                
                // Generate trainer ID
                String trainerId = "T" + (1000 + new Random().nextInt(9000));
                
                System.out.println("\n=== Trainer Registered ===");
                System.out.println("Trainer ID: " + trainerId);
                System.out.println("Name: " + firstName + " " + lastName);
                System.out.println("Email: " + email);
                System.out.println("Phone: " + phoneField.getText());
                System.out.println("Specialization: " + specialization);
                System.out.println("Certification: " + certificationField.getText());
                System.out.println("Hire Date: " + hireDatePicker.getValue());
                System.out.println("========================\n");
                
                // Show success
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Trainer Registered Successfully");
                success.setContentText("Trainer ID: " + trainerId + "\nName: " + firstName + " " + lastName);
                success.showAndWait();
                
                // Refresh list (in real implementation)
            }
        });
    }
    
    /**
     * Show trainer profile
     */
    private void showTrainerProfile(TrainerData trainer) {
        selectedTrainer = trainer;
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Trainer Profile - " + trainer.name);
        dialog.setHeaderText(trainer.name + " (" + trainer.trainerId + ")");
        
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
        
        addProfileRow(personalGrid, 0, "Trainer ID:", trainer.trainerId);
        addProfileRow(personalGrid, 1, "Name:", trainer.name);
        addProfileRow(personalGrid, 2, "Email:", trainer.email);
        addProfileRow(personalGrid, 3, "Phone:", trainer.phone);
        addProfileRow(personalGrid, 4, "Specialization:", trainer.specialization);
        addProfileRow(personalGrid, 5, "Certification:", trainer.certification);
        
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
        
        addProfileRow(professionalGrid, 0, "Status:", trainer.status);
        addProfileRow(professionalGrid, 1, "Hire Date:", trainer.hireDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addProfileRow(professionalGrid, 2, "Sessions/Month:", String.valueOf(trainer.sessionsPerMonth));
        addProfileRow(professionalGrid, 3, "Active Clients:", String.valueOf(trainer.activeClients));
        addProfileRow(professionalGrid, 4, "Total Sessions:", String.valueOf(trainer.totalSessions));
        
        professionalInfo.getChildren().addAll(professionalTitle, professionalGrid);
        
        content.getChildren().addAll(personalInfo, professionalInfo);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }
    
    /**
     * Manage trainer schedule
     */
    private void manageSchedule(TrainerData trainer) {
        System.out.println("Managing schedule for: " + trainer.name);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Manage Schedule");
        alert.setHeaderText("Schedule Management for " + trainer.name);
        alert.setContentText("Current availability:\n" +
                             "Monday-Friday: 6:00 AM - 2:00 PM\n" +
                             "Saturday: 8:00 AM - 12:00 PM\n\n" +
                             "Schedule management interface will be implemented here.");
        alert.showAndWait();
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
    
    // ==================== DATA GENERATION ====================
    
    /**
     * Generate mock trainer data
     */
    private List<TrainerData> generateMockTrainers() {
        List<TrainerData> trainers = new ArrayList<>();
        
        String[][] trainerInfo = {
            {"Mike Johnson", "Personal Training", "ACE-CPT", "Active", "2022-01-15"},
            {"Sarah Williams", "Yoga", "RYT-200", "Active", "2021-06-20"},
            {"David Chen", "CrossFit", "CF-L2", "Active", "2020-03-10"},
            {"Emily Rodriguez", "Pilates", "PMA-CPT", "Active", "2023-02-01"},
            {"James Anderson", "Strength Training", "NSCA-CSCS", "Active", "2019-08-15"},
            {"Lisa Martinez", "Cardio", "ACSM-CPT", "On Leave", "2021-11-05"},
            {"Robert Taylor", "Boxing", "USA Boxing", "Active", "2022-07-12"},
            {"Jessica Lee", "Swimming", "WSI", "Active", "2020-09-18"}
        };
        
        Random random = new Random();
        
        for (int i = 0; i < trainerInfo.length; i++) {
            TrainerData trainer = new TrainerData();
            trainer.trainerId = "T" + (1001 + i);
            trainer.name = trainerInfo[i][0];
            trainer.email = trainerInfo[i][0].toLowerCase().replace(" ", ".") + "@block20gym.com";
            trainer.phone = "(555) " + (100 + random.nextInt(900)) + "-" + (1000 + random.nextInt(9000));
            trainer.specialization = trainerInfo[i][1];
            trainer.certification = trainerInfo[i][2];
            trainer.status = trainerInfo[i][3];
            trainer.hireDate = LocalDate.parse(trainerInfo[i][4]);
            trainer.sessionsPerMonth = 15 + random.nextInt(25);
            trainer.activeClients = 8 + random.nextInt(17);
            trainer.totalSessions = 100 + random.nextInt(400);
            
            trainers.add(trainer);
        }
        
        return trainers;
    }
    
    // ==================== DATA CLASS ====================
    
    /**
     * Trainer data class
     */
    private static class TrainerData {
        String trainerId;
        String name;
        String email;
        String phone;
        String specialization;
        String certification;
        String status;
        LocalDate hireDate;
        int sessionsPerMonth;
        int activeClients;
        int totalSessions;
    }
}
