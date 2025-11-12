/*
 * Block20 Gym Management System
 * Staff Portal - Top Navigation Bar Component
 */
package com.block20.components;

import javafx.geometry.Pos;
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
    
    public TopNavigation(String staffRole) {
        this.staffRole = staffRole;
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
        
        // Logo text
        Label logoText = new Label("BLOCK20");
        logoText.getStyleClass().add("logo-text");
        
        // Role badge
        Label roleBadge = new Label(staffRole);
        roleBadge.getStyleClass().add("role-badge");
        
        logoArea.getChildren().addAll(logoText, roleBadge);
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
        notificationsBtn.setOnAction(e -> handleNotifications());
        
        // User profile button
        Button profileBtn = createIconButton("👤");
        profileBtn.setOnAction(e -> handleProfile());
        
        // Settings button
        Button settingsBtn = createIconButton("⚙️");
        settingsBtn.setOnAction(e -> handleSettings());
        
        actionsArea.getChildren().addAll(notificationsBtn, profileBtn, settingsBtn);
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
    
    /**
     * Handle notifications button click
     */
    private void handleNotifications() {
        System.out.println("Notifications clicked");
        // TODO: Show notifications panel
    }
    
    /**
     * Handle profile button click
     */
    private void handleProfile() {
        System.out.println("Profile clicked");
        // TODO: Show profile menu
    }
    
    /**
     * Handle settings button click
     */
    private void handleSettings() {
        System.out.println("Settings clicked");
        // TODO: Show settings menu
    }
    
    /**
     * Get the root view
     */
    public HBox getView() {
        return rootView;
    }
}
