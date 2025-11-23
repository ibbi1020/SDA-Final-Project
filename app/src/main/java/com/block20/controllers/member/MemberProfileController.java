package com.block20.controllers.member;

import com.block20.models.Member;
import com.block20.services.MemberService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class MemberProfileController extends ScrollPane {

    private final String memberId;
    private final MemberService memberService;
    private MemberProfileData profileData;

    private VBox contentContainer;
    private TextField fullNameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField addressField;
    private DatePicker dobPicker;
    private TextField emergencyNameField;
    private TextField emergencyPhoneField;
    private TextField emergencyRelationshipField;
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;

    public MemberProfileController(String memberId, MemberService memberService) {
        this.memberId = memberId;
        this.memberService = memberService;
        this.profileData = loadProfileData(memberId);
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

        fullNameField = createTextField(profileData.fullName);
        emailField = createTextField(profileData.email);
        phoneField = createTextField(profileData.phone);
        addressField = createTextField(profileData.address);
        dobPicker = createDatePicker(profileData.dateOfBirth);

        grid.add(wrapField("Full Name", fullNameField), 0, 0);
        grid.add(wrapField("Email", emailField), 1, 0);
        grid.add(wrapField("Phone", phoneField), 0, 1);
        grid.add(wrapField("Address", addressField), 1, 1);
        grid.add(wrapField("Date of Birth", dobPicker), 0, 2);

        Text emergencyHeader = new Text("Emergency Contact");
        emergencyHeader.getStyleClass().add("text-h4");
        emergencyHeader.setStyle("-fx-padding: 16 0 0 0;");

        GridPane emergencyGrid = new GridPane();
        emergencyGrid.setHgap(20);
        emergencyGrid.setVgap(16);

        emergencyNameField = createTextField(profileData.emergencyName);
        emergencyPhoneField = createTextField(profileData.emergencyPhone);
        emergencyRelationshipField = createTextField(profileData.emergencyRelationship);

        emergencyGrid.add(wrapField("Contact Name", emergencyNameField), 0, 0);
        emergencyGrid.add(wrapField("Contact Phone", emergencyPhoneField), 1, 0);
        emergencyGrid.add(wrapField("Relationship", emergencyRelationshipField), 0, 1);

        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        editButton = new Button("Edit Profile");
        editButton.getStyleClass().add("primary-button");
        editButton.setOnAction(e -> setEditing(true));

        saveButton = new Button("Save Changes");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> saveProfileChanges());

        cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setOnAction(e -> {
            resetFields();
            setEditing(false);
        });

        buttonRow.getChildren().addAll(editButton, saveButton, cancelButton);

        card.getChildren().addAll(grid, emergencyHeader, emergencyGrid, buttonRow);
        contentContainer.getChildren().addAll(title, subtitle, card);
        setContent(contentContainer);
        setEditing(false);
    }

    private VBox wrapField(String label, Control input) {
        VBox field = new VBox(4);
        field.setPrefWidth(260);

        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-fill: -fx-gray-600;");

        input.setMaxWidth(Double.MAX_VALUE);
        field.getChildren().addAll(labelText, input);
        return field;
    }

    private TextField createTextField(String value) {
        TextField field = new TextField(value != null ? value : "");
        field.setPrefWidth(240);
        field.setEditable(false);
        return field;
    }

    private DatePicker createDatePicker(LocalDate value) {
        DatePicker picker = new DatePicker(value);
        picker.setEditable(false);
        return picker;
    }

    private void setEditing(boolean editing) {
        fullNameField.setEditable(editing);
        emailField.setEditable(editing);
        phoneField.setEditable(editing);
        addressField.setEditable(editing);
        dobPicker.setDisable(!editing);
        emergencyNameField.setEditable(editing);
        emergencyPhoneField.setEditable(editing);
        emergencyRelationshipField.setEditable(editing);

        editButton.setDisable(editing);
        saveButton.setDisable(!editing);
        cancelButton.setDisable(!editing);
    }

    private void resetFields() {
        fullNameField.setText(profileData.fullName);
        emailField.setText(profileData.email);
        phoneField.setText(profileData.phone);
        addressField.setText(profileData.address);
        dobPicker.setValue(profileData.dateOfBirth);
        emergencyNameField.setText(profileData.emergencyName);
        emergencyPhoneField.setText(profileData.emergencyPhone);
        emergencyRelationshipField.setText(profileData.emergencyRelationship);
    }

    private void saveProfileChanges() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        LocalDate dateOfBirth = dobPicker.getValue();
        String emergencyName = emergencyNameField.getText().trim();
        String emergencyPhone = emergencyPhoneField.getText().trim();
        String emergencyRelationship = emergencyRelationshipField.getText().trim();

        try {
            if (memberService != null && profileData.member != null) {
                memberService.updateMemberDetails(
                    profileData.member.getMemberId(),
                    fullName,
                    email,
                    phone,
                    address,
                    emergencyName,
                    emergencyPhone,
                    emergencyRelationship
                );
                profileData.member.setFullName(fullName);
                profileData.member.setEmail(email);
                profileData.member.setPhone(phone);
                profileData.member.setAddress(address);
                profileData.member.setEmergencyContactName(emergencyName);
                profileData.member.setEmergencyContactPhone(emergencyPhone);
                profileData.member.setEmergencyContactRelationship(emergencyRelationship);
            }

            profileData.fullName = fullName;
            profileData.email = email;
            profileData.phone = phone;
            profileData.address = address;
            profileData.dateOfBirth = dateOfBirth;
            profileData.emergencyName = emergencyName;
            profileData.emergencyPhone = emergencyPhone;
            profileData.emergencyRelationship = emergencyRelationship;

            setEditing(false);
            showAlert(Alert.AlertType.INFORMATION, "Profile Updated", "Your profile changes have been saved.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Unable to save profile", ex.getMessage());
        }
    }

    private MemberProfileData loadProfileData(String memberId) {
        MemberProfileData data = new MemberProfileData();
        data.memberId = memberId;
        data.dateOfBirth = LocalDate.of(1990, 1, 15);
        data.emergencyName = "Jane Doe";
        data.emergencyPhone = "+1 (555) 987-6543";
        data.emergencyRelationship = "Spouse";

        if (memberService != null) {
            Optional<Member> member = memberService.getAllMembers().stream()
                    .filter(m -> m.getMemberId().equalsIgnoreCase(memberId))
                    .findFirst();
            member.ifPresent(data::applyMember);
        }

        if (data.fullName == null) {
            data.fullName = "John Doe";
            data.email = "john.doe@email.com";
            data.phone = "+1 (555) 123-4567";
            data.address = "123 Main Street";
        }

        return data;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class MemberProfileData {
        private String memberId;
        private Member member;
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private LocalDate dateOfBirth;
        private String emergencyName;
        private String emergencyPhone;
        private String emergencyRelationship;

        private void applyMember(Member member) {
            this.member = member;
            this.fullName = member.getFullName();
            this.email = member.getEmail();
            this.phone = member.getPhone();
            this.address = member.getAddress() != null ? member.getAddress() : "";
            this.emergencyName = member.getEmergencyContactName() != null ? member.getEmergencyContactName() : "";
            this.emergencyPhone = member.getEmergencyContactPhone() != null ? member.getEmergencyContactPhone() : "";
            this.emergencyRelationship = member.getEmergencyContactRelationship() != null ? member.getEmergencyContactRelationship() : "";
        }
    }
}
