/*
 * Block20 Gym Management System
 * Member Profile Controller - Real Data Integration
 */
package com.block20.controllers.member;

import com.block20.models.Member;
import com.block20.services.MemberService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberProfileController extends ScrollPane {

    private final String memberId;
    private final MemberService memberService;
    
    // UI Fields
    private TextField fullNameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField addressField;
    private DatePicker dobPicker; // Note: Usually DOB is immutable, but we'll allow view
    private TextField emergencyNameField;
    private TextField emergencyPhoneField;
    private TextField emergencyRelationshipField;
    
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;
    
    private VBox contentContainer;

    public MemberProfileController(String memberId, MemberService memberService) {
        this.memberId = memberId;
        this.memberService = memberService;
        initializeView();
        loadRealData(); // Load data on startup
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

        // --- Personal Info Grid ---
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);

        fullNameField = createTextField("");
        emailField = createTextField("");
        phoneField = createTextField("");
        addressField = createTextField("");
        dobPicker = new DatePicker(); 
        dobPicker.setEditable(false); dobPicker.setDisable(true); // DOB usually read-only

        grid.add(wrapField("Full Name", fullNameField), 0, 0);
        grid.add(wrapField("Email", emailField), 1, 0);
        grid.add(wrapField("Phone", phoneField), 0, 1);
        grid.add(wrapField("Address", addressField), 1, 1);
        grid.add(wrapField("Date of Birth", dobPicker), 0, 2);

        // --- Emergency Contact Section ---
        Text emergencyHeader = new Text("Emergency Contact");
        emergencyHeader.getStyleClass().add("text-h4");
        emergencyHeader.setStyle("-fx-padding: 16 0 0 0;");

        GridPane emergencyGrid = new GridPane();
        emergencyGrid.setHgap(20);
        emergencyGrid.setVgap(16);

        emergencyNameField = createTextField("");
        emergencyPhoneField = createTextField("");
        emergencyRelationshipField = createTextField("");

        emergencyGrid.add(wrapField("Contact Name", emergencyNameField), 0, 0);
        emergencyGrid.add(wrapField("Contact Phone", emergencyPhoneField), 1, 0);
        emergencyGrid.add(wrapField("Relationship", emergencyRelationshipField), 0, 1);

        // --- Buttons ---
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        editButton = new Button("Edit Profile");
        editButton.getStyleClass().add("btn-primary");
        editButton.setOnAction(e -> setEditing(true));

        saveButton = new Button("Save Changes");
        saveButton.getStyleClass().add("btn-success");
        saveButton.setOnAction(e -> saveProfileChanges());

        cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("btn-secondary");
        cancelButton.setOnAction(e -> {
            loadRealData(); // Revert changes
            setEditing(false);
        });

        buttonRow.getChildren().addAll(editButton, saveButton, cancelButton);

        card.getChildren().addAll(grid, new Separator(), emergencyHeader, emergencyGrid, new Separator(), buttonRow);
        contentContainer.getChildren().addAll(title, subtitle, card);
        setContent(contentContainer);
        
        setEditing(false); // Default to view mode
    }

    private void loadRealData() {
        Member member = memberService.getMemberById(memberId);
        if (member == null) {
            System.err.println("Member not found: " + memberId);
            return;
        }

        // Populate fields safely (handle nulls)
        fullNameField.setText(member.getFullName());
        emailField.setText(member.getEmail());
        phoneField.setText(member.getPhone());
        addressField.setText(member.getAddress() != null ? member.getAddress() : "");
        
        // Note: If DOB isn't in your Member model yet, this might be null. 
        // Assuming you added it or parsed it from joinDate for now.
        dobPicker.setValue(member.getJoinDate()); // Using Join Date as proxy if DOB missing in model
        
        emergencyNameField.setText(member.getEmergencyContactName() != null ? member.getEmergencyContactName() : "");
        emergencyPhoneField.setText(member.getEmergencyContactPhone() != null ? member.getEmergencyContactPhone() : "");
        emergencyRelationshipField.setText(member.getEmergencyContactRelationship() != null ? member.getEmergencyContactRelation() : "");
    }

    private void saveProfileChanges() {
        try {
            // 1. Validate Logic is in Service
            
            // 2. Call Service to Update
            memberService.updateFullProfile(
                memberId,
                fullNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                addressField.getText(),
                emergencyNameField.getText(),
                emergencyPhoneField.getText(),
                emergencyRelationshipField.getText()
            );

            // 3. Refresh UI
            setEditing(false);
            loadRealData(); // Confirm saved data
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Profile Updated");
            alert.setContentText("Your changes have been saved successfully.");
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void setEditing(boolean editing) {
        fullNameField.setEditable(editing);
        // emailField.setEditable(editing); // Usually email changes require verification, disabling for safety
        phoneField.setEditable(editing);
        addressField.setEditable(editing);
        
        emergencyNameField.setEditable(editing);
        emergencyPhoneField.setEditable(editing);
        emergencyRelationshipField.setEditable(editing);

        editButton.setVisible(!editing);
        editButton.setManaged(!editing);
        
        saveButton.setVisible(editing);
        saveButton.setManaged(editing);
        
        cancelButton.setVisible(editing);
        cancelButton.setManaged(editing);
        
        // Visual cue
        if(editing) fullNameField.getParent().getParent().setStyle("-fx-background-color: #F8FAFC; -fx-padding: 24;");
        else fullNameField.getParent().getParent().setStyle("-fx-background-color: white; -fx-padding: 24;");
    }

    private VBox wrapField(String label, Control input) {
        VBox field = new VBox(4);
        Label labelText = new Label(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-text-fill: #64748B;");
        input.setPrefWidth(250);
        field.getChildren().addAll(labelText, input);
        return field;
    }

    private TextField createTextField(String value) {
        TextField field = new TextField(value);
        field.setEditable(false);
        field.getStyleClass().add("form-input");
        return field;
    }
}