/*
 * Block20 Gym Management System
 * Staff Portal - Sidebar Navigation Component
 */
package com.block20.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Sidebar navigation component for Staff Portal
 * Provides role-based navigation menu with sections
 */
public class SidebarNavigation {
    
    private VBox rootView;
    private Map<String, HBox> navigationItems;
    private String activeItem;
    private Consumer<String> navigationHandler;
    
    public SidebarNavigation(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        this.navigationItems = new HashMap<>();
        initializeView();
    }
    
    /**
     * Initialize the sidebar view
     */
    private void initializeView() {
        rootView = new VBox();
        rootView.getStyleClass().add("sidebar");
        
        // Create scroll pane for navigation
        VBox navContent = new VBox();
        
        // Add navigation sections
        navContent.getChildren().addAll(
            createMainSection(),
            createMembersSection(),
            createEnrollmentSection(),
            createTrainersSection(),
            createEquipmentSection(),
            createReportsSection()
        );
        
        ScrollPane scrollPane = new ScrollPane(navContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        rootView.getChildren().add(scrollPane);
        
        // Set dashboard as initially active
        setActiveItem("dashboard");
    }
    
    /**
     * Create the main navigation section
     */
    private VBox createMainSection() {
        VBox section = new VBox(8);
        section.getStyleClass().add("sidebar-section");
        
        // Section label
        Label sectionLabel = new Label("MAIN");
        sectionLabel.getStyleClass().add("sidebar-section-label");
        
        // Navigation items
        section.getChildren().addAll(
            sectionLabel,
            createNavItem("dashboard", "🏠", "Dashboard")
        );
        
        return section;
    }
    
    /**
     * Create the members navigation section
     */
    private VBox createMembersSection() {
        VBox section = new VBox(8);
        section.getStyleClass().add("sidebar-section");
        
        // Section label
        Label sectionLabel = new Label("MEMBERS");
        sectionLabel.getStyleClass().add("sidebar-section-label");
        
        // Navigation items
        section.getChildren().addAll(
            sectionLabel,
            createNavItem("members-registry", "�", "Member Registry"),
            createNavItem("members-checkin", "⏱️", "Check-In/Out")
        );
        
        return section;
    }
    
    /**
     * Create the enrollment navigation section
     */
    private VBox createEnrollmentSection() {
        VBox section = new VBox(8);
        section.getStyleClass().add("sidebar-section");
        
        // Section label
        Label sectionLabel = new Label("ENROLLMENT & RENEWALS");
        sectionLabel.getStyleClass().add("sidebar-section-label");
        
        // Navigation items
        section.getChildren().addAll(
            sectionLabel,
            createNavItem("enrollment-new", "📝", "New Enrollment"),
            createNavItem("renewals", "🔄", "Renewals")
        );
        
        return section;
    }
    
    /**
     * Create the trainers navigation section
     */
    private VBox createTrainersSection() {
        VBox section = new VBox(8);
        section.getStyleClass().add("sidebar-section");
        
        // Section label
        Label sectionLabel = new Label("TRAINERS");
        sectionLabel.getStyleClass().add("sidebar-section-label");
        
        // Navigation items
        section.getChildren().addAll(
            sectionLabel,
            createNavItem("trainers-registry", "�", "Trainer Registry"),
            createNavItem("trainers-sessions", "🏋️", "Training Sessions")
        );
        
        return section;
    }
    
    /**
     * Create the equipment navigation section
     */
    private VBox createEquipmentSection() {
        VBox section = new VBox(8);
        section.getStyleClass().add("sidebar-section");
        
        // Section label
        Label sectionLabel = new Label("EQUIPMENT");
        sectionLabel.getStyleClass().add("sidebar-section-label");
        
        // Navigation items
        section.getChildren().addAll(
            sectionLabel,
            createNavItem("equipment-inventory", "📦", "Equipment Inventory"),
            createNavItem("equipment-maintenance", "🔧", "Maintenance Schedule")
        );
        
        return section;
    }
    
    /**
     * Create the reports navigation section
     */
    private VBox createReportsSection() {
        VBox section = new VBox(8);
        section.getStyleClass().add("sidebar-section");
        
        // Section label
        Label sectionLabel = new Label("REPORTS");
        sectionLabel.getStyleClass().add("sidebar-section-label");
        
        // Navigation items
        section.getChildren().addAll(
            sectionLabel,
            createNavItem("reports-financial", "💰", "Financial Reports"),
            createNavItem("reports-operational", "📈", "Operational Reports")
        );
        
        return section;
    }
    
    /**
     * Create a single navigation item
     */
    private HBox createNavItem(String id, String icon, String text) {
        HBox navItem = new HBox(12);
        navItem.getStyleClass().add("sidebar-link");
        navItem.setAlignment(Pos.CENTER_LEFT);
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("icon");
        
        // Text label
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("label");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textLabel, Priority.ALWAYS);
        
        navItem.getChildren().addAll(iconLabel, textLabel);
        
        // Store reference
        navigationItems.put(id, navItem);
        
        // Add click handler
        navItem.setOnMouseClicked(e -> handleNavigation(id));
        
        return navItem;
    }
    
    /**
     * Handle navigation item click
     */
    private void handleNavigation(String id) {
        setActiveItem(id);
        if (navigationHandler != null) {
            navigationHandler.accept(id);
        }
    }
    
    /**
     * Set the active navigation item
     */
    public void setActiveItem(String id) {
        // Remove active class from previous item
        if (activeItem != null && navigationItems.containsKey(activeItem)) {
            navigationItems.get(activeItem).getStyleClass().remove("active");
        }
        
        // Add active class to new item
        if (navigationItems.containsKey(id)) {
            navigationItems.get(id).getStyleClass().add("active");
            activeItem = id;
        }
    }
    
    /**
     * Get the root view
     */
    public VBox getView() {
        return rootView;
    }
}
