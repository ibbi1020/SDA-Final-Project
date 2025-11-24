/*
 * Block20 Gym Management System
 * Member Training Controller - Fixed Booking Logic
 */
package com.block20.controllers.member;

import com.block20.models.Trainer;
import com.block20.models.TrainerAvailabilitySlot;
import com.block20.models.TrainingSession;
import com.block20.services.TrainerScheduleService;
import com.block20.services.TrainerService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public class MemberTrainingController extends ScrollPane {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");

    private VBox contentContainer;
    private final String memberId;
    private final String memberName;
    private final TrainerService trainerService;
    private final TrainerScheduleService trainerScheduleService;
    private final Consumer<String> navigationHandler;
    
    private FlowPane trainersGrid;
    private VBox upcomingSessionsList;

    public MemberTrainingController(String memberId, String memberName, TrainerService trainerService, TrainerScheduleService trainerScheduleService, Consumer<String> navigationHandler) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.trainerService = trainerService;
        this.trainerScheduleService = trainerScheduleService;
        this.navigationHandler = navigationHandler;
        initializeView();
    }
    
    private void initializeView() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.setPadding(new Insets(32));
        contentContainer.getStyleClass().add("main-content");
        
        contentContainer.getChildren().addAll(
            createHeader(),
            createUpcomingSessionsSection(),
            createBookSessionSection()
        );
        
        setContent(contentContainer);
        refreshAllData();
    }
    
    private void refreshAllData() {
        refreshTrainerCards();
        refreshUpcomingSessions();
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        HBox titleRow = new HBox(16); titleRow.setAlignment(Pos.CENTER_LEFT);
        Text title = new Text("Training Sessions"); title.getStyleClass().add("text-h2");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button bookButton = new Button("+ Book New Session");
        bookButton.getStyleClass().addAll("btn-primary");
        bookButton.setOnAction(e -> showBookingDialog(null));
        titleRow.getChildren().addAll(title, spacer, bookButton);
        Text subtitle = new Text("Schedule and manage your training sessions"); subtitle.getStyleClass().add("text-muted");
        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }
    
    // --- TRAINER LIST SECTION ---
    
    private VBox createBookSessionSection() {
        VBox section = new VBox(16);
        Text sectionTitle = new Text("Available Trainers"); sectionTitle.getStyleClass().add("text-h3");
        trainersGrid = new FlowPane(16, 16);
        section.getChildren().addAll(sectionTitle, trainersGrid);
        return section;
    }
    
    private void refreshTrainerCards() {
        trainersGrid.getChildren().clear();
        List<Trainer> trainers = trainerService.getAllTrainers();
        
        if (trainers.isEmpty()) {
            Label empty = new Label("No trainers found. Check back later.");
            empty.getStyleClass().add("text-muted");
            trainersGrid.getChildren().add(empty);
        } else {
            for (Trainer t : trainers) {
                if ("Active".equalsIgnoreCase(t.getStatus())) {
                    trainersGrid.getChildren().add(createTrainerCard(t));
                }
            }
        }
    }
    
    private VBox createTrainerCard(Trainer trainer) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        
        Label avatar = new Label("🏋️"); avatar.setStyle("-fx-font-size: 40px;"); avatar.setAlignment(Pos.CENTER);
        Text name = new Text(trainer.getFullName()); name.getStyleClass().add("text-body"); name.setStyle("-fx-font-weight: bold;");
        Text spec = new Text(trainer.getSpecialization()); spec.getStyleClass().add("text-caption");
        
        Button bookBtn = new Button("Book Session");
        bookBtn.getStyleClass().add("btn-secondary");
        bookBtn.setPrefWidth(Double.MAX_VALUE);
        bookBtn.setOnAction(e -> showBookingDialog(trainer));
        
        card.getChildren().addAll(avatar, name, spec, bookBtn);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    // --- UPCOMING SESSIONS SECTION ---

    private VBox createUpcomingSessionsSection() {
        VBox section = new VBox(16);
        Text title = new Text("Your Upcoming Sessions"); title.getStyleClass().add("text-h3");
        upcomingSessionsList = new VBox(12);
        section.getChildren().addAll(title, upcomingSessionsList);
        return section;
    }

    private void refreshUpcomingSessions() {
        upcomingSessionsList.getChildren().clear();
        List<TrainingSession> sessions = trainerScheduleService.getSessionsForMember(memberId);
        
        if (sessions.isEmpty()) {
            upcomingSessionsList.getChildren().add(new Label("No upcoming sessions."));
        } else {
            // Sort by date
            sessions.sort(Comparator.comparing(TrainingSession::getSessionDate).thenComparing(TrainingSession::getStartTime));
            
            for (TrainingSession s : sessions) {
                if (!s.getSessionDate().isBefore(LocalDate.now())) {
                    upcomingSessionsList.getChildren().add(createSessionCard(s));
                }
            }
        }
    }

    private HBox createSessionCard(TrainingSession session) {
        HBox card = new HBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        
        VBox dateBox = new VBox(4);
        dateBox.setAlignment(Pos.CENTER); dateBox.setPrefWidth(60);
        dateBox.setStyle("-fx-background-color: #EFF6FF; -fx-background-radius: 8; -fx-padding: 8;");
        Text month = new Text(session.getSessionDate().format(DateTimeFormatter.ofPattern("MMM")));
        Text day = new Text(session.getSessionDate().format(DateTimeFormatter.ofPattern("dd"))); day.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        dateBox.getChildren().addAll(month, day);
        
        VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
        Text title = new Text(session.getSessionType()); title.setStyle("-fx-font-weight: bold;");
        Text details = new Text("with " + session.getTrainerName() + " at " + session.getStartTime().format(TIME_FORMATTER));
        info.getChildren().addAll(title, details);
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-danger-small");
        cancelBtn.setOnAction(e -> cancelSession(session));
        
        card.getChildren().addAll(dateBox, info, cancelBtn);
        return card;
    }

    // --- BOOKING LOGIC ---

    private void showBookingDialog(Trainer preselectedTrainer) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Book Training Session");
        dialog.setHeaderText("Schedule a new training session");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane grid = new GridPane(); grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));

        // 1. Trainer Selection
        ComboBox<Trainer> trainerCombo = new ComboBox<>();
        trainerCombo.getItems().addAll(trainerService.getAllTrainers());
        trainerCombo.setConverter(new StringConverter<>() {
            public String toString(Trainer t) { return t == null ? "" : t.getFullName(); }
            public Trainer fromString(String s) { return null; }
        });
        if (preselectedTrainer != null) {
            trainerCombo.getSelectionModel().select(preselectedTrainer);
            trainerCombo.setDisable(true); // Lock if preselected
        }

        // 2. Date Selection
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        
        // 3. Time Selection (Dependent on Trainer & Date)
        ComboBox<LocalTime> timeCombo = new ComboBox<>();
        timeCombo.setDisable(true); // Disabled until date picked

        // 4. Type
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Personal Training", "Yoga", "HIIT", "Consultation"));
        typeCombo.getSelectionModel().selectFirst();

        // Logic to populate times
        datePicker.valueProperty().addListener((obs, oldVal, date) -> {
            Trainer t = trainerCombo.getValue();
            if (t != null && date != null) {
                loadAvailableSlots(t, date, timeCombo);
            }
        });
        trainerCombo.valueProperty().addListener((obs, old, t) -> {
            if (t != null && datePicker.getValue() != null) {
                loadAvailableSlots(t, datePicker.getValue(), timeCombo);
            }
        });

        grid.add(new Label("Trainer:"), 0, 0); grid.add(trainerCombo, 1, 0);
        grid.add(new Label("Date:"), 0, 1); grid.add(datePicker, 1, 1);
        grid.add(new Label("Time:"), 0, 2); grid.add(timeCombo, 1, 2);
        grid.add(new Label("Type:"), 0, 3); grid.add(typeCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);
        
        // Initial load if preselected
        if (preselectedTrainer != null) {
            loadAvailableSlots(preselectedTrainer, datePicker.getValue(), timeCombo);
        }

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (trainerCombo.getValue() == null || timeCombo.getValue() == null) {
                        throw new IllegalArgumentException("Please select a trainer and time.");
                    }
                    
                    trainerScheduleService.scheduleSession(
                        memberId, memberName,
                        trainerCombo.getValue().getTrainerId(),
                        typeCombo.getValue(),
                        datePicker.getValue(),
                        timeCombo.getValue(),
                        60, // Default 1 hour
                        "Booked via Member Portal"
                    );
                    
                    refreshUpcomingSessions();
                    showInfo("Success", "Session booked successfully!");
                    
                } catch (Exception e) {
                    showError("Booking Failed", e.getMessage());
                }
            }
        });
    }

    private void loadAvailableSlots(Trainer trainer, LocalDate date, ComboBox<LocalTime> timeCombo) {
        timeCombo.getItems().clear();
        timeCombo.setDisable(true);
        
        // 1. Get Trainer's Shift for this day of week
        List<TrainerAvailabilitySlot> slots = trainerScheduleService.getAvailabilityForTrainer(trainer.getTrainerId());
        Optional<TrainerAvailabilitySlot> shift = slots.stream()
            .filter(s -> s.getDayOfWeek() == date.getDayOfWeek())
            .findFirst();
            
        if (shift.isEmpty()) {
            timeCombo.setPromptText("Trainer off this day");
            return;
        }
        
        // 2. Generate Slots (e.g. 9:00, 10:00...)
        LocalTime start = shift.get().getStartTime();
        LocalTime end = shift.get().getEndTime();
        List<LocalTime> possibleTimes = new ArrayList<>();
        
        while (start.isBefore(end)) {
            possibleTimes.add(start);
            start = start.plusHours(1);
        }
        
        // 3. Filter out booked slots (Simple version)
        // In a real app, check 'trainerScheduleService.getSessionsForTrainer(id)' and remove conflicts
        // For this demo, we assume availability logic is in service or basic slots are free
        
        timeCombo.getItems().addAll(possibleTimes);
        timeCombo.setDisable(false);
        timeCombo.setPromptText("Select a time");
    }

    private void cancelSession(TrainingSession session) {
        try {
            trainerScheduleService.cancelSession(session.getSessionId(), "Member cancelled");
            refreshUpcomingSessions();
        } catch (Exception e) {
            showError("Error", "Could not cancel session.");
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}