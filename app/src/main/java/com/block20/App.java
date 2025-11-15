/*
 * Block20 Gym Management System
 * Main Application Entry Point
 */
package com.block20;

import com.block20.views.LoginGatewayView;
import com.block20.views.MemberPortalView;
import com.block20.views.StaffPortalView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for Block20 Gym Management System
 * Initializes and launches the JavaFX application with Login Gateway
 */
public class App extends Application {
    
    private Stage primaryStage;
    private Scene scene;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Show Login Gateway
        showLoginGateway();
        
        // Configure primary stage
        primaryStage.setTitle("Block20 - Gym Management System");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    /**
     * Show login gateway screen
     */
    private void showLoginGateway() {
        LoginGatewayView loginView = new LoginGatewayView(this::handleLogin);
        
        scene = new Scene(loginView, 1400, 900);
        
        // Load stylesheet
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
    }
    
    /**
     * Handle successful login
     * @param userType "Member" or "Staff"
     * @param userId User's ID
     */
    private void handleLogin(String userType, String userId) {
        System.out.println("Login successful: " + userType + " - " + userId);
        
        if (userType.equals("Member")) {
            showMemberPortal(userId);
        } else if (userType.equals("Staff")) {
            showStaffPortal(userId);
        }
    }
    
    /**
     * Show Member Portal
     */
    private void showMemberPortal(String memberId) {
        // Extract member name from ID for demo (in real app, would fetch from database)
        String memberName = "Member " + memberId;
        
        MemberPortalView memberPortal = new MemberPortalView(memberId, memberName, this::handleLogout);
        
        scene = new Scene(memberPortal.getView(), 1400, 900);
        
        // Load stylesheet
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
    }
    
    /**
     * Show Staff Portal
     */
    private void showStaffPortal(String staffId) {
        // Extract staff name from ID for demo (in real app, would fetch from database)
        String staffName = "Staff " + staffId;
        String staffRole = "STAFF";
        
        StaffPortalView staffPortal = new StaffPortalView(staffName, staffRole);
        
        scene = new Scene(staffPortal.getView(), 1400, 900);
        
        // Load stylesheet
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
    }
    
    /**
     * Handle logout - return to login gateway
     */
    private void handleLogout(String userId) {
        System.out.println("Logout: " + userId);
        showLoginGateway();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

