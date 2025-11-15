/*
 * Block20 Gym Management System
 * Member Training Controller - Book and manage training sessions
 */
package com.block20.controllers.member;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Member Training Controller - Book sessions, view upcoming sessions
 */
public class MemberTrainingController extends ScrollPane {
    
    private VBox contentContainer;
    private String memberId;
    private Consumer<String> navigationHandler;
    
    public MemberTrainingController(String memberId, Consumer<String> navigationHandler) {
        this.memberId = memberId;
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
        bookButton.setOnAction(e -> showBookingDialog());
        
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
        
        // Use FlowPane for responsive wrapping
        FlowPane trainersGrid = new FlowPane(16, 16);
        
        trainersGrid.getChildren().addAll(
            createTrainerCard("John Smith", "Personal Training", "Strength & Conditioning"),
            createTrainerCard("Sarah Johnson", "Yoga Instructor", "Flexibility & Balance"),
            createTrainerCard("Mike Davis", "HIIT Specialist", "Cardio & Weight Loss")
        );
        
        section.getChildren().addAll(sectionTitle, trainersGrid);
        return section;
    }
    
    private VBox createTrainerCard(String name, String title, String specialization) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setMaxWidth(280);
        card.setMinWidth(250);
        
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 48px;");
        avatar.setAlignment(Pos.CENTER);
        
        Text trainerName = new Text(name);
        trainerName.getStyleClass().add("text-body");
        trainerName.setStyle("-fx-font-weight: 600;");
        
        Text trainerTitle = new Text(title);
        trainerTitle.getStyleClass().add("text-body-sm");
        trainerTitle.setStyle("-fx-fill: -fx-gray-600;");
        
        Text spec = new Text(specialization);
        spec.getStyleClass().add("text-caption");
        spec.setStyle("-fx-fill: -fx-gray-500;");
        spec.setWrappingWidth(240);
        
        Button bookButton = new Button("Book Session");
        bookButton.getStyleClass().addAll("secondary-button");
        bookButton.setPrefWidth(Double.MAX_VALUE);
        bookButton.setOnAction(e -> showBookingDialog());
        
        card.getChildren().addAll(avatar, trainerName, trainerTitle, spec, bookButton);
        card.setAlignment(Pos.CENTER);
        return card;
    }
    
    private VBox createUpcomingSessionsSection() {
        VBox section = new VBox(16);
        
        Text sectionTitle = new Text("Your Upcoming Sessions");
        sectionTitle.getStyleClass().add("text-h3");
        
        VBox sessionsList = new VBox(12);
        
        sessionsList.getChildren().addAll(
            createSessionCard("Personal Training", "John Smith", LocalDateTime.now().plusDays(2).withHour(14).withMinute(0)),
            createSessionCard("Yoga Class", "Sarah Johnson", LocalDateTime.now().plusDays(5).withHour(10).withMinute(30)),
            createSessionCard("HIIT Session", "Mike Davis", LocalDateTime.now().plusDays(7).withHour(18).withMinute(0))
        );
        
        section.getChildren().addAll(sectionTitle, sessionsList);
        return section;
    }
    
    private HBox createSessionCard(String sessionType, String trainerName, LocalDateTime dateTime) {
        HBox card = new HBox(16);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        
        VBox dateBox = new VBox(4);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPrefWidth(60);
        dateBox.setStyle("-fx-background-color: -fx-primary-50; -fx-background-radius: 8; -fx-padding: 8;");
        
        Text month = new Text(dateTime.format(DateTimeFormatter.ofPattern("MMM")));
        month.getStyleClass().add("text-caption");
        month.setStyle("-fx-fill: -fx-primary-600; -fx-font-weight: 600;");
        
        Text day = new Text(dateTime.format(DateTimeFormatter.ofPattern("dd")));
        day.getStyleClass().add("text-h3");
        day.setStyle("-fx-fill: -fx-primary-600;");
        
        dateBox.getChildren().addAll(month, day);
        
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Text sessionName = new Text(sessionType);
        sessionName.getStyleClass().add("text-body");
        sessionName.setStyle("-fx-font-weight: 600;");
        
        HBox details = new HBox(12);
        Text trainer = new Text("👤 " + trainerName);
        trainer.getStyleClass().add("text-body-sm");
        Text time = new Text("🕐 " + dateTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        time.getStyleClass().add("text-body-sm");
        details.getChildren().addAll(trainer, time);
        
        info.getChildren().addAll(sessionName, details);
        
        VBox actions = new VBox(8);
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().addAll("danger-button", "button-small");
        actions.getChildren().add(cancelButton);
        
        card.getChildren().addAll(dateBox, info, actions);
        return card;
    }
    
    private void showBookingDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Session");
        alert.setHeaderText("Select available time slot");
        alert.setContentText("Session booking functionality coming soon!");
        alert.showAndWait();
    }
}
