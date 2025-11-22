/*
 * Block20 Gym Management System
 * Training Sessions Controller - Schedule and Manage Training Sessions
 */
package com.block20.controllers.trainers;

import com.block20.models.Trainer;
import com.block20.models.TrainerAvailabilitySlot;
import com.block20.models.TrainingSession;
import com.block20.services.TrainerScheduleService;
import com.block20.services.TrainerService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Controller for training sessions management:
 * 1. View All Sessions (upcoming, completed, cancelled)
 * 2. Schedule New Session
 * 3. Cancel/Reschedule Session
 * 4. Mark Attendance
 */
public class TrainingSessionsController extends ScrollPane {
    
    private VBox contentContainer;
    private Consumer<String> navigationHandler;
    private final TrainerService trainerService;
    private final TrainerScheduleService trainerScheduleService;
    private List<Trainer> availableTrainers = Collections.emptyList();
    private VBox sessionsTableRows;
    private Label todaysSessionsValue;
    private Label weeklySessionsValue;
    private Label monthlySessionsValue;
    private Label revenueValue;
    private ToggleButton upcomingTab;
    private ToggleButton todayTab;
    private ToggleButton completedTab;
    private ToggleButton cancelledTab;
    
    // Current filter state
    private String currentFilter = "Upcoming"; // Upcoming, Completed, Cancelled, All
    
    /**
     * Constructor
     */
    public TrainingSessionsController(Consumer<String> navigationHandler,
                                      TrainerService trainerService,
                                      TrainerScheduleService trainerScheduleService) {
        this.navigationHandler = navigationHandler;
        this.trainerService = trainerService;
        this.trainerScheduleService = trainerScheduleService;
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
        
        // Stats bar
        HBox statsBar = createStatsBar();
        
        // Sessions table
        VBox tableSection = createSessionsTable();
        
        contentContainer.getChildren().addAll(header, actionBar, filterTabs, statsBar, tableSection);
        loadTrainerData();
        
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
        
        Label titleLabel = new Label("Training Sessions");
        titleLabel.getStyleClass().add("page-title");
        
        Label subtitleLabel = new Label("Schedule and manage personal training sessions");
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
        datePicker.setPromptText("Select date");
        
        Label dateLabel = new Label("Jump to date:");
        dateLabel.getStyleClass().add("form-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Schedule session button
        Button scheduleButton = new Button("+ Schedule Session");
        scheduleButton.getStyleClass().add("btn-primary");
        scheduleButton.setOnAction(e -> showScheduleSessionDialog());
        
        Button manageAvailabilityButton = new Button("Manage Availability");
        manageAvailabilityButton.getStyleClass().add("btn-secondary");
        manageAvailabilityButton.setOnAction(e -> showAvailabilityManager());
        
        actionBar.getChildren().addAll(dateLabel, datePicker, spacer, manageAvailabilityButton, scheduleButton);
        
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
        
        upcomingTab = createFilterTab("Upcoming (0)", filterGroup, true);
        upcomingTab.setOnAction(e -> applyFilter("Upcoming"));
        
        todayTab = createFilterTab("Today (0)", filterGroup, false);
        todayTab.setOnAction(e -> applyFilter("Today"));
        
        completedTab = createFilterTab("Completed (0)", filterGroup, false);
        completedTab.setOnAction(e -> applyFilter("Completed"));
        
        cancelledTab = createFilterTab("Cancelled (0)", filterGroup, false);
        cancelledTab.setOnAction(e -> applyFilter("Cancelled"));
        
        tabsBox.getChildren().addAll(upcomingTab, todayTab, completedTab, cancelledTab);
        
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
    
    /**
     * Create stats bar
     */
    private HBox createStatsBar() {
        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(15));
        statsBar.getStyleClass().add("stats-bar");

        todaysSessionsValue = createStatValueLabel("#3B82F6");
        weeklySessionsValue = createStatValueLabel("#10B981");
        monthlySessionsValue = createStatValueLabel("#8B5CF6");
        revenueValue = createStatValueLabel("#F59E0B");

        statsBar.getChildren().addAll(
            createStatItem("Today's Sessions", todaysSessionsValue, "#3B82F6"),
            createStatItem("This Week", weeklySessionsValue, "#10B981"),
            createStatItem("This Month", monthlySessionsValue, "#8B5CF6"),
            createStatItem("Revenue (Month)", revenueValue, "#F59E0B")
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
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        statBox.getChildren().addAll(valueLabel, labelText);
        
        return statBox;
    }

    private Label createStatValueLabel(String color) {
        Label label = new Label("0");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        return label;
    }
    
    /**
     * Create sessions table
     */
    private VBox createSessionsTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");
        
        // Table header
        HBox tableHeader = new HBox();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(10));
        
        Label dateCol = new Label("Date & Time");
        dateCol.setPrefWidth(150);
        dateCol.getStyleClass().add("table-header-cell");
        
        Label memberCol = new Label("Member");
        memberCol.setPrefWidth(200);
        memberCol.getStyleClass().add("table-header-cell");
        
        Label trainerCol = new Label("Trainer");
        trainerCol.setPrefWidth(180);
        trainerCol.getStyleClass().add("table-header-cell");
        
        Label typeCol = new Label("Session Type");
        typeCol.setPrefWidth(150);
        typeCol.getStyleClass().add("table-header-cell");
        
        Label durationCol = new Label("Duration");
        durationCol.setPrefWidth(100);
        durationCol.getStyleClass().add("table-header-cell");
        
        Label statusCol = new Label("Status");
        statusCol.setPrefWidth(120);
        statusCol.getStyleClass().add("table-header-cell");
        
        Label actionCol = new Label("Actions");
        actionCol.setPrefWidth(200);
        actionCol.getStyleClass().add("table-header-cell");
        
        tableHeader.getChildren().addAll(dateCol, memberCol, trainerCol, typeCol, durationCol, statusCol, actionCol);
        
        // Table rows
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("table-scroll");
        
        sessionsTableRows = new VBox(5);
        sessionsTableRows.setPadding(new Insets(10));
        refreshSessionsTable();
        
        scrollPane.setContent(sessionsTableRows);
        
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }

    private void refreshSessionsTable() {
        if (sessionsTableRows == null) {
            return;
        }
        List<TrainingSession> sessions = trainerScheduleService != null
                ? trainerScheduleService.getAllSessions()
                : Collections.emptyList();

        List<TrainingSession> filtered = filterSessions(sessions);

        sessionsTableRows.getChildren().setAll(
                filtered.stream()
                        .map(this::createTableRow)
                        .collect(Collectors.toList())
        );

        updateStatCards(sessions);
        updateFilterTabCounts(sessions);
    }

    private List<TrainingSession> filterSessions(List<TrainingSession> sessions) {
        LocalDate today = LocalDate.now();
        return sessions.stream()
                .filter(session -> {
                    switch (currentFilter) {
                        case "Today":
                            return session.getSessionDate().equals(today);
                        case "Completed":
                            return "Completed".equalsIgnoreCase(session.getStatus());
                        case "Cancelled":
                            return "Cancelled".equalsIgnoreCase(session.getStatus());
                        case "Upcoming":
                        default:
                            return ("Scheduled".equalsIgnoreCase(session.getStatus())
                                    || "In Progress".equalsIgnoreCase(session.getStatus()))
                                    && !session.getSessionDate().isBefore(today);
                    }
                })
                .sorted(Comparator
                        .comparing(TrainingSession::getSessionDate)
                        .thenComparing(TrainingSession::getStartTime))
                .collect(Collectors.toList());
    }

    private void updateStatCards(List<TrainingSession> sessions) {
        if (todaysSessionsValue == null) {
            return;
        }
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        long todayCount = sessions.stream()
            .filter(this::isCountableForStats)
            .filter(session -> session.getSessionDate().equals(today))
                .count();

        long weekCount = sessions.stream()
            .filter(this::isCountableForStats)
            .filter(session -> !session.getSessionDate().isBefore(startOfWeek)
                        && !session.getSessionDate().isAfter(endOfWeek))
                .count();

        long monthCount = sessions.stream()
            .filter(this::isCountableForStats)
            .filter(session -> session.getSessionDate().getMonth().equals(today.getMonth())
                        && session.getSessionDate().getYear() == today.getYear())
                .count();

        int revenueEstimate = (int) (monthCount * 75); // Rough estimate

        todaysSessionsValue.setText(String.valueOf(todayCount));
        weeklySessionsValue.setText(String.valueOf(weekCount));
        monthlySessionsValue.setText(String.valueOf(monthCount));
        revenueValue.setText("$" + revenueEstimate);
    }

    private boolean isCountableForStats(TrainingSession session) {
        String status = session.getStatus() != null ? session.getStatus() : "";
        return !"Cancelled".equalsIgnoreCase(status);
    }

    private void updateFilterTabCounts(List<TrainingSession> sessions) {
        if (upcomingTab == null) {
            return;
        }
        LocalDate today = LocalDate.now();

        long todayCount = sessions.stream()
                .filter(session -> session.getSessionDate().equals(today))
                .count();

        long upcomingCount = sessions.stream()
                .filter(session -> isUpcoming(session, today))
                .count();

        long completedCountValue = sessions.stream()
                .filter(session -> "Completed".equalsIgnoreCase(session.getStatus()))
                .count();

        long cancelledCountValue = sessions.stream()
                .filter(session -> "Cancelled".equalsIgnoreCase(session.getStatus()))
                .count();

        upcomingTab.setText(String.format("Upcoming (%d)", upcomingCount));
        todayTab.setText(String.format("Today (%d)", todayCount));
        completedTab.setText(String.format("Completed (%d)", completedCountValue));
        cancelledTab.setText(String.format("Cancelled (%d)", cancelledCountValue));
    }

    private boolean isUpcoming(TrainingSession session, LocalDate today) {
        String status = session.getStatus() != null ? session.getStatus() : "";
        boolean isFutureOrToday = !session.getSessionDate().isBefore(today);
        return isFutureOrToday && ("Scheduled".equalsIgnoreCase(status) || "In Progress".equalsIgnoreCase(status));
    }
    
    /**
     * Create table row
     */
    private HBox createTableRow(TrainingSession session) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Date & Time
        VBox dateTimeBox = new VBox(2);
        dateTimeBox.setPrefWidth(150);
        Label dateLabel = new Label(session.getSessionDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.setStyle("-fx-font-weight: 500;");
        Label timeLabel = new Label(session.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);
        
        // Member
        Label memberLabel = new Label(session.getMemberName());
        memberLabel.setPrefWidth(200);
        
        // Trainer
        Label trainerLabel = new Label(session.getTrainerName());
        trainerLabel.setPrefWidth(180);
        trainerLabel.setStyle("-fx-font-weight: 500;");
        
        // Session Type
        Label typeLabel = new Label(session.getSessionType());
        typeLabel.setPrefWidth(150);
        
        // Duration
        Label durationLabel = new Label(session.getDurationMinutes() + " min");
        durationLabel.setPrefWidth(100);
        
        // Status badge
        Label statusBadge = new Label(session.getStatus());
        statusBadge.setPrefWidth(120);
        statusBadge.getStyleClass().add("badge");
        switch (session.getStatus()) {
            case "Scheduled":
                statusBadge.getStyleClass().add("badge-info");
                break;
            case "Completed":
                statusBadge.getStyleClass().add("badge-success");
                break;
            case "Cancelled":
                statusBadge.getStyleClass().add("badge-error");
                break;
            case "In Progress":
                statusBadge.getStyleClass().add("badge-warning");
                break;
        }
        
        // Action buttons
        HBox actionBox = new HBox(8);
        actionBox.setPrefWidth(200);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        if ("Scheduled".equals(session.getStatus())) {
            Button viewButton = new Button("View");
            viewButton.getStyleClass().add("btn-primary-small");
            viewButton.setOnAction(e -> viewSession(session));
            
            Button cancelButton = new Button("Cancel");
            cancelButton.getStyleClass().add("btn-secondary-small");
            cancelButton.setOnAction(e -> cancelSession(session));
            
            actionBox.getChildren().addAll(viewButton, cancelButton);
        } else {
            Button viewButton = new Button("View");
            viewButton.getStyleClass().add("btn-primary-small");
            viewButton.setOnAction(e -> viewSession(session));
            
            actionBox.getChildren().add(viewButton);
        }
        
        row.getChildren().addAll(dateTimeBox, memberLabel, trainerLabel, typeLabel, durationLabel, statusBadge, actionBox);
        
        return row;
    }
    
    /**
     * Apply filter
     */
    private void applyFilter(String filter) {
        currentFilter = filter;
        refreshSessionsTable();
    }
    
    // ==================== DIALOG VIEWS ====================
    
    /**
     * Show schedule session dialog
     */
    private void showScheduleSessionDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Schedule Training Session");
        dialog.setHeaderText("Book a new training session");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Member selection
        ComboBox<MemberOption> memberBox = new ComboBox<>();
        memberBox.setItems(FXCollections.observableArrayList(getSampleMembers()));
        memberBox.setPromptText("Select member");
        memberBox.setPrefWidth(300);
        memberBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(MemberOption option) {
                return option != null ? option.toString() : "";
            }

            @Override
            public MemberOption fromString(String string) {
                return null;
            }
        });
        
        // Trainer selection
        ComboBox<Trainer> trainerBox = new ComboBox<>();
        trainerBox.setPromptText("Select trainer");
        trainerBox.setPrefWidth(300);
        populateTrainerBox(trainerBox);
        
        // Session type
        ComboBox<String> sessionTypeBox = new ComboBox<>();
        sessionTypeBox.getItems().addAll(
            "Personal Training",
            "Yoga Session",
            "CrossFit Training",
            "Pilates",
            "Strength Training",
            "Cardio Training"
        );
        sessionTypeBox.setPromptText("Select session type");
        sessionTypeBox.setPrefWidth(300);
        
        // Date picker
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setPrefWidth(300);
        
        // Time selection
        ComboBox<String> timeBox = new ComboBox<>();
        for (int hour = 6; hour <= 20; hour++) {
            for (int min = 0; min < 60; min += 30) {
                String time = String.format("%02d:%02d", hour, min);
                timeBox.getItems().add(time);
            }
        }
        timeBox.setPromptText("Select time");
        timeBox.setPrefWidth(300);
        
        // Duration
        ComboBox<String> durationBox = new ComboBox<>();
        durationBox.getItems().addAll("30 minutes", "45 minutes", "60 minutes", "90 minutes");
        durationBox.setValue("60 minutes");
        durationBox.setPrefWidth(300);
        
        // Notes
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Session notes or special instructions...");
        notesArea.setPrefRowCount(3);
        notesArea.setPrefWidth(300);
        
        // Add to grid
        grid.add(new Label("Member:"), 0, 0);
        grid.add(memberBox, 1, 0);
        grid.add(new Label("Trainer:"), 0, 1);
        grid.add(trainerBox, 1, 1);
        grid.add(new Label("Session Type:"), 0, 2);
        grid.add(sessionTypeBox, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Time:"), 0, 4);
        grid.add(timeBox, 1, 4);
        grid.add(new Label("Duration:"), 0, 5);
        grid.add(durationBox, 1, 5);
        grid.add(new Label("Notes:"), 0, 6);
        grid.add(notesArea, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate
                if (memberBox.getValue() == null || trainerBox.getValue() == null || 
                    sessionTypeBox.getValue() == null || timeBox.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Missing Required Fields");
                    alert.setContentText("Please fill in all required fields.");
                    alert.showAndWait();
                    return;
                }
                
                MemberOption selectedMember = memberBox.getValue();
                Trainer selectedTrainer = trainerBox.getValue();
                LocalDate sessionDate = datePicker.getValue();
                LocalTime sessionTime = LocalTime.parse(timeBox.getValue());
                int durationMinutes = Integer.parseInt(durationBox.getValue().split(" ")[0]);

                try {
                    if (trainerScheduleService == null) {
                        throw new IllegalStateException("Scheduling service is not available.");
                    }
                    TrainingSession session = trainerScheduleService.scheduleSession(
                            selectedMember.memberId,
                            selectedMember.memberName,
                            selectedTrainer.getTrainerId(),
                            sessionTypeBox.getValue(),
                            sessionDate,
                            sessionTime,
                            durationMinutes,
                            notesArea.getText()
                    );

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText("Session Scheduled Successfully");
                    success.setContentText("Session ID: " + session.getSessionId() + "\n" +
                            "Date: " + session.getSessionDate() + " at " + session.getStartTime() + "\n" +
                            "Confirmation emails sent to member and trainer.");
                    success.showAndWait();
                    refreshSessionsTable();
                } catch (Exception ex) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Unable to schedule session");
                    error.setHeaderText("Validation error");
                    error.setContentText(ex.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private void showAvailabilityManager() {
        if (trainerScheduleService == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unavailable");
            alert.setHeaderText("Scheduling service not available");
            alert.setContentText("Please contact support.");
            alert.showAndWait();
            return;
        }

        loadTrainerData();
        if (availableTrainers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Trainers");
            alert.setHeaderText("No trainers found");
            alert.setContentText("Add trainers before configuring availability.");
            alert.showAndWait();
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Trainer Availability");
        dialog.setHeaderText("Manage availability slots");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        ComboBox<Trainer> trainerBox = new ComboBox<>();
        trainerBox.setPrefWidth(350);
        trainerBox.setItems(FXCollections.observableArrayList(availableTrainers));
        configureTrainerComboBoxDisplay(trainerBox);
        trainerBox.getSelectionModel().selectFirst();

        ListView<TrainerAvailabilitySlot> slotList = new ListView<>();
        slotList.setPrefHeight(250);
        slotList.setPlaceholder(new Label("No availability slots yet"));
        slotList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(TrainerAvailabilitySlot item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                        String text = String.format("%s: %s - %s",
                            item.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault()),
                            item.getStartTime(),
                            item.getEndTime());
                    setText(text);
                }
            }
        });

        ComboBox<DayOfWeek> dayBox = new ComboBox<>(FXCollections.observableArrayList(DayOfWeek.values()));
        dayBox.getSelectionModel().select(DayOfWeek.MONDAY);
        ComboBox<String> startTimeBox = new ComboBox<>(FXCollections.observableArrayList(buildTimeOptions()));
        startTimeBox.setPromptText("Start");
        ComboBox<String> endTimeBox = new ComboBox<>(FXCollections.observableArrayList(buildTimeOptions()));
        endTimeBox.setPromptText("End");

        HBox addSlotRow = new HBox(10, dayBox, startTimeBox, new Label("to"), endTimeBox);
        addSlotRow.setAlignment(Pos.CENTER_LEFT);

        Button addSlotButton = new Button("Add Slot");
        addSlotButton.getStyleClass().add("btn-primary-small");
        Button removeSlotButton = new Button("Remove Selected");
        removeSlotButton.getStyleClass().add("btn-secondary-small");
        removeSlotButton.disableProperty().bind(slotList.getSelectionModel().selectedItemProperty().isNull());

        addSlotButton.setOnAction(e -> {
            Trainer trainer = trainerBox.getValue();
            if (trainer == null) {
                showError("Select trainer", "Please pick a trainer");
                return;
            }
            DayOfWeek day = dayBox.getValue();
            String startValue = startTimeBox.getValue();
            String endValue = endTimeBox.getValue();
            if (day == null || startValue == null || endValue == null) {
                showError("Missing fields", "Day, start, and end times are required.");
                return;
            }
            LocalTime start = LocalTime.parse(startValue);
            LocalTime end = LocalTime.parse(endValue);
            if (!start.isBefore(end)) {
                showError("Invalid time", "Start must be before end time.");
                return;
            }
            try {
                trainerScheduleService.addAvailabilitySlot(trainer.getTrainerId(), day, start, end);
                refreshAvailabilityList(trainer, slotList);
            } catch (Exception ex) {
                showError("Unable to add slot", ex.getMessage());
            }
        });

        removeSlotButton.setOnAction(e -> {
            TrainerAvailabilitySlot selected = slotList.getSelectionModel().getSelectedItem();
            Trainer trainer = trainerBox.getValue();
            if (selected == null || trainer == null) {
                return;
            }
            try {
                trainerScheduleService.removeAvailabilitySlot(selected.getSlotId());
                refreshAvailabilityList(trainer, slotList);
            } catch (Exception ex) {
                showError("Unable to remove slot", ex.getMessage());
            }
        });

        trainerBox.valueProperty().addListener((obs, old, val) -> refreshAvailabilityList(val, slotList));
        refreshAvailabilityList(trainerBox.getValue(), slotList);

        HBox buttonRow = new HBox(10, addSlotButton, removeSlotButton);

        root.getChildren().addAll(new Label("Trainer"), trainerBox, slotList, addSlotRow, buttonRow);
        dialog.getDialogPane().setContent(root);
        dialog.showAndWait();
    }

    private void loadTrainerData() {
        if (trainerService != null) {
            availableTrainers = trainerService.getAllTrainers();
        } else {
            availableTrainers = Collections.emptyList();
        }
    }

    private void populateTrainerBox(ComboBox<Trainer> trainerBox) {
        loadTrainerData();
        if (availableTrainers.isEmpty()) {
            trainerBox.setItems(FXCollections.observableArrayList());
            trainerBox.setPromptText("No trainers available");
            trainerBox.setDisable(true);
            return;
        }

        trainerBox.setDisable(false);
        trainerBox.setItems(FXCollections.observableArrayList(availableTrainers));
        configureTrainerComboBoxDisplay(trainerBox);
    }

    private String formatTrainerDisplay(Trainer trainer) {
        return trainer.getFullName() + " - " + trainer.getSpecialization();
    }

    private void configureTrainerComboBoxDisplay(ComboBox<Trainer> comboBox) {
        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Trainer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatTrainerDisplay(item));
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Trainer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatTrainerDisplay(item));
            }
        });
    }
    
    /**
     * View session details
     */
    private void viewSession(TrainingSession session) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Session Details");
        dialog.setHeaderText("Training Session #" + session.getSessionId());
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        // Session Information
        VBox sessionInfo = new VBox(10);
        sessionInfo.getStyleClass().add("info-card");
        sessionInfo.setPadding(new Insets(15));
        
        Label sessionTitle = new Label("Session Information");
        sessionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane sessionGrid = new GridPane();
        sessionGrid.setHgap(20);
        sessionGrid.setVgap(8);
        
        addDetailRow(sessionGrid, 0, "Session ID:", session.getSessionId());
        addDetailRow(sessionGrid, 1, "Date:", session.getSessionDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addDetailRow(sessionGrid, 2, "Time:", session.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        addDetailRow(sessionGrid, 3, "Duration:", session.getDurationMinutes() + " minutes");
        addDetailRow(sessionGrid, 4, "Session Type:", session.getSessionType());
        addDetailRow(sessionGrid, 5, "Status:", session.getStatus());
        
        sessionInfo.getChildren().addAll(sessionTitle, sessionGrid);
        
        // Participant Information
        VBox participantInfo = new VBox(10);
        participantInfo.getStyleClass().add("info-card");
        participantInfo.setPadding(new Insets(15));
        
        Label participantTitle = new Label("Participants");
        participantTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane participantGrid = new GridPane();
        participantGrid.setHgap(20);
        participantGrid.setVgap(8);
        
        addDetailRow(participantGrid, 0, "Member:", session.getMemberName());
        addDetailRow(participantGrid, 1, "Trainer:", session.getTrainerName());

        if (session.getNotes() != null && !session.getNotes().isBlank()) {
            VBox notesBox = new VBox(5);
            notesBox.getStyleClass().add("info-card");
            notesBox.setPadding(new Insets(15));
            Label notesTitle = new Label("Notes");
            notesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            Label notesValue = new Label(session.getNotes());
            notesValue.setWrapText(true);
            notesBox.getChildren().addAll(notesTitle, notesValue);
            content.getChildren().add(notesBox);
        }
        
        participantInfo.getChildren().addAll(participantTitle, participantGrid);
        
        content.getChildren().addAll(sessionInfo, participantInfo);
        
        dialog.getDialogPane().setContent(content);
        
        if ("Scheduled".equals(session.getStatus())) {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        }
        
        dialog.showAndWait();
    }
    
    /**
     * Cancel session
     */
    private void cancelSession(TrainingSession session) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Session");
        alert.setHeaderText("Cancel Training Session");
        alert.setContentText("Are you sure you want to cancel this session?\n\n" +
                             "Member: " + session.getMemberName() + "\n" +
                             "Trainer: " + session.getTrainerName() + "\n" +
                             "Date: " + session.getSessionDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                             " at " + session.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (trainerScheduleService != null) {
                        trainerScheduleService.cancelSession(session.getSessionId(), "Cancelled via UI");
                    }
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText("Session Cancelled");
                    success.setContentText("The session has been cancelled.\nNotifications sent to member and trainer.");
                    success.showAndWait();
                    refreshSessionsTable();
                } catch (Exception ex) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Cancellation Failed");
                    error.setHeaderText("Unable to cancel session");
                    error.setContentText(ex.getMessage());
                    error.showAndWait();
                }
            }
        });
    }
    
    /**
     * Add detail row
     */
    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelText = new Label(label);
        labelText.getStyleClass().add("form-label");
        labelText.setStyle("-fx-font-weight: 500; -fx-min-width: 120px;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 14px;");
        
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }

    private void refreshAvailabilityList(Trainer trainer, ListView<TrainerAvailabilitySlot> listView) {
        if (listView == null) {
            return;
        }
        if (trainer == null || trainerScheduleService == null) {
            listView.setItems(FXCollections.observableArrayList());
            return;
        }
        List<TrainerAvailabilitySlot> slots = trainerScheduleService.getAvailabilityForTrainer(trainer.getTrainerId()).stream()
                .sorted(Comparator
                        .comparing((TrainerAvailabilitySlot slot) -> slot.getDayOfWeek().getValue())
                        .thenComparing(TrainerAvailabilitySlot::getStartTime))
                .collect(Collectors.toList());
        listView.setItems(FXCollections.observableArrayList(slots));
    }

    private List<String> buildTimeOptions() {
        List<String> times = new ArrayList<>();
        LocalTime time = LocalTime.of(6, 0);
        LocalTime end = LocalTime.of(21, 0);
        while (!time.isAfter(end)) {
            times.add(time.toString());
            time = time.plusMinutes(30);
        }
        return times;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<MemberOption> getSampleMembers() {
        return List.of(
                new MemberOption("M1001", "John Smith"),
                new MemberOption("M1002", "Sarah Johnson"),
                new MemberOption("M1003", "Mike Chen"),
                new MemberOption("M1004", "Emma Davis"),
                new MemberOption("M1005", "Lisa Martinez")
        );
    }

    private static class MemberOption {
        private final String memberId;
        private final String memberName;

        private MemberOption(String memberId, String memberName) {
            this.memberId = memberId;
            this.memberName = memberName;
        }

        @Override
        public String toString() {
            return memberId + " - " + memberName;
        }
    }
}
