/*
 * Block20 Gym Management System
 * Member Profile Controller
 */
package com.block20.controllers.member;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class MemberProfileController extends ScrollPane {
    
    private VBox contentContainer;
    
    public MemberProfileController(String memberId) {
        initializeView();
    }
    
    private void initializeView() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.setPadding(new Insets(32));
        contentContainer.getStyleClass().add("main-content");
        
        Text title = new Text("My Profile");
        title.getStyleClass().add("text-h2");
        
        Text subtitle = new Text("Manage your personal information");
        subtitle.getStyleClass().add("text-muted");
        
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);
        
        grid.add(createFormField("Full Name", "John Doe"), 0, 0);
        grid.add(createFormField("Email", "john.doe@email.com"), 1, 0);
        grid.add(createFormField("Phone", "+1 (555) 123-4567"), 0, 1);
        grid.add(createFormField("Date of Birth", "Jan 15, 1990"), 1, 1);
        
        Text emergencyHeader = new Text("Emergency Contact");
        emergencyHeader.getStyleClass().add("text-h4");
        emergencyHeader.setStyle("-fx-padding: 16 0 0 0;");
        
        GridPane emergencyGrid = new GridPane();
        emergencyGrid.setHgap(20);
        emergencyGrid.setVgap(16);
        
        emergencyGrid.add(createFormField("Contact Name", "Jane Doe"), 0, 0);
        emergencyGrid.add(createFormField("Contact Phone", "+1 (555) 987-6543"), 1, 0);
        emergencyGrid.add(createFormField("Relationship", "Spouse"), 0, 1);
        
        Button editButton = new Button("Edit Profile");
        editButton.getStyleClass().addAll("primary-button");
        editButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Edit Profile");
            alert.setContentText("Profile editing functionality coming soon!");
            alert.showAndWait();
        });
        
        card.getChildren().addAll(grid, emergencyHeader, emergencyGrid, editButton);
        contentContainer.getChildren().addAll(title, subtitle, card);
        setContent(contentContainer);
    }
    
    private VBox createFormField(String label, String value) {
        VBox field = new VBox(4);
        field.setPrefWidth(250);
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-fill: -fx-gray-600;");
        
        Text valueText = new Text(value);
        valueText.getStyleClass().add("text-body");
        valueText.setStyle("-fx-font-weight: 600;");
        
        field.getChildren().addAll(labelText, valueText);
        return field;
    }
}
