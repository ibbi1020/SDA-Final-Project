/*
 * Block20 Gym Management System
 * Main Application Entry Point
 */
package com.block20;

import com.block20.views.StaffPortalView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for Block20 Gym Management System
 * Initializes and launches the JavaFX application
 */
public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // For now, directly load Staff Portal with hardcoded user
        // TODO: Implement login screen and authentication
        String staffName = "Sarah Johnson";
        String staffRole = "STAFF";
        
        // Create Staff Portal view
        StaffPortalView staffPortal = new StaffPortalView(staffName, staffRole);
        
        // Create scene
        Scene scene = new Scene(staffPortal.getView(), 1400, 900);
        
        // Load stylesheet
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        // Configure primary stage
        primaryStage.setTitle("Block20 - Gym Management System");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
