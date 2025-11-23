/*
 * Block20 Gym Management System
 * Member Portal - Main View Container
 */
package com.block20.views;

import com.block20.controllers.member.*;
import com.block20.services.MemberService;
import com.block20.services.PaymentService;
import com.block20.services.TrainerScheduleService;
import com.block20.services.TrainerService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.util.function.Consumer;

/**
 * Member Portal View - Main Container
 * Navigation: Dashboard, Membership, Training, Payments, Attendance, Profile
 */
public class MemberPortalView {
    
    private BorderPane rootView;
    private VBox sidebar;
    private StackPane contentArea;
    private String memberId;
    private String memberName;
    private Consumer<String> onLogout;
    private final MemberService memberService;
    private final PaymentService paymentService;
    private final TrainerService trainerService;
    private final TrainerScheduleService trainerScheduleService;
    
    public MemberPortalView(String memberId,
                            String memberName,
                            Consumer<String> onLogout,
                            MemberService memberService,
                            PaymentService paymentService,
                            TrainerService trainerService,
                            TrainerScheduleService trainerScheduleService) {
        this.memberId = memberId;
        this.memberName = memberName != null ? memberName : "Member";
        this.onLogout = onLogout;
        this.memberService = memberService;
        this.paymentService = paymentService;
        this.trainerService = trainerService;
        this.trainerScheduleService = trainerScheduleService;
        initializeView();
    }
    
    private void initializeView() {
        rootView = new BorderPane();
        rootView.getStyleClass().add("root");
        
        // Top navigation bar
        rootView.setTop(createTopNav());
        
        // Left sidebar
        sidebar = createSidebar();
        rootView.setLeft(sidebar);
        
        // Content area
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        rootView.setCenter(contentArea);
        
        // Show dashboard by default
        handleNavigation("dashboard");
    }
    
    /**
     * Create top navigation bar
     */
    private HBox createTopNav() {
        HBox topNav = new HBox(16);
        topNav.getStyleClass().add("top-nav");
        topNav.setAlignment(Pos.CENTER_LEFT);
        topNav.setPadding(new Insets(12, 24, 12, 24));
        
        // Logo area
        HBox logoArea = new HBox(12);
        logoArea.setAlignment(Pos.CENTER_LEFT);
        logoArea.getStyleClass().add("logo-area");
        logoArea.setCursor(Cursor.HAND);
        
        Label logo = new Label("🏋️");
        logo.setStyle("-fx-font-size: 24px;");
        
        Text logoText = new Text("BLOCK20");
        logoText.getStyleClass().add("logo-text");
        logoText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label memberBadge = new Label("MEMBER");
        memberBadge.getStyleClass().add("role-badge");
        memberBadge.setStyle("-fx-background-color: -fx-success-500;");
        
        logoArea.getChildren().addAll(logo, logoText, memberBadge);
        logoArea.setOnMouseClicked(e -> handleNavigation("dashboard"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Member info and logout
        HBox rightSection = new HBox(16);
        rightSection.setAlignment(Pos.CENTER_RIGHT);
        
        Text memberNameText = new Text(memberName);
        memberNameText.getStyleClass().add("text-body");
        memberNameText.setStyle("-fx-fill: -fx-gray-700; -fx-font-weight: 600;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("secondary-button");
        logoutButton.setOnAction(e -> {
            if (onLogout != null) {
                onLogout.accept(memberId);
            }
        });
        
        rightSection.getChildren().addAll(memberNameText, logoutButton);
        
        topNav.getChildren().addAll(logoArea, spacer, rightSection);
        return topNav;
    }
    
    /**
     * Create sidebar navigation
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);
        sidebar.setPadding(new Insets(24, 0, 24, 0));
        
        sidebar.getChildren().addAll(
            createSidebarLink("📊", "Dashboard", "dashboard", true),
            createSidebarLink("💳", "Membership", "membership", false),
            createSidebarLink("🏃", "Training Sessions", "training", false),
            createSidebarLink("💰", "Payments & Billing", "payments", false),
            createSidebarLink("📅", "Attendance History", "attendance", false),
            createSidebarLink("👤", "My Profile", "profile", false)
        );
        
        return sidebar;
    }
    
    /**
     * Create sidebar navigation link
     */
    private Button createSidebarLink(String icon, String label, String destination, boolean active) {
        Button link = new Button();
        link.getStyleClass().add("sidebar-link");
        if (active) {
            link.getStyleClass().add("active");
        }
        link.setPrefWidth(Double.MAX_VALUE);
        link.setAlignment(Pos.CENTER_LEFT);
        
        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("icon");
        iconLabel.setStyle("-fx-font-size: 18px;");
        
        Label textLabel = new Label(label);
        textLabel.getStyleClass().add("label");
        
        content.getChildren().addAll(iconLabel, textLabel);
        link.setGraphic(content);
        
        link.setOnAction(e -> {
            // Remove active class from all links
            sidebar.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    node.getStyleClass().remove("active");
                }
            });
            // Add active class to clicked link
            link.getStyleClass().add("active");
            // Navigate
            handleNavigation(destination);
        });
        
        return link;
    }
    
    /**
     * Handle navigation to different sections
     */
    private void handleNavigation(String destination) {
        System.out.println("Member navigation to: " + destination);
        
        switch (destination) {
            case "dashboard":
                showDashboard();
                break;
            case "membership":
                showMembership();
                break;
            case "training":
                showTraining();
                break;
            case "payments":
                showPayments();
                break;
            case "attendance":
                showAttendance();
                break;
            case "profile":
                showProfile();
                break;
            default:
                showPlaceholder(destination);
        }
    }
    
    /**
     * Show member dashboard
     */
    private void showDashboard() {
        MemberDashboardController dashboard = new MemberDashboardController(memberId, memberName, this::handleNavigation);
        setContent(dashboard);
    }
    
    /**
     * Show membership management (renewals, plan info)
     */
    private void showMembership() {
        MemberMembershipController membershipController = new MemberMembershipController(memberId, this::handleNavigation);
        setContent(membershipController);
    }
    
    /**
     * Show training sessions (book, view upcoming)
     */
    private void showTraining() {
        MemberTrainingController trainingController = new MemberTrainingController(
                memberId,
                memberName,
                trainerService,
                trainerScheduleService,
                this::handleNavigation
        );
        setContent(trainingController);
    }
    
    /**
     * Show payments and billing
     */
    private void showPayments() {
        MemberPaymentsController paymentsController = new MemberPaymentsController(memberId, paymentService);
        setContent(paymentsController);
    }
    
    /**
     * Show attendance history
     */
    private void showAttendance() {
        MemberAttendanceController attendanceController = new MemberAttendanceController(memberId);
        setContent(attendanceController);
    }
    
    /**
     * Show member profile
     */
    private void showProfile() {
        MemberProfileController profileController = new MemberProfileController(memberId, memberService);
        setContent(profileController);
    }
    
    /**
     * Show placeholder for screens under construction
     */
    private void showPlaceholder(String screenName) {
        VBox placeholder = new VBox(20);
        placeholder.getStyleClass().add("content-area");
        placeholder.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(screenName);
        titleLabel.getStyleClass().add("text-h1");
        
        Label subtitleLabel = new Label("This screen is under construction");
        subtitleLabel.getStyleClass().add("text-body");
        
        placeholder.getChildren().addAll(titleLabel, subtitleLabel);
        setContent(placeholder);
    }
    
    /**
     * Set content area to new view
     */
    private void setContent(Region view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
        VBox.setVgrow(view, Priority.ALWAYS);
    }
    
    /**
     * Get root view
     */
    public BorderPane getView() {
        return rootView;
    }
}
