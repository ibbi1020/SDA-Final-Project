/*
 * Block20 Gym Management System
 * Staff Portal - Top Navigation Bar Component
 */
package com.block20.components;

import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Top navigation bar component for Staff Portal
 * Displays logo, role badge, and action icons
 */
public class TopNavigation {
    
    private HBox rootView;
    private final String staffRole;
    private final Consumer<String> actionHandler;
    
    public TopNavigation(String staffRole) {
        this(staffRole, null);
    }
    
    public TopNavigation(String staffRole, Consumer<String> actionHandler) {
        this.staffRole = staffRole;
        this.actionHandler = actionHandler;
        initializeView();
    }
    
    /**
     * Initialize the top navigation bar
     */
    private void initializeView() {
        rootView = new HBox(20);
        rootView.getStyleClass().add("top-nav");
        rootView.setAlignment(Pos.CENTER_LEFT);
        
        // Left side - Logo area
        HBox logoArea = createLogoArea();
        
        // Center spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Right side - Action buttons
        HBox actionsArea = createActionsArea();
        
        rootView.getChildren().addAll(logoArea, spacer, actionsArea);
    }
    
    /**
     * Create the logo area with branding
     */
    private HBox createLogoArea() {
        HBox logoArea = new HBox(12);
        logoArea.getStyleClass().add("logo-area");
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setCursor(Cursor.HAND);
        
        // Logo text
        Label logoText = new Label("BLOCK20");
        logoText.getStyleClass().add("logo-text");
        
        // Role badge
        Label roleBadge = new Label(staffRole);
        roleBadge.getStyleClass().add("role-badge");
        
        logoArea.getChildren().addAll(logoText, roleBadge);
        logoArea.setOnMouseClicked(e -> handleAction("logo"));
        return logoArea;
    }
    
    /**
     * Create the actions area with icon buttons
     */
    private HBox createActionsArea() {
        HBox actionsArea = new HBox(8);
        actionsArea.setAlignment(Pos.CENTER_RIGHT);
        
        // Notifications button
        Button notificationsBtn = createIconButton("🔔");
        notificationsBtn.setOnAction(e -> handleAction("notifications"));
        
        // User profile button
        Button profileBtn = createIconButton("👤");
        profileBtn.setOnAction(e -> handleAction("profile"));
        
        // Settings button
        Button settingsBtn = createIconButton("⚙️");
        settingsBtn.setOnAction(e -> handleAction("settings"));
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setOnAction(e -> handleAction("logout"));
        logoutBtn.setMinHeight(36);
        
        actionsArea.getChildren().addAll(notificationsBtn, profileBtn, settingsBtn, logoutBtn);
        return actionsArea;
    }
    
    /**
     * Create an icon button
     */
    private Button createIconButton(String icon) {
        Button button = new Button(icon);
        button.getStyleClass().add("icon-button");
        return button;
    }
    
    private void handleAction(String action) {
        if (actionHandler != null) {
            actionHandler.accept(action);
        } else {
            System.out.println("Top nav action: " + action);
        }
    }
    
    /**
     * Get the root view
     */
    public HBox getView() {
        return rootView;
    }
}
