/*
 * Block20 Gym Management System
 * Equipment Inventory Controller - Manage Gym Equipment
 */
package com.block20.controllers.equipment;

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
 * Controller for equipment inventory management:
 * 1. View All Equipment
 * 2. Add New Equipment
 * 3. View Equipment Details
 * 4. Track Equipment Status
 */
public class EquipmentInventoryController extends ScrollPane {
    
    private VBox contentContainer;
    private Consumer<String> navigationHandler;
    private VBox tableRows;
    private List<EquipmentData> equipmentList;
    
    // Filter state
    private String currentFilter = "All";
    
    /**
     * Constructor
     */
    public EquipmentInventoryController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        this.equipmentList = generateMockEquipment();
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
        
        // Action bar
        HBox actionBar = createActionBar();
        
        // Stats bar
        HBox statsBar = createStatsBar();
        
        // Equipment table
        VBox tableSection = createEquipmentTable();
        
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
        
        Label titleLabel = new Label("Equipment Inventory");
        titleLabel.getStyleClass().add("page-title");
        
        Label subtitleLabel = new Label("Manage gym equipment and assets");
        subtitleLabel.getStyleClass().add("page-subtitle");
        
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    /**
     * Create action bar
     */
    private HBox createActionBar() {
        HBox actionBar = new HBox(15);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search equipment by name, ID, or category...");
        searchField.setPrefWidth(500);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Search: " + newVal);
            // In real implementation, this would filter the table
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Add equipment button
        Button addButton = new Button("+ Add Equipment");
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> showAddEquipmentDialog());
        
        actionBar.getChildren().addAll(searchField, spacer, addButton);
        
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
        
        long totalCount = equipmentList.size();
        long operationalCount = equipmentList.stream().filter(e -> e.status.equals("Operational")).count();
        long maintenanceCount = equipmentList.stream().filter(e -> e.status.equals("Under Maintenance")).count();
        long outOfServiceCount = equipmentList.stream().filter(e -> e.status.equals("Out of Service")).count();
        
        statsBar.getChildren().addAll(
            createStatItem("Total Equipment", String.valueOf(totalCount), "#3B82F6"),
            createStatItem("Operational", String.valueOf(operationalCount), "#10B981"),
            createStatItem("Under Maintenance", String.valueOf(maintenanceCount), "#F59E0B"),
            createStatItem("Out of Service", String.valueOf(outOfServiceCount), "#EF4444")
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
     * Create equipment table
     */
    private VBox createEquipmentTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");
        
        // Table header
        HBox tableHeader = new HBox();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(10));
        
        Label idCol = new Label("Equipment ID");
        idCol.setPrefWidth(120);
        idCol.getStyleClass().add("table-header-cell");
        
        Label nameCol = new Label("Name");
        nameCol.setPrefWidth(200);
        nameCol.getStyleClass().add("table-header-cell");
        
        Label categoryCol = new Label("Category");
        categoryCol.setPrefWidth(120);
        categoryCol.getStyleClass().add("table-header-cell");
        
        Label zoneCol = new Label("Zone");
        zoneCol.setPrefWidth(150);
        zoneCol.getStyleClass().add("table-header-cell");
        
        Label statusCol = new Label("Status");
        statusCol.setPrefWidth(150);
        statusCol.getStyleClass().add("table-header-cell");
        
        Label purchaseDateCol = new Label("Purchase Date");
        purchaseDateCol.setPrefWidth(130);
        purchaseDateCol.getStyleClass().add("table-header-cell");
        
        Label actionCol = new Label("Actions");
        actionCol.setPrefWidth(200);
        actionCol.getStyleClass().add("table-header-cell");
        
        tableHeader.getChildren().addAll(idCol, nameCol, categoryCol, zoneCol, statusCol, purchaseDateCol, actionCol);
        
        // Table rows
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("table-scroll");
        
        tableRows = new VBox(5);
        tableRows.setPadding(new Insets(10));
        
        for (EquipmentData equipment : equipmentList) {
            tableRows.getChildren().add(createTableRow(equipment));
        }
        
        scrollPane.setContent(tableRows);
        
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }
    
    /**
     * Create table row
     */
    private HBox createTableRow(EquipmentData equipment) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Equipment ID
        Label idLabel = new Label(equipment.equipmentId);
        idLabel.setPrefWidth(120);
        idLabel.setStyle("-fx-font-weight: 500;");
        
        // Name
        Label nameLabel = new Label(equipment.name);
        nameLabel.setPrefWidth(200);
        
        // Category
        Label categoryLabel = new Label(equipment.category);
        categoryLabel.setPrefWidth(120);
        
        // Zone
        Label zoneLabel = new Label(equipment.zone);
        zoneLabel.setPrefWidth(150);
        
        // Status badge
        Label statusBadge = new Label(equipment.status);
        statusBadge.setPrefWidth(150);
        statusBadge.getStyleClass().add("badge");
        switch (equipment.status) {
            case "Operational":
                statusBadge.getStyleClass().add("badge-success");
                break;
            case "Needs Maintenance":
                statusBadge.getStyleClass().add("badge-warning");
                break;
            case "Under Maintenance":
                statusBadge.getStyleClass().add("badge-info");
                break;
            case "Out of Service":
                statusBadge.getStyleClass().add("badge-error");
                break;
        }
        
        // Purchase Date
        Label purchaseLabel = new Label(equipment.purchaseDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        purchaseLabel.setPrefWidth(130);
        purchaseLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        
        // Action buttons
        HBox actionBox = new HBox(8);
        actionBox.setPrefWidth(200);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        Button viewButton = new Button("View");
        viewButton.getStyleClass().add("btn-primary-small");
        viewButton.setOnAction(e -> viewEquipment(equipment));
        
        Button maintainButton = new Button("Schedule");
        maintainButton.getStyleClass().add("btn-secondary-small");
        maintainButton.setOnAction(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept("equipment-maintenance");
            }
        });
        
        actionBox.getChildren().addAll(viewButton, maintainButton);
        
        row.getChildren().addAll(idLabel, nameLabel, categoryLabel, zoneLabel, statusBadge, purchaseLabel, actionBox);
        
        return row;
    }
    
    // ==================== DIALOG VIEWS ====================
    
    /**
     * Show add equipment dialog
     */
    private void showAddEquipmentDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Equipment");
        dialog.setHeaderText("Register new gym equipment");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Equipment name
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Treadmill Pro 3000");
        nameField.setPrefWidth(300);
        
        // Category
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Cardio", "Strength", "Free Weights", "Functional Training", "Yoga", "CrossFit");
        categoryBox.setPromptText("Select category");
        categoryBox.setPrefWidth(300);
        
        // Zone
        ComboBox<String> zoneBox = new ComboBox<>();
        zoneBox.getItems().addAll("Cardio Zone", "Strength Zone", "Free Weights Area", "Functional Training", "Yoga Studio", "CrossFit Box");
        zoneBox.setPromptText("Select zone");
        zoneBox.setPrefWidth(300);
        
        // Manufacturer
        TextField manufacturerField = new TextField();
        manufacturerField.setPromptText("e.g., Life Fitness");
        manufacturerField.setPrefWidth(300);
        
        // Model
        TextField modelField = new TextField();
        modelField.setPromptText("e.g., T3-2024");
        modelField.setPrefWidth(300);
        
        // Serial Number
        TextField serialField = new TextField();
        serialField.setPromptText("e.g., SN123456789");
        serialField.setPrefWidth(300);
        
        // Purchase Date
        DatePicker purchaseDatePicker = new DatePicker(LocalDate.now());
        purchaseDatePicker.setPrefWidth(300);
        
        // Warranty Expiry
        DatePicker warrantyPicker = new DatePicker(LocalDate.now().plusYears(1));
        warrantyPicker.setPrefWidth(300);
        
        // Condition/Status
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Operational", "Needs Maintenance", "Under Maintenance", "Out of Service");
        statusBox.setValue("Operational");
        statusBox.setPrefWidth(300);
        
        // Notes
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Additional notes or specifications...");
        notesArea.setPrefRowCount(3);
        notesArea.setPrefWidth(300);
        
        // Add to grid
        grid.add(new Label("Equipment Name:*"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:*"), 0, 1);
        grid.add(categoryBox, 1, 1);
        grid.add(new Label("Zone:*"), 0, 2);
        grid.add(zoneBox, 1, 2);
        grid.add(new Label("Manufacturer:"), 0, 3);
        grid.add(manufacturerField, 1, 3);
        grid.add(new Label("Model:"), 0, 4);
        grid.add(modelField, 1, 4);
        grid.add(new Label("Serial Number:"), 0, 5);
        grid.add(serialField, 1, 5);
        grid.add(new Label("Purchase Date:*"), 0, 6);
        grid.add(purchaseDatePicker, 1, 6);
        grid.add(new Label("Warranty Expiry:"), 0, 7);
        grid.add(warrantyPicker, 1, 7);
        grid.add(new Label("Status:"), 0, 8);
        grid.add(statusBox, 1, 8);
        grid.add(new Label("Notes:"), 0, 9);
        grid.add(notesArea, 1, 9);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate
                if (nameField.getText().trim().isEmpty() || categoryBox.getValue() == null || zoneBox.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Missing Required Fields");
                    alert.setContentText("Please fill in all required fields marked with *");
                    alert.showAndWait();
                    return;
                }
                
                // Generate equipment ID
                String equipmentId = "EQ" + (1001 + equipmentList.size());
                
                System.out.println("\n=== New Equipment Added ===");
                System.out.println("Equipment ID: " + equipmentId);
                System.out.println("Name: " + nameField.getText());
                System.out.println("Category: " + categoryBox.getValue());
                System.out.println("Zone: " + zoneBox.getValue());
                System.out.println("Manufacturer: " + manufacturerField.getText());
                System.out.println("Model: " + modelField.getText());
                System.out.println("Serial Number: " + serialField.getText());
                System.out.println("Purchase Date: " + purchaseDatePicker.getValue());
                System.out.println("Warranty Expiry: " + warrantyPicker.getValue());
                System.out.println("Status: " + statusBox.getValue());
                System.out.println("Notes: " + notesArea.getText());
                System.out.println("===========================\n");
                
                // Add to list and refresh table
                EquipmentData newEquipment = new EquipmentData();
                newEquipment.equipmentId = equipmentId;
                newEquipment.name = nameField.getText();
                newEquipment.category = categoryBox.getValue();
                newEquipment.zone = zoneBox.getValue();
                newEquipment.manufacturer = manufacturerField.getText();
                newEquipment.model = modelField.getText();
                newEquipment.serialNumber = serialField.getText();
                newEquipment.purchaseDate = purchaseDatePicker.getValue();
                newEquipment.warrantyExpiry = warrantyPicker.getValue();
                newEquipment.status = statusBox.getValue();
                newEquipment.notes = notesArea.getText();
                
                equipmentList.add(newEquipment);
                tableRows.getChildren().add(createTableRow(newEquipment));
                
                // Show success
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Equipment Added Successfully");
                success.setContentText("Equipment ID: " + equipmentId + "\n" +
                                      "Name: " + nameField.getText() + "\n" +
                                      "Category: " + categoryBox.getValue());
                success.showAndWait();
            }
        });
    }
    
    /**
     * View equipment details
     */
    private void viewEquipment(EquipmentData equipment) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Equipment Details");
        dialog.setHeaderText(equipment.name + " (" + equipment.equipmentId + ")");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        // Basic Information
        VBox basicInfo = new VBox(10);
        basicInfo.getStyleClass().add("info-card");
        basicInfo.setPadding(new Insets(15));
        
        Label basicTitle = new Label("Basic Information");
        basicTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(20);
        basicGrid.setVgap(8);
        
        addDetailRow(basicGrid, 0, "Equipment ID:", equipment.equipmentId);
        addDetailRow(basicGrid, 1, "Name:", equipment.name);
        addDetailRow(basicGrid, 2, "Category:", equipment.category);
        addDetailRow(basicGrid, 3, "Zone:", equipment.zone);
        addDetailRow(basicGrid, 4, "Status:", equipment.status);
        
        basicInfo.getChildren().addAll(basicTitle, basicGrid);
        
        // Technical Details
        VBox techInfo = new VBox(10);
        techInfo.getStyleClass().add("info-card");
        techInfo.setPadding(new Insets(15));
        
        Label techTitle = new Label("Technical Details");
        techTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane techGrid = new GridPane();
        techGrid.setHgap(20);
        techGrid.setVgap(8);
        
        addDetailRow(techGrid, 0, "Manufacturer:", equipment.manufacturer);
        addDetailRow(techGrid, 1, "Model:", equipment.model);
        addDetailRow(techGrid, 2, "Serial Number:", equipment.serialNumber);
        addDetailRow(techGrid, 3, "Purchase Date:", equipment.purchaseDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addDetailRow(techGrid, 4, "Warranty Expiry:", equipment.warrantyExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        techInfo.getChildren().addAll(techTitle, techGrid);
        
        // Notes
        if (equipment.notes != null && !equipment.notes.isEmpty()) {
            VBox notesBox = new VBox(10);
            notesBox.getStyleClass().add("info-card");
            notesBox.setPadding(new Insets(15));
            
            Label notesTitle = new Label("Notes");
            notesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            Label notesText = new Label(equipment.notes);
            notesText.setWrapText(true);
            
            notesBox.getChildren().addAll(notesTitle, notesText);
            content.getChildren().addAll(basicInfo, techInfo, notesBox);
        } else {
            content.getChildren().addAll(basicInfo, techInfo);
        }
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }
    
    /**
     * Add detail row
     */
    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelText = new Label(label);
        labelText.getStyleClass().add("form-label");
        labelText.setStyle("-fx-font-weight: 500; -fx-min-width: 140px;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 14px;");
        
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }
    
    // ==================== DATA GENERATION ====================
    
    /**
     * Generate mock equipment data
     */
    private List<EquipmentData> generateMockEquipment() {
        List<EquipmentData> equipment = new ArrayList<>();
        Random random = new Random();
        
        // Cardio equipment
        equipment.add(createEquipment("EQ1001", "Treadmill Pro 3000", "Cardio", "Cardio Zone", 
            "Life Fitness", "T3-2024", "SN-TM-001", LocalDate.of(2024, 1, 15), "Operational", 
            "High-end commercial treadmill with touch screen"));
        
        equipment.add(createEquipment("EQ1002", "Elliptical Trainer", "Cardio", "Cardio Zone", 
            "Precor", "EFX-885", "SN-EL-002", LocalDate.of(2023, 6, 20), "Operational", 
            "Low-impact cardio machine"));
        
        equipment.add(createEquipment("EQ1003", "Rowing Machine", "Cardio", "Cardio Zone", 
            "Concept2", "Model D", "SN-RW-003", LocalDate.of(2024, 3, 10), "Needs Maintenance", 
            "Chain needs lubrication"));
        
        // Strength equipment
        equipment.add(createEquipment("EQ1004", "Leg Press Machine", "Strength", "Strength Zone", 
            "Hammer Strength", "LP-450", "SN-LP-004", LocalDate.of(2023, 9, 5), "Operational", 
            "Plate-loaded leg press"));
        
        equipment.add(createEquipment("EQ1005", "Cable Crossover", "Strength", "Strength Zone", 
            "Life Fitness", "CC-200", "SN-CC-005", LocalDate.of(2024, 2, 18), "Operational", 
            "Dual adjustable pulleys"));
        
        equipment.add(createEquipment("EQ1006", "Smith Machine", "Strength", "Strength Zone", 
            "Body-Solid", "GS348Q", "SN-SM-006", LocalDate.of(2023, 11, 12), "Under Maintenance", 
            "Rails being serviced"));
        
        // Free Weights
        equipment.add(createEquipment("EQ1007", "Dumbbell Set (5-50kg)", "Free Weights", "Free Weights Area", 
            "Rogue Fitness", "RDB-50", "SN-DB-007", LocalDate.of(2024, 1, 8), "Operational", 
            "Complete rubber hex dumbbell set"));
        
        equipment.add(createEquipment("EQ1008", "Olympic Barbell", "Free Weights", "Free Weights Area", 
            "Eleiko", "IWF-20KG", "SN-BB-008", LocalDate.of(2024, 4, 22), "Operational", 
            "Competition grade barbell"));
        
        // Functional Training
        equipment.add(createEquipment("EQ1009", "TRX Suspension System", "Functional Training", "Functional Training", 
            "TRX", "PRO4", "SN-TR-009", LocalDate.of(2023, 8, 30), "Operational", 
            "Professional suspension trainer"));
        
        equipment.add(createEquipment("EQ1010", "Kettlebell Set", "Functional Training", "Functional Training", 
            "Rogue Fitness", "RKB-SET", "SN-KB-010", LocalDate.of(2024, 5, 14), "Out of Service", 
            "Damaged handle on 24kg kettlebell"));
        
        return equipment;
    }
    
    /**
     * Create equipment data object
     */
    private EquipmentData createEquipment(String id, String name, String category, String zone,
                                         String manufacturer, String model, String serial,
                                         LocalDate purchaseDate, String status, String notes) {
        EquipmentData eq = new EquipmentData();
        eq.equipmentId = id;
        eq.name = name;
        eq.category = category;
        eq.zone = zone;
        eq.manufacturer = manufacturer;
        eq.model = model;
        eq.serialNumber = serial;
        eq.purchaseDate = purchaseDate;
        eq.warrantyExpiry = purchaseDate.plusYears(1);
        eq.status = status;
        eq.notes = notes;
        return eq;
    }
    
    // ==================== DATA CLASS ====================
    
    /**
     * Equipment data class
     */
    private static class EquipmentData {
        String equipmentId;
        String name;
        String category;
        String zone;
        String manufacturer;
        String model;
        String serialNumber;
        LocalDate purchaseDate;
        LocalDate warrantyExpiry;
        String status;
        String notes;
    }
}
