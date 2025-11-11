/*
 * Block20 Gym Management System
 * Main Application Entry Point
 */
package com.block20;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create welcome label
        Label label = new Label("Welcome to Block20 Gym Management System");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Create root layout
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);
        
        // Configure primary stage
        primaryStage.setTitle("Block20 - Gym Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
