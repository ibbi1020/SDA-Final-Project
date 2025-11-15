/*
 * Block20 Gym Management System
 * Login Gateway - Entry point for Members and Staff
 */
package com.block20.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.util.function.BiConsumer;

/**
 * Login Gateway View
 * Provides toggle between Member and Staff login
 * No authentication logic - accepts any input for demonstration
 */
public class LoginGatewayView extends StackPane {
    
    private VBox mainContainer;
    private ToggleGroup loginTypeToggle;
    private TextField userIdField;
    private PasswordField passwordField;
    private BiConsumer<String, String> onLoginSuccess; // (userType, userId)
    
    public LoginGatewayView(BiConsumer<String, String> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        initializeView();
    }
    
    private void initializeView() {
        getStyleClass().add("root");
        
        mainContainer = new VBox(0);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setMaxWidth(480);
        
        mainContainer.getChildren().addAll(
            createLogoSection(),
            createLoginCard()
        );
        
        getChildren().add(mainContainer);
    }
    
    /**
     * Create logo and branding section
     */
    private VBox createLogoSection() {
        VBox logoSection = new VBox(12);
        logoSection.setAlignment(Pos.CENTER);
        logoSection.setPadding(new Insets(0, 0, 32, 0));
        
        // Logo/Icon
        Label logoIcon = new Label("🏋️");
        logoIcon.setStyle("-fx-font-size: 64px;");
        
        // Gym name
        Text gymName = new Text("BLOCK20");
        gymName.getStyleClass().add("text-h1");
        gymName.setStyle("-fx-fill: -fx-primary-500;");
        
        // Tagline
        Text tagline = new Text("Gym Management System");
        tagline.getStyleClass().add("text-body");
        tagline.setStyle("-fx-fill: -fx-gray-600;");
        
        logoSection.getChildren().addAll(logoIcon, gymName, tagline);
        return logoSection;
    }
    
    /**
     * Create login card with toggle and form
     */
    private VBox createLoginCard() {
        VBox card = new VBox(24);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(40));
        card.setMaxWidth(480);
        
        card.getChildren().addAll(
            createLoginTypeToggle(),
            createLoginForm(),
            createLoginButton(),
            createInfoFooter()
        );
        
        return card;
    }
    
    /**
     * Create toggle between Member and Staff login
     */
    private VBox createLoginTypeToggle() {
        VBox toggleSection = new VBox(12);
        toggleSection.setAlignment(Pos.CENTER);
        
        Text label = new Text("Login As");
        label.getStyleClass().add("text-body-sm");
        label.setStyle("-fx-fill: -fx-gray-600;");
        
        HBox toggleButtons = new HBox(0);
        toggleButtons.setAlignment(Pos.CENTER);
        toggleButtons.getStyleClass().add("toggle-group");
        
        loginTypeToggle = new ToggleGroup();
        
        ToggleButton memberToggle = new ToggleButton("Member");
        memberToggle.setToggleGroup(loginTypeToggle);
        memberToggle.setSelected(true);
        memberToggle.getStyleClass().addAll("toggle-button", "toggle-left");
        memberToggle.setPrefWidth(120);
        
        ToggleButton staffToggle = new ToggleButton("Staff");
        staffToggle.setToggleGroup(loginTypeToggle);
        staffToggle.getStyleClass().addAll("toggle-button", "toggle-right");
        staffToggle.setPrefWidth(120);
        
        toggleButtons.getChildren().addAll(memberToggle, staffToggle);
        
        toggleSection.getChildren().addAll(label, toggleButtons);
        return toggleSection;
    }
    
    /**
     * Create login form with ID and Password fields
     */
    private VBox createLoginForm() {
        VBox form = new VBox(16);
        
        // User ID field
        VBox userIdGroup = new VBox(8);
        Label userIdLabel = new Label("Member/Staff ID");
        userIdLabel.getStyleClass().add("text-body-sm");
        userIdLabel.setStyle("-fx-font-weight: 600;");
        
        userIdField = new TextField();
        userIdField.setPromptText("Enter your ID (e.g., M001 or S001)");
        userIdField.getStyleClass().add("text-input");
        userIdField.setPrefHeight(44);
        
        userIdGroup.getChildren().addAll(userIdLabel, userIdField);
        
        // Password field
        VBox passwordGroup = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("text-body-sm");
        passwordLabel.setStyle("-fx-font-weight: 600;");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("text-input");
        passwordField.setPrefHeight(44);
        
        // Handle Enter key press
        passwordField.setOnAction(e -> handleLogin());
        userIdField.setOnAction(e -> passwordField.requestFocus());
        
        passwordGroup.getChildren().addAll(passwordLabel, passwordField);
        
        form.getChildren().addAll(userIdGroup, passwordGroup);
        return form;
    }
    
    /**
     * Create login button
     */
    private Button createLoginButton() {
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().addAll("primary-button", "button-large");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(48);
        loginButton.setOnAction(e -> handleLogin());
        
        return loginButton;
    }
    
    /**
     * Create info footer
     */
    private VBox createInfoFooter() {
        VBox footer = new VBox(8);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16, 0, 0, 0));
        
        Text infoText = new Text("Demo Mode: Enter any ID and password to login");
        infoText.getStyleClass().add("text-caption");
        infoText.setStyle("-fx-fill: -fx-gray-500;");
        
        Text exampleText = new Text("Example: M001 (Member) or S001 (Staff)");
        exampleText.getStyleClass().add("text-caption");
        exampleText.setStyle("-fx-fill: -fx-gray-400;");
        
        footer.getChildren().addAll(infoText, exampleText);
        return footer;
    }
    
    /**
     * Handle login button click
     */
    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Basic validation
        if (userId.isEmpty() || password.isEmpty()) {
            showError("Please enter both ID and password");
            return;
        }
        
        // Determine user type from toggle
        ToggleButton selectedToggle = (ToggleButton) loginTypeToggle.getSelectedToggle();
        String userType = selectedToggle.getText(); // "Member" or "Staff"
        
        // No authentication - just redirect based on type
        if (onLoginSuccess != null) {
            onLoginSuccess.accept(userType, userId);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
