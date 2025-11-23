/*
 * Block20 Gym Management System
 * Staff Portal - Top Navigation Bar Component
 */
package com.block20.components;

import com.block20.models.AppNotification;
import com.block20.services.NotificationService;

import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.Animation;

public class TopNavigation {
    
    private HBox rootView;
    private final String staffRole;
    private final Consumer<String> actionHandler;
    private final NotificationService notificationService;
    private Label unreadBadge;
    
    public TopNavigation(String staffRole, Consumer<String> actionHandler, NotificationService notificationService) {
        this.staffRole = staffRole;
        this.actionHandler = actionHandler;
        this.notificationService = notificationService;
        initializeView();
    }
    
private void initializeView() {
        rootView = new HBox(20);
        rootView.getStyleClass().add("top-nav");
        rootView.setAlignment(Pos.CENTER_LEFT);
        
        rootView.getChildren().addAll(createLogoArea(), createSpacer(), createActionsArea());
        
        // 1. Check immediately
        updateBadge();

        // 2. NEW: Check automatically every 2 seconds (Live Updates!)
        Timeline notificationTimer = new Timeline(
            new KeyFrame(Duration.seconds(2), e -> updateBadge())
        );
        notificationTimer.setCycleCount(Animation.INDEFINITE);
        notificationTimer.play();
    }
    
    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
    
    private HBox createLogoArea() {
        HBox logoArea = new HBox(12);
        logoArea.getStyleClass().add("logo-area");
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.setCursor(Cursor.HAND);
        
        Label logoText = new Label("BLOCK20");
        logoText.getStyleClass().add("logo-text");
        
        Label roleBadge = new Label(staffRole);
        roleBadge.getStyleClass().add("role-badge");
        
        logoArea.getChildren().addAll(logoText, roleBadge);
        logoArea.setOnMouseClicked(e -> handleAction("logo"));
        return logoArea;
    }
    
    private HBox createActionsArea() {
        HBox actionsArea = new HBox(8);
        actionsArea.setAlignment(Pos.CENTER_RIGHT);
        
        // 1. Smart Bell Button
        StackPane notificationContainer = new StackPane();
        
        Button bellBtn = createIconButton("🔔");
        bellBtn.setOnAction(e -> showNotificationDropdown(bellBtn));
        
        unreadBadge = new Label();
        unreadBadge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 10px; -fx-padding: 0 4;");
        unreadBadge.setTranslateX(8);
        unreadBadge.setTranslateY(-8);
        unreadBadge.setVisible(false);
        unreadBadge.setMouseTransparent(true); // <--- CRITICAL FIX: Let clicks pass through to button!
        
        notificationContainer.getChildren().addAll(bellBtn, unreadBadge);
        
        // 2. Other Buttons
        Button profileBtn = createIconButton("👤");
        profileBtn.setOnAction(e -> handleAction("profile"));
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setOnAction(e -> handleAction("logout"));
        
        actionsArea.getChildren().addAll(notificationContainer, profileBtn, logoutBtn);
        return actionsArea;
    }
    
    private void showNotificationDropdown(Button anchor) {
        System.out.println("DEBUG: Bell Clicked."); 
        
        ContextMenu popup = new ContextMenu();
        popup.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-width: 1px;");
        
        if (notificationService == null) {
            System.out.println("DEBUG: Service is NULL!");
            return;
        }

        List<AppNotification> alerts = notificationService.getNotifications();
        System.out.println("DEBUG: Found " + alerts.size() + " alerts.");
        
        if (alerts.isEmpty()) {
            Label empty = new Label("No notifications");
            empty.setPadding(new Insets(10));
            popup.getItems().add(new CustomMenuItem(empty));
        } else {
            for (AppNotification note : alerts) {
                VBox box = new VBox(2);
                box.setPadding(new Insets(8));
                box.setPrefWidth(250);
                
                Label t = new Label(note.getTitle());
                t.setStyle("-fx-font-weight: bold;");
                
                Text m = new Text(note.getMessage());
                m.setWrappingWidth(230);
                
                Label tm = new Label(note.getTimeFormatted());
                tm.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                
                box.getChildren().addAll(t, m, tm);
                
                // Highlight unread
                if (!note.isRead()) {
                    box.setStyle("-fx-background-color: #EFF6FF;");
                }
                
                // Mark as read logic
                note.markRead();
                
                CustomMenuItem item = new CustomMenuItem(box);
                item.setHideOnClick(false); 
                popup.getItems().add(item);
            }
        }
        
        popup.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
        popup.setOnHidden(e -> updateBadge());
    }
    
    private void updateBadge() {
        if (notificationService == null) return;
        int count = notificationService.getUnreadCount();
        unreadBadge.setText(String.valueOf(count));
        unreadBadge.setVisible(count > 0);
    }

    private Button createIconButton(String icon) {
        Button button = new Button(icon);
        button.getStyleClass().add("icon-button");
        return button;
    }
    
    private void handleAction(String action) {
        if (actionHandler != null) {
            actionHandler.accept(action);
        }
    }
    
    public HBox getView() {
        return rootView;
    }
}