/*
 * Block20 Gym Management System
 * Maintenance Schedule Controller - Schedule and Track Equipment Maintenance
 */
package com.block20.controllers.equipment;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Controller for maintenance schedule management:
 * 1. View Maintenance Schedule
 * 2. Schedule New Maintenance
 * 3. Complete Maintenance Tasks
 * 4. Track Maintenance History
 */
public class MaintenanceScheduleController extends ScrollPane {
    
    private static final double[] COLUMN_PERCENTAGES = {0.08, 0.18, 0.14, 0.10, 0.14, 0.16, 0.10, 0.10};

    private VBox contentContainer;
    private Consumer<String> navigationHandler;
    private VBox tableRows;
    private List<MaintenanceData> maintenanceList;
    private FlowPane statsBarView;
    private ToggleButton upcomingTab;
    private ToggleButton overdueTab;
    private ToggleButton completedTab;
    
    // Filter state
    private String currentFilter = "Upcoming";
    
    /**
     * Constructor
     */
    public MaintenanceScheduleController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        this.maintenanceList = generateMockMaintenance();
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
        
        // Filter tabs
        HBox filterTabs = createFilterTabs();
        updateFilterTabLabels();
        
        // Stats bar
        statsBarView = createStatsBar();
        
        // Maintenance table
        VBox tableSection = createMaintenanceTable();
        
        contentContainer.getChildren().addAll(header, actionBar, filterTabs, statsBarView, tableSection);
        
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
        
        Label titleLabel = new Label("Maintenance Schedule");
        titleLabel.getStyleClass().add("page-title");
        
        Label subtitleLabel = new Label("Schedule and track equipment maintenance tasks");
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
        
        // Date picker for quick navigation
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Jump to date");
        
        Label dateLabel = new Label("Jump to date:");
        dateLabel.getStyleClass().add("form-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Schedule maintenance button
        Button scheduleButton = new Button("+ Schedule Maintenance");
        scheduleButton.getStyleClass().add("btn-primary");
        scheduleButton.setOnAction(e -> showScheduleMaintenanceDialog());
        
        actionBar.getChildren().addAll(dateLabel, datePicker, spacer, scheduleButton);
        
        return actionBar;
    }
    
    /**
     * Create filter tabs
     */
    private HBox createFilterTabs() {
        HBox tabsBox = new HBox(0);
        tabsBox.setAlignment(Pos.CENTER_LEFT);
        tabsBox.getStyleClass().add("filter-tabs");
        
        ToggleGroup filterGroup = new ToggleGroup();
        
        long upcomingCount = maintenanceList.stream().filter(m -> m.status.equals("Scheduled")).count();
        long completedCount = maintenanceList.stream().filter(m -> m.status.equals("Completed")).count();
        long overdueCount = maintenanceList.stream().filter(m -> m.status.equals("Overdue")).count();
        
        upcomingTab = createFilterTab("Upcoming (" + upcomingCount + ")", filterGroup, true);
        upcomingTab.setOnAction(e -> applyFilter("Upcoming"));
        
        overdueTab = createFilterTab("Overdue (" + overdueCount + ")", filterGroup, false);
        overdueTab.setOnAction(e -> applyFilter("Overdue"));
        
        completedTab = createFilterTab("Completed (" + completedCount + ")", filterGroup, false);
        completedTab.setOnAction(e -> applyFilter("Completed"));
        
        tabsBox.getChildren().addAll(upcomingTab, overdueTab, completedTab);
        
        return tabsBox;
    }
    
    /**
     * Create filter tab
     */
    private ToggleButton createFilterTab(String text, ToggleGroup group, boolean selected) {
        ToggleButton tab = new ToggleButton(text);
        tab.setToggleGroup(group);
        tab.setSelected(selected);
        tab.getStyleClass().add("filter-tab");
        return tab;
    }

    private void updateFilterTabLabels() {
        if (upcomingTab == null || overdueTab == null || completedTab == null) {
            return;
        }
        long upcomingCount = maintenanceList.stream().filter(m -> "Scheduled".equals(m.status)).count();
        long overdueCount = maintenanceList.stream().filter(m -> "Overdue".equals(m.status)).count();
        long completedCount = maintenanceList.stream().filter(m -> "Completed".equals(m.status)).count();
        upcomingTab.setText("Upcoming (" + upcomingCount + ")");
        overdueTab.setText("Overdue (" + overdueCount + ")");
        completedTab.setText("Completed (" + completedCount + ")");
    }
    
    /**
     * Create stats bar
     */
    private FlowPane createStatsBar() {
        FlowPane statsBar = new FlowPane(16, 16);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(16));
        statsBar.getStyleClass().add("stats-bar");
        statsBar.setPrefWrapLength(900);
        statsBar.setMaxWidth(Double.MAX_VALUE);
        
        long todayCount = maintenanceList.stream()
            .filter(m -> LocalDate.now().equals(m.scheduledDate) && "Scheduled".equals(m.status))
            .count();
        
        long thisWeekCount = maintenanceList.stream()
            .filter(m -> !m.scheduledDate.isBefore(LocalDate.now()) &&
                        !m.scheduledDate.isAfter(LocalDate.now().plusDays(7)) &&
                        "Scheduled".equals(m.status))
            .count();
        
        long overdueCount = maintenanceList.stream().filter(m -> "Overdue".equals(m.status)).count();
        long totalScheduled = maintenanceList.stream().filter(m -> !"Completed".equals(m.status)).count();
        
        statsBar.getChildren().addAll(
            createStatItem("Today's Tasks", String.valueOf(todayCount), "#3B82F6"),
            createStatItem("This Week", String.valueOf(thisWeekCount), "#10B981"),
            createStatItem("Overdue", String.valueOf(overdueCount), "#EF4444"),
            createStatItem("Active Schedule", String.valueOf(totalScheduled), "#8B5CF6")
        );
        
        return statsBar;
    }
    
    /**
     * Create stat item
     */
    private VBox createStatItem(String label, String value, String color) {
        VBox statBox = new VBox(6);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(14));
        statBox.getStyleClass().add("stat-item");
        statBox.setMinWidth(180);
        statBox.setPrefWidth(220);
        statBox.setMaxWidth(Double.MAX_VALUE);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        statBox.getChildren().addAll(valueLabel, labelText);
        
        return statBox;
    }
    
    /**
     * Create maintenance table
     */
    private VBox createMaintenanceTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");
        
        GridPane tableHeader = new GridPane();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(12));
        tableHeader.setHgap(12);
        applyTableColumnConstraints(tableHeader);
        
        addHeaderCell(tableHeader, "Task ID", 0);
        addHeaderCell(tableHeader, "Equipment", 1);
        addHeaderCell(tableHeader, "Maintenance Type", 2);
        addHeaderCell(tableHeader, "Priority", 3);
        addHeaderCell(tableHeader, "Scheduled Date", 4);
        addHeaderCell(tableHeader, "Technician", 5);
        addHeaderCell(tableHeader, "Status", 6);
        addHeaderCell(tableHeader, "Actions", 7);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("table-scroll");
        
        tableRows = new VBox(5);
        tableRows.setPadding(new Insets(10));
        tableRows.setFillWidth(true);
        
        scrollPane.setContent(tableRows);
        refreshTableRows();
        
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }

    private void addHeaderCell(GridPane header, String text, int column) {
        Label label = new Label(text);
        label.getStyleClass().add("table-header-cell");
        header.add(label, column, 0);
        GridPane.setHgrow(label, Priority.ALWAYS);
    }

    private GridPane createTableRow(MaintenanceData maintenance) {
        GridPane row = new GridPane();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(12));
        row.setHgap(12);
        row.setAlignment(Pos.CENTER_LEFT);
        applyTableColumnConstraints(row);
        
        Label idLabel = new Label(maintenance.taskId);
        idLabel.setStyle("-fx-font-weight: 500;");
        row.add(idLabel, 0, 0);
        
        Label equipmentLabel = new Label(maintenance.equipmentName);
        row.add(equipmentLabel, 1, 0);
        
        Label typeLabel = new Label(maintenance.maintenanceType);
        row.add(typeLabel, 2, 0);
        
        Label priorityBadge = new Label(maintenance.priority);
        priorityBadge.getStyleClass().add("badge");
        switch (maintenance.priority) {
            case "Urgent" -> priorityBadge.getStyleClass().add("badge-error");
            case "Normal" -> priorityBadge.getStyleClass().add("badge-warning");
            default -> priorityBadge.getStyleClass().add("badge-info");
        }
        row.add(priorityBadge, 3, 0);
        
        Label dateLabel = new Label(maintenance.scheduledDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        row.add(dateLabel, 4, 0);
        
        Label technicianLabel = new Label(maintenance.technician);
        row.add(technicianLabel, 5, 0);
        
        Label statusBadge = new Label(maintenance.status);
        statusBadge.getStyleClass().add("badge");
        switch (maintenance.status) {
            case "Scheduled" -> statusBadge.getStyleClass().add("badge-info");
            case "Completed" -> statusBadge.getStyleClass().add("badge-success");
            case "Overdue" -> statusBadge.getStyleClass().add("badge-error");
            default -> statusBadge.getStyleClass().add("badge-warning");
        }
        row.add(statusBadge, 6, 0);
        
        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setFillHeight(false);
        
        if ("Scheduled".equals(maintenance.status) || "Overdue".equals(maintenance.status)) {
            Button completeButton = new Button("Complete");
            completeButton.getStyleClass().add("btn-primary-small");
            completeButton.setOnAction(e -> completeMaintenance(maintenance));
            actionBox.getChildren().add(completeButton);
        } else {
            Button viewButton = new Button("View");
            viewButton.getStyleClass().add("btn-secondary-small");
            viewButton.setOnAction(e -> viewMaintenance(maintenance));
            actionBox.getChildren().add(viewButton);
        }
        row.add(actionBox, 7, 0);
        
        return row;
    }

    private void applyTableColumnConstraints(GridPane grid) {
        if (!grid.getColumnConstraints().isEmpty()) {
            return;
        }
        for (double percent : COLUMN_PERCENTAGES) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(percent * 100);
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }
    }

    private void refreshTableRows() {
        if (tableRows == null) {
            return;
        }
        tableRows.getChildren().clear();
        maintenanceList.stream()
            .filter(this::matchesCurrentFilter)
            .sorted(Comparator
                .comparing((MaintenanceData m) -> m.scheduledDate)
                .thenComparing(m -> m.taskId))
            .map(this::createTableRow)
            .forEach(tableRows.getChildren()::add);
    }

    private boolean matchesCurrentFilter(MaintenanceData maintenance) {
        return switch (currentFilter) {
            case "Overdue" -> "Overdue".equals(maintenance.status);
            case "Completed" -> "Completed".equals(maintenance.status);
            case "Upcoming" -> "Scheduled".equals(maintenance.status);
            default -> true;
        };
    }

    private void refreshStatsBar() {
        if (contentContainer == null || statsBarView == null) {
            return;
        }
        FlowPane updatedBar = createStatsBar();
        int index = contentContainer.getChildren().indexOf(statsBarView);
        if (index >= 0) {
            contentContainer.getChildren().set(index, updatedBar);
            statsBarView = updatedBar;
        }
    }
    
    /**
     * Apply filter
     */
    private void applyFilter(String filter) {
        currentFilter = filter;
        refreshTableRows();
    }
    
    // ==================== DIALOG VIEWS ====================
    
    /**
     * Show schedule maintenance dialog
     */
    private void showScheduleMaintenanceDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Schedule Maintenance");
        dialog.setHeaderText("Schedule equipment maintenance task");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Equipment selection
        ComboBox<String> equipmentBox = new ComboBox<>();
        equipmentBox.getItems().addAll(
            "EQ1001 - Treadmill Pro 3000",
            "EQ1002 - Elliptical Trainer",
            "EQ1003 - Rowing Machine",
            "EQ1004 - Leg Press Machine",
            "EQ1005 - Cable Crossover",
            "EQ1006 - Smith Machine",
            "EQ1007 - Dumbbell Set",
            "EQ1008 - Olympic Barbell",
            "EQ1009 - TRX Suspension System",
            "EQ1010 - Kettlebell Set"
        );
        equipmentBox.setPromptText("Select equipment");
        equipmentBox.setPrefWidth(300);
        
        // Maintenance type
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(
            "Routine Inspection",
            "Repair",
            "Deep Clean",
            "Calibration",
            "Warranty Service",
            "Parts Replacement"
        );
        typeBox.setPromptText("Select maintenance type");
        typeBox.setPrefWidth(300);
        
        // Priority
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Urgent", "Normal", "Routine");
        priorityBox.setValue("Normal");
        priorityBox.setPrefWidth(300);
        
        // Scheduled date
        DatePicker scheduledDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        scheduledDatePicker.setPrefWidth(300);
        
        // Technician
        TextField technicianField = new TextField();
        technicianField.setPromptText("e.g., John Maintenance");
        technicianField.setPrefWidth(300);
        
        // Estimated cost
        TextField costField = new TextField();
        costField.setPromptText("e.g., 150.00");
        costField.setPrefWidth(300);
        
        // Description/Notes
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Describe the maintenance work required...");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setPrefWidth(300);
        
        // Add to grid
        grid.add(new Label("Equipment:*"), 0, 0);
        grid.add(equipmentBox, 1, 0);
        grid.add(new Label("Maintenance Type:*"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityBox, 1, 2);
        grid.add(new Label("Scheduled Date:*"), 0, 3);
        grid.add(scheduledDatePicker, 1, 3);
        grid.add(new Label("Technician:*"), 0, 4);
        grid.add(technicianField, 1, 4);
        grid.add(new Label("Estimated Cost ($):"), 0, 5);
        grid.add(costField, 1, 5);
        grid.add(new Label("Description:*"), 0, 6);
        grid.add(descriptionArea, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate
                if (equipmentBox.getValue() == null || typeBox.getValue() == null || 
                    technicianField.getText().trim().isEmpty() || descriptionArea.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Missing Required Fields");
                    alert.setContentText("Please fill in all required fields marked with *");
                    alert.showAndWait();
                    return;
                }
                
                // Generate task ID
                String taskId = "MT" + System.currentTimeMillis();
                
                System.out.println("\n=== Maintenance Task Scheduled ===");
                System.out.println("Task ID: " + taskId);
                System.out.println("Equipment: " + equipmentBox.getValue());
                System.out.println("Maintenance Type: " + typeBox.getValue());
                System.out.println("Priority: " + priorityBox.getValue());
                System.out.println("Scheduled Date: " + scheduledDatePicker.getValue());
                System.out.println("Technician: " + technicianField.getText());
                System.out.println("Estimated Cost: $" + costField.getText());
                System.out.println("Description: " + descriptionArea.getText());
                System.out.println("==================================\n");
                
                // Add to list and refresh table
                MaintenanceData newMaintenance = new MaintenanceData();
                newMaintenance.taskId = taskId;
                newMaintenance.equipmentName = equipmentBox.getValue().split(" - ")[1];
                newMaintenance.maintenanceType = typeBox.getValue();
                newMaintenance.priority = priorityBox.getValue();
                newMaintenance.scheduledDate = scheduledDatePicker.getValue();
                newMaintenance.technician = technicianField.getText();
                newMaintenance.estimatedCost = costField.getText().isEmpty() ? "0.00" : costField.getText();
                newMaintenance.description = descriptionArea.getText();
                newMaintenance.status = "Scheduled";
                
                maintenanceList.add(newMaintenance);
                refreshTableRows();
                refreshStatsBar();
                updateFilterTabLabels();
                
                // Show success
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Maintenance Task Scheduled");
                success.setContentText("Task ID: " + taskId + "\n" +
                                      "Equipment: " + equipmentBox.getValue() + "\n" +
                                      "Scheduled for: " + scheduledDatePicker.getValue());
                success.showAndWait();
            }
        });
    }
    
    /**
     * Complete maintenance task
     */
    private void completeMaintenance(MaintenanceData maintenance) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Complete Maintenance");
        dialog.setHeaderText("Mark maintenance task as completed");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Completion date
        DatePicker completionDatePicker = new DatePicker(LocalDate.now());
        completionDatePicker.setPrefWidth(300);
        
        // Actual cost
        TextField actualCostField = new TextField();
        actualCostField.setPromptText("e.g., 175.00");
        actualCostField.setText(maintenance.estimatedCost);
        actualCostField.setPrefWidth(300);
        
        // Work performed
        TextArea workArea = new TextArea();
        workArea.setPromptText("Describe work performed...");
        workArea.setPrefRowCount(4);
        workArea.setPrefWidth(300);
        
        // Parts used
        TextArea partsArea = new TextArea();
        partsArea.setPromptText("List parts used (if any)...");
        partsArea.setPrefRowCount(3);
        partsArea.setPrefWidth(300);
        
        grid.add(new Label("Completion Date:*"), 0, 0);
        grid.add(completionDatePicker, 1, 0);
        grid.add(new Label("Actual Cost ($):*"), 0, 1);
        grid.add(actualCostField, 1, 1);
        grid.add(new Label("Work Performed:*"), 0, 2);
        grid.add(workArea, 1, 2);
        grid.add(new Label("Parts Used:"), 0, 3);
        grid.add(partsArea, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (actualCostField.getText().trim().isEmpty() || workArea.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Missing Required Fields");
                    alert.setContentText("Please fill in all required fields marked with *");
                    alert.showAndWait();
                    return;
                }
                
                System.out.println("\n=== Maintenance Task Completed ===");
                System.out.println("Task ID: " + maintenance.taskId);
                System.out.println("Equipment: " + maintenance.equipmentName);
                System.out.println("Completion Date: " + completionDatePicker.getValue());
                System.out.println("Actual Cost: $" + actualCostField.getText());
                System.out.println("Work Performed: " + workArea.getText());
                System.out.println("Parts Used: " + partsArea.getText());
                System.out.println("==================================\n");
                
                // Update status
                maintenance.status = "Completed";
                maintenance.completionDate = completionDatePicker.getValue();
                maintenance.actualCost = actualCostField.getText();
                maintenance.workPerformed = workArea.getText();
                maintenance.partsUsed = partsArea.getText();
                
                // Refresh table (in real implementation)
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Maintenance Task Completed");
                success.setContentText("Task ID: " + maintenance.taskId + " has been marked as completed.");
                success.showAndWait();

                refreshTableRows();
                refreshStatsBar();
                updateFilterTabLabels();
            }
        });
    }
    
    /**
     * View maintenance details
     */
    private void viewMaintenance(MaintenanceData maintenance) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Maintenance Details");
        dialog.setHeaderText(maintenance.taskId + " - " + maintenance.equipmentName);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        // Task Information
        VBox taskInfo = new VBox(10);
        taskInfo.getStyleClass().add("info-card");
        taskInfo.setPadding(new Insets(15));
        
        Label taskTitle = new Label("Task Information");
        taskTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane taskGrid = new GridPane();
        taskGrid.setHgap(20);
        taskGrid.setVgap(8);
        
        addDetailRow(taskGrid, 0, "Task ID:", maintenance.taskId);
        addDetailRow(taskGrid, 1, "Equipment:", maintenance.equipmentName);
        addDetailRow(taskGrid, 2, "Type:", maintenance.maintenanceType);
        addDetailRow(taskGrid, 3, "Priority:", maintenance.priority);
        addDetailRow(taskGrid, 4, "Status:", maintenance.status);
        
        taskInfo.getChildren().addAll(taskTitle, taskGrid);
        
        // Schedule Information
        VBox scheduleInfo = new VBox(10);
        scheduleInfo.getStyleClass().add("info-card");
        scheduleInfo.setPadding(new Insets(15));
        
        Label scheduleTitle = new Label("Schedule & Cost");
        scheduleTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane scheduleGrid = new GridPane();
        scheduleGrid.setHgap(20);
        scheduleGrid.setVgap(8);
        
        addDetailRow(scheduleGrid, 0, "Scheduled Date:", maintenance.scheduledDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addDetailRow(scheduleGrid, 1, "Technician:", maintenance.technician);
        addDetailRow(scheduleGrid, 2, "Estimated Cost:", "$" + maintenance.estimatedCost);
        
        if (maintenance.completionDate != null) {
            addDetailRow(scheduleGrid, 3, "Completion Date:", maintenance.completionDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            addDetailRow(scheduleGrid, 4, "Actual Cost:", "$" + maintenance.actualCost);
        }
        
        scheduleInfo.getChildren().addAll(scheduleTitle, scheduleGrid);
        
        // Description
        VBox descBox = new VBox(10);
        descBox.getStyleClass().add("info-card");
        descBox.setPadding(new Insets(15));
        
        Label descTitle = new Label("Description");
        descTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label descText = new Label(maintenance.description);
        descText.setWrapText(true);
        
        descBox.getChildren().addAll(descTitle, descText);
        
        content.getChildren().addAll(taskInfo, scheduleInfo, descBox);
        
        // Work performed (if completed)
        if (maintenance.workPerformed != null && !maintenance.workPerformed.isEmpty()) {
            VBox workBox = new VBox(10);
            workBox.getStyleClass().add("info-card");
            workBox.setPadding(new Insets(15));
            
            Label workTitle = new Label("Work Performed");
            workTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            Label workText = new Label(maintenance.workPerformed);
            workText.setWrapText(true);
            
            workBox.getChildren().addAll(workTitle, workText);
            content.getChildren().add(workBox);
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
     * Generate mock maintenance data
     */
    private List<MaintenanceData> generateMockMaintenance() {
        List<MaintenanceData> maintenance = new ArrayList<>();
        Random random = new Random();
        
        // Today's tasks
        maintenance.add(createMaintenance("MT1001", "Treadmill Pro 3000", "Routine Inspection", "Normal",
            LocalDate.now(), "Mike Technician", "150.00", "Monthly safety inspection and belt tension check", "Scheduled"));
        
        maintenance.add(createMaintenance("MT1002", "Rowing Machine", "Repair", "Urgent",
            LocalDate.now(), "Sarah Service", "200.00", "Chain lubrication and pulley alignment", "Scheduled"));
        
        // Upcoming this week
        maintenance.add(createMaintenance("MT1003", "Smith Machine", "Warranty Service", "Normal",
            LocalDate.now().plusDays(2), "John Maintenance", "0.00", "Rails service under warranty", "Scheduled"));
        
        maintenance.add(createMaintenance("MT1004", "Cable Crossover", "Deep Clean", "Routine",
            LocalDate.now().plusDays(3), "Mike Technician", "100.00", "Deep cleaning of cables and pulleys", "Scheduled"));
        
        maintenance.add(createMaintenance("MT1005", "Elliptical Trainer", "Calibration", "Normal",
            LocalDate.now().plusDays(5), "Sarah Service", "125.00", "Resistance calibration and console update", "Scheduled"));
        
        // Overdue
        maintenance.add(createMaintenance("MT1006", "Leg Press Machine", "Parts Replacement", "Urgent",
            LocalDate.now().minusDays(2), "John Maintenance", "300.00", "Replace worn seat padding", "Overdue"));
        
        maintenance.add(createMaintenance("MT1007", "Kettlebell Set", "Repair", "Urgent",
            LocalDate.now().minusDays(5), "Mike Technician", "80.00", "Replace damaged handle on 24kg kettlebell", "Overdue"));
        
        // Completed
        maintenance.add(createMaintenance("MT1008", "Olympic Barbell", "Routine Inspection", "Routine",
            LocalDate.now().minusDays(7), "Sarah Service", "50.00", "Inspect knurling and spin", "Completed"));
        
        maintenance.add(createMaintenance("MT1009", "TRX Suspension System", "Deep Clean", "Routine",
            LocalDate.now().minusDays(10), "John Maintenance", "75.00", "Clean straps and check anchor points", "Completed"));
        
        maintenance.add(createMaintenance("MT1010", "Dumbbell Set", "Routine Inspection", "Routine",
            LocalDate.now().minusDays(14), "Mike Technician", "100.00", "Inspect all dumbbells for damage", "Completed"));
        
        return maintenance;
    }
    
    /**
     * Create maintenance data object
     */
    private MaintenanceData createMaintenance(String taskId, String equipmentName, String type, String priority,
                                             LocalDate scheduledDate, String technician, String estimatedCost,
                                             String description, String status) {
        MaintenanceData mt = new MaintenanceData();
        mt.taskId = taskId;
        mt.equipmentName = equipmentName;
        mt.maintenanceType = type;
        mt.priority = priority;
        mt.scheduledDate = scheduledDate;
        mt.technician = technician;
        mt.estimatedCost = estimatedCost;
        mt.description = description;
        mt.status = status;
        
        if (status.equals("Completed")) {
            mt.completionDate = scheduledDate.plusDays(1);
            mt.actualCost = estimatedCost;
            mt.workPerformed = "Successfully completed " + type.toLowerCase();
            mt.partsUsed = "N/A";
        }
        
        return mt;
    }
    
    // ==================== DATA CLASS ====================
    
    /**
     * Maintenance data class
     */
    private static class MaintenanceData {
        String taskId;
        String equipmentName;
        String maintenanceType;
        String priority;
        LocalDate scheduledDate;
        String technician;
        String estimatedCost;
        String description;
        String status;
        
        // Completion fields
        LocalDate completionDate;
        String actualCost;
        String workPerformed;
        String partsUsed;
    }
}
