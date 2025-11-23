/*
 * Block20 Gym Management System
 * Member Training Controller - Book and manage training sessions
 */
package com.block20.controllers.member;

import com.block20.models.Trainer;
import com.block20.models.TrainingSession;
import com.block20.services.TrainerScheduleService;
import com.block20.services.TrainerService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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

/**
 * Member Training Controller - Book sessions, view upcoming sessions
 */
public class MemberTrainingController extends ScrollPane {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private VBox contentContainer;
    private final String memberId;
    private final String memberName;
    private final TrainerService trainerService;
    private final TrainerScheduleService trainerScheduleService;
    private final Consumer<String> navigationHandler;
    private List<Trainer> availableTrainers = Collections.emptyList();
    private FlowPane trainersGrid;
    private VBox upcomingSessionsList;
    private final List<Trainer> trainerCache = new ArrayList<>();
    private final List<TrainingSession> upcomingSessions = new ArrayList<>();

    public MemberTrainingController(String memberId,
                                    String memberName,
                                    TrainerService trainerService,
                                    TrainerScheduleService trainerScheduleService,
                                    Consumer<String> navigationHandler) {
        this.memberId = memberId;
        this.memberName = memberName != null ? memberName : "Member";
        this.trainerService = trainerService;
        this.trainerScheduleService = trainerScheduleService;
        this.navigationHandler = navigationHandler;
        initializeView();
    }
    
    private void initializeView() {
        // Configure ScrollPane
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.setPadding(new Insets(32));
        contentContainer.getStyleClass().add("main-content");
        
        contentContainer.getChildren().addAll(
            createHeader(),
            createBookSessionSection(),
            createUpcomingSessionsSection()
        );
        loadTrainerData();
        refreshTrainerCards();
        refreshUpcomingSessions();
        
        setContent(contentContainer);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(8);
        
        HBox titleRow = new HBox(16);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("Training Sessions");
        title.getStyleClass().add("text-h2");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button bookButton = new Button("+ Book New Session");
        bookButton.getStyleClass().addAll("primary-button");
        bookButton.setOnAction(e -> showBookingDialog(null));
        
        titleRow.getChildren().addAll(title, spacer, bookButton);
        
        Text subtitle = new Text("Schedule and manage your training sessions");
        subtitle.getStyleClass().add("text-muted");
        
        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }
    
    private VBox createBookSessionSection() {
        VBox section = new VBox(16);
        
        Text sectionTitle = new Text("Available Trainers");
        sectionTitle.getStyleClass().add("text-h3");
        
        trainersGrid = new FlowPane(16, 16);
        trainersGrid.setPrefWrapLength(720);
        trainersGrid.setMaxWidth(Double.MAX_VALUE);
        
        Label emptyState = new Label("No trainers available to book right now.");
        emptyState.getStyleClass().add("text-muted");
        trainersGrid.getChildren().add(emptyState);
        
        section.getChildren().addAll(sectionTitle, trainersGrid);
        return section;
    }
    
    private VBox createTrainerCard(Trainer trainer) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setMaxWidth(280);
        card.setMinWidth(250);
        
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 48px;");
        avatar.setAlignment(Pos.CENTER);
        
        Text trainerName = new Text(trainer.getFullName());
        trainerName.getStyleClass().add("text-body");
        trainerName.setStyle("-fx-font-weight: 600;");
        
        String specialization = trainer.getSpecialization() != null ? trainer.getSpecialization() : "Personal Coach";
        Text trainerTitle = new Text(specialization);
        trainerTitle.getStyleClass().add("text-body-sm");
        trainerTitle.setStyle("-fx-fill: -fx-gray-600;");
        
        Text spec = new Text(trainer.getCertification() != null ? trainer.getCertification() : "Experienced Instructor");
        spec.getStyleClass().add("text-caption");
        spec.setStyle("-fx-fill: -fx-gray-500;");
        spec.setWrappingWidth(240);
        
        Button bookButton = new Button("Book Session");
        bookButton.getStyleClass().addAll("secondary-button");
        bookButton.setPrefWidth(Double.MAX_VALUE);
        bookButton.setOnAction(e -> showBookingDialog(trainer));
        
        card.getChildren().addAll(avatar, trainerName, trainerTitle, spec, bookButton);
        card.setAlignment(Pos.CENTER);
        return card;
    }
    
    private VBox createUpcomingSessionsSection() {
        VBox section = new VBox(16);
        
        Text sectionTitle = new Text("Your Upcoming Sessions");
        sectionTitle.getStyleClass().add("text-h3");
        
        upcomingSessionsList = new VBox(12);
        Label emptyState = new Label("You have no upcoming sessions yet.");
        emptyState.getStyleClass().add("text-muted");
        upcomingSessionsList.getChildren().add(emptyState);
        
        section.getChildren().addAll(sectionTitle, upcomingSessionsList);
        return section;
    }
    
    private HBox createSessionCard(TrainingSession session) {
        LocalDateTime dateTime = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        String trainerName = session.getTrainerName();
        HBox card = new HBox(16);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        
        VBox dateBox = new VBox(4);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPrefWidth(60);
        dateBox.setStyle("-fx-background-color: -fx-primary-50; -fx-background-radius: 8; -fx-padding: 8;");
        
        Text month = new Text(dateTime.format(MONTH_FORMATTER));
        month.getStyleClass().add("text-caption");
        month.setStyle("-fx-fill: -fx-primary-600; -fx-font-weight: 600;");
        
        Text day = new Text(dateTime.format(DAY_FORMATTER));
        day.getStyleClass().add("text-h3");
        day.setStyle("-fx-fill: -fx-primary-600;");
        
        dateBox.getChildren().addAll(month, day);
        
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Text sessionName = new Text(session.getSessionType());
        sessionName.getStyleClass().add("text-body");
        sessionName.setStyle("-fx-font-weight: 600;");
        
        HBox details = new HBox(12);
        Text trainer = new Text("👤 " + trainerName);
        trainer.getStyleClass().add("text-body-sm");
        Text time = new Text("🕐 " + dateTime.format(TIME_FORMATTER));
        time.getStyleClass().add("text-body-sm");
        details.getChildren().addAll(trainer, time);
        
        Text status = new Text(session.getStatus() != null ? session.getStatus() : "Scheduled");
        status.getStyleClass().add("text-caption");
        status.setStyle("-fx-fill: -fx-gray-600;");
        info.getChildren().addAll(sessionName, details, status);
        
        VBox actions = new VBox(8);
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().addAll("danger-button", "button-small");
        cancelButton.setOnAction(e -> cancelTrainingSession(session));
        actions.getChildren().add(cancelButton);
        
        card.getChildren().addAll(dateBox, info, actions);
        return card;
    }

    private void loadTrainerData() {
        trainerCache.clear();
        try {
            List<Trainer> trainers = trainerService.getAllTrainers();
            if (trainers != null) {
                trainerCache.addAll(trainers.stream()
                    .filter(trainer -> trainer.getStatus() == null || "Active".equalsIgnoreCase(trainer.getStatus()))
                    .sorted(Comparator
                        .comparing((Trainer t) -> t.getSpecialization() == null ? "~" : t.getSpecialization(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Trainer::getFullName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList()));
            }
        } catch (Exception ex) {
            trainerCache.clear();
        }
        availableTrainers = new ArrayList<>(trainerCache);
    }

    private void refreshTrainerCards() {
        if (trainersGrid == null) {
            return;
        }
        trainersGrid.getChildren().clear();
        if (availableTrainers.isEmpty()) {
            Label emptyState = new Label("No trainers available to book right now.");
            emptyState.getStyleClass().add("text-muted");
            trainersGrid.getChildren().add(emptyState);
            return;
        }
        availableTrainers.stream()
            .map(this::createTrainerCard)
            .forEach(trainersGrid.getChildren()::add);
    }

    private void refreshUpcomingSessions() {
        if (upcomingSessionsList == null) {
            return;
        }
        upcomingSessions.clear();
        try {
            List<TrainingSession> sessions = trainerScheduleService.getSessionsForMember(memberId);
            if (sessions != null) {
                upcomingSessions.addAll(sessions.stream()
                    .filter(session -> session.getSessionDate() != null && !session.getSessionDate().isBefore(LocalDate.now()))
                    .sorted(Comparator
                        .comparing(TrainingSession::getSessionDate)
                        .thenComparing(TrainingSession::getStartTime))
                    .collect(Collectors.toList()));
            }
        } catch (Exception ex) {
            upcomingSessions.clear();
        }

        upcomingSessionsList.getChildren().clear();
        if (upcomingSessions.isEmpty()) {
            Label emptyState = new Label("You have no upcoming sessions yet.");
            emptyState.getStyleClass().add("text-muted");
            upcomingSessionsList.getChildren().add(emptyState);
            return;
        }

        upcomingSessions.stream()
            .map(this::createSessionCard)
            .forEach(upcomingSessionsList.getChildren()::add);
    }

    private void showBookingDialog(Trainer preselectedTrainer) {
        if (availableTrainers.isEmpty()) {
            showInfo("No Trainers Available", "Please check back later. We are onboarding trainers.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Book Training Session");
        dialog.setHeaderText("Schedule a new training session");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        final boolean trainerLocked = preselectedTrainer != null;
        ComboBox<Trainer> trainerField = null;
        Node trainerInput;
        if (!trainerLocked) {
            trainerField = new ComboBox<>(FXCollections.observableArrayList(availableTrainers));
            trainerField.setConverter(new StringConverter<>() {
                @Override
                public String toString(Trainer trainer) {
                    return trainer == null ? "" : trainer.getFullName();
                }

                @Override
                public Trainer fromString(String string) {
                    return null;
                }
            });
            trainerField.getSelectionModel().select(availableTrainers.get(0));
            trainerField.setPrefWidth(Double.MAX_VALUE);
            trainerField.setMaxWidth(Double.MAX_VALUE);
            trainerField.setPrefHeight(44);
            trainerInput = trainerField;
        } else {
            VBox lockedTrainerBox = new VBox(4);
            lockedTrainerBox.getStyleClass().add("text-body");

            Text name = new Text(preselectedTrainer.getFullName());
            name.setStyle("-fx-font-weight: 600;");

            String specialization = preselectedTrainer.getSpecialization() != null
                ? preselectedTrainer.getSpecialization()
                : "Personal Coach";
            Text detail = new Text(specialization);
            detail.getStyleClass().add("text-body-sm");
            detail.setStyle("-fx-fill: -fx-gray-600;");

            Hyperlink changeTrainer = new Hyperlink("Choose different trainer");
            changeTrainer.setOnAction(e -> {
                dialog.close();
                Platform.runLater(() -> showBookingDialog(null));
            });

            lockedTrainerBox.getChildren().addAll(name, detail, changeTrainer);
            trainerInput = lockedTrainerBox;
        }

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setPrefWidth(Double.MAX_VALUE);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPrefHeight(44);

        ComboBox<String> sessionTypeField = new ComboBox<>(FXCollections.observableArrayList(
            "Personal Training",
            "Strength & Conditioning",
            "Yoga",
            "HIIT",
            "Mobility"
        ));
        sessionTypeField.getSelectionModel().selectFirst();
        sessionTypeField.setPrefWidth(Double.MAX_VALUE);
        sessionTypeField.setMaxWidth(Double.MAX_VALUE);
        sessionTypeField.setPrefHeight(44);

        ComboBox<LocalTime> timeField = new ComboBox<>(FXCollections.observableArrayList(buildDefaultTimeSlots()));
        timeField.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalTime time) {
                return time == null ? "" : time.format(TIME_FORMATTER);
            }

            @Override
            public LocalTime fromString(String string) {
                return null;
            }
        });
        timeField.getSelectionModel().select(LocalTime.of(9, 0));
        timeField.setPrefWidth(Double.MAX_VALUE);
        timeField.setMaxWidth(Double.MAX_VALUE);
        timeField.setPrefHeight(44);

        Spinner<Integer> durationField = new Spinner<>();
        SpinnerValueFactory<Integer> durationFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 120, 60, 15);
        durationField.setValueFactory(durationFactory);
        durationField.setEditable(false);
        durationField.setPrefWidth(Double.MAX_VALUE);
        durationField.setMaxWidth(Double.MAX_VALUE);
        durationField.setPrefHeight(44);

        Label durationDisplay = new Label(durationFactory.getValue() + " minutes");
        durationDisplay.getStyleClass().add("text-body-sm");
        durationDisplay.setStyle("-fx-fill: -fx-gray-600;");
        durationFactory.valueProperty().addListener((obs, oldVal, newVal) ->
            durationDisplay.setText(newVal + " minutes")
        );
        VBox durationWrapper = new VBox(6, durationField, durationDisplay);

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Goals or focus areas for this session");
        notesArea.setPrefRowCount(3);
        notesArea.setPrefWidth(Double.MAX_VALUE);
        notesArea.setMaxWidth(Double.MAX_VALUE);
        notesArea.setPrefHeight(88);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.add(new Label("Trainer"), 0, 0);
        form.add(trainerInput, 1, 0);
        form.add(new Label("Session Type"), 0, 1);
        form.add(sessionTypeField, 1, 1);
        form.add(new Label("Date"), 0, 2);
        form.add(datePicker, 1, 2);
        form.add(new Label("Start Time"), 0, 3);
        form.add(timeField, 1, 3);
        form.add(new Label("Duration (minutes)"), 0, 4);
        form.add(durationWrapper, 1, 4);
        form.add(new Label("Notes"), 0, 5);
        form.add(notesArea, 1, 5);

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(120);
            GridPane.setHgrow(trainerInput, Priority.ALWAYS);
            GridPane.setHgrow(sessionTypeField, Priority.ALWAYS);
            GridPane.setHgrow(datePicker, Priority.ALWAYS);
            GridPane.setHgrow(timeField, Priority.ALWAYS);
            GridPane.setHgrow(durationWrapper, Priority.ALWAYS);
            GridPane.setHgrow(notesArea, Priority.ALWAYS);
        ColumnConstraints inputCol = new ColumnConstraints();
        inputCol.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(labelCol, inputCol);

        dialog.getDialogPane().setContent(form);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (!trainerLocked && trainerField != null) {
            okButton.disableProperty().bind(
                trainerField.getSelectionModel().selectedItemProperty().isNull()
                    .or(datePicker.valueProperty().isNull())
                    .or(sessionTypeField.valueProperty().isNull())
                    .or(timeField.valueProperty().isNull())
            );
        } else {
            okButton.disableProperty().bind(
                datePicker.valueProperty().isNull()
                    .or(sessionTypeField.valueProperty().isNull())
                    .or(timeField.valueProperty().isNull())
            );
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        Trainer selectedTrainer = trainerLocked ? preselectedTrainer : trainerField.getSelectionModel().getSelectedItem();
        LocalDate sessionDate = datePicker.getValue();
        LocalTime startTime = timeField.getSelectionModel().getSelectedItem();
        int durationMinutes = durationField.getValue();
        try {
            TrainingSession session = trainerScheduleService.scheduleSession(
                memberId,
                memberName,
                selectedTrainer.getTrainerId(),
                sessionTypeField.getSelectionModel().getSelectedItem(),
                sessionDate,
                startTime,
                durationMinutes,
                notesArea.getText()
            );
            refreshUpcomingSessions();
            showInfo("Session Scheduled", "You're booked with " + selectedTrainer.getFullName() + " on " + sessionDate + ".");
        } catch (Exception ex) {
            showError("Unable to Schedule", ex.getMessage() != null ? ex.getMessage() : "Please try again later.");
        }
    }

    private List<LocalTime> buildDefaultTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime time = LocalTime.of(6, 0);
        while (!time.isAfter(LocalTime.of(20, 0))) {
            slots.add(time);
            time = time.plusMinutes(30);
        }
        return slots;
    }

    private void cancelTrainingSession(TrainingSession session) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Session");
        confirm.setHeaderText("Cancel " + session.getSessionType() + "?");
        confirm.setContentText("This will release the reserved slot with " + session.getTrainerName() + ".");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        try {
            trainerScheduleService.cancelSession(session.getSessionId(), "Cancelled by member");
            refreshUpcomingSessions();
            showInfo("Session Cancelled", "We've cancelled your session with " + session.getTrainerName() + ".");
        } catch (Exception ex) {
            showError("Unable to Cancel", ex.getMessage() != null ? ex.getMessage() : "Please try again later.");
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
