/*
 * Block20 Gym Management System
 * Staff Portal - Main Layout
 */
package com.block20.views;

import com.block20.services.MemberService;
import com.block20.services.ExportService;
import com.block20.services.PaymentService;
import com.block20.services.TrainerScheduleService;
import com.block20.services.TrainerService;
import com.block20.components.SidebarNavigation;
import com.block20.components.TopNavigation;
import com.block20.controllers.FinancialReportsController;
import com.block20.controllers.OperationalReportsController;
import com.block20.controllers.enrollment.EnrollmentController;
import com.block20.controllers.equipment.EquipmentInventoryController;
import com.block20.controllers.equipment.MaintenanceScheduleController;
import com.block20.controllers.members.CheckInController;
import com.block20.controllers.members.MemberRegistryController;
import com.block20.controllers.renewals.RenewalsController;
import com.block20.controllers.staff.StaffDashboardController;
import com.block20.controllers.trainers.TrainerRegistryController;
import com.block20.controllers.trainers.TrainingSessionsController;
import com.block20.services.EquipmentService;
import com.block20.services.NotificationService;
import java.util.function.Consumer;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Main layout for the Staff Portal
 * Manages the overall structure with top nav, sidebar, and content area
 */
public class StaffPortalView {

    private BorderPane rootView;
    private VBox contentArea;
    private SidebarNavigation sidebar;
    private TopNavigation topNav;

    private final String staffId;
    private final String staffName;
    private final String staffRole;
    private final Consumer<String> logoutHandler;

    private final MemberService memberService;
    private final PaymentService paymentService;
    private final EquipmentService equipmentService;
    private final ExportService exportService;
    private final TrainerService trainerService;
    private final NotificationService notificationService;
    private final TrainerScheduleService trainerScheduleService;

    public StaffPortalView(String staffId,
            String staffName,
            String staffRole,
            MemberService memberService,
            PaymentService paymentService,
            EquipmentService equipmentService,
            ExportService exportService,
            TrainerService trainerService,
            TrainerScheduleService trainerScheduleService,
            Consumer<String> logoutHandler,
            NotificationService notificationService) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffRole = staffRole;
        this.memberService = memberService;
        this.paymentService = paymentService;
        this.equipmentService = equipmentService;
        this.exportService = exportService;
        this.trainerService = trainerService;
        this.trainerScheduleService = trainerScheduleService;
        this.logoutHandler = logoutHandler;
        this.notificationService = notificationService;
        initializeView();
    }

    /**
     * Initialize the main portal view
     */
    private void initializeView() {
        rootView = new BorderPane();
        rootView.getStyleClass().add("main-container");

        // Create top navigation
        topNav = new TopNavigation(staffRole, this::handleNavigation, this.notificationService);
        rootView.setTop(topNav.getView());

        // Create sidebar navigation
        sidebar = new SidebarNavigation(this::handleNavigation);
        rootView.setLeft(sidebar.getView());

        // Create content area
        contentArea = new VBox();
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        rootView.setCenter(contentArea);

        // Load default dashboard view
        showDashboard();
    }

    /**
     * Handle navigation events from sidebar
     */
    private void handleNavigation(String navItem) {
        System.out.println("Navigation to: " + navItem);

        switch (navItem) {
            // --- FIXED: Added Missing TopNav Cases ---
            case "logout":
                if (logoutHandler != null) {
                    logoutHandler.accept(staffId);
                }
                break;
            case "logo":
                showDashboard();
                break;
            case "profile":
                Alert p = new Alert(Alert.AlertType.INFORMATION);
                p.setTitle("Profile"); p.setHeaderText(null);
                p.setContentText(String.format("Signed in as %s (%s)", staffName, staffRole));
                p.showAndWait();
                break;
            // -----------------------------------------

            case "dashboard":
                showDashboard();
                break;
            case "members-registry":
                showMembersRegistry();
                break;
            case "members-checkin":
                showCheckIn();
                break;
            case "enrollment-new":
                showEnrollmentNew();
                break;
            case "renewals":
                showRenewals();
                break;
            case "trainers-registry":
                showTrainersRegistry();
                break;
            case "trainers-sessions":
                showTrainersSessions();
                break;
            case "equipment-inventory":
                showEquipmentInventory();
                break;
            case "equipment-maintenance":
                showEquipmentMaintenance();
                break;
            case "reports-financial":
                showReportsFinancial();
                break;
            case "reports-operational":
                showReportsOperational();
                break;
            default:
                // If the bell is NOT handled internally by TopNavigation, it might send "notifications" here.
                // We ignore it here because the Smart Bell should handle itself.
                if (!navItem.equals("notifications") && !navItem.equals("settings")) {
                    showPlaceholder(navItem);
                }
        }
    }

    private void showDashboard() {
        // Pass BOTH services
        StaffDashboardController dashboard = new StaffDashboardController(
                staffName,
                memberService,
                equipmentService,
                this::handleNavigation);
        setContent(dashboard);
    }

    /**
     * Show check-in/check-out view
     */
    private void showCheckIn() {
        // PASS THE SERVICE
        CheckInController checkInController = new CheckInController(this.memberService);
        setContent(checkInController);
    }

    /**
     * Show member registry view (combines search, create, attendance)
     */
    private void showMembersRegistry() {
        // Pass the service (the "Brain") to the controller
        MemberRegistryController memberRegistry = new MemberRegistryController(this::handleNavigation,
                this.memberService);
        setContent(memberRegistry);
    }

    /**
     * Show new enrollment view
     */
    private void showEnrollmentNew() {
        // PASS 'this.memberService' into the constructor
        EnrollmentController enrollmentController = new EnrollmentController(
                this::handleNavigation,
                this.memberService,
                this.paymentService);
        setContent(enrollmentController);
    }

    /**
     * Show renewals view (pending renewals + renewal processing)
     */
    private void showRenewals() {
        // PASS THE SERVICE
        RenewalsController renewalsController = new RenewalsController(this::handleNavigation, this.memberService);
        setContent(renewalsController.getView()); // Note: check if your controller extends Region or has getView()
    }

    /**
     * Show trainer registry view (combines register and manage trainers)
     */
    private void showTrainersRegistry() {
        TrainerRegistryController trainerRegistryController = new TrainerRegistryController(this::handleNavigation,
                this.trainerService);
        setContent(trainerRegistryController);
    }

    /**
     * Show training sessions view (combines view sessions and schedule)
     */
    private void showTrainersSessions() {
        TrainingSessionsController trainingSessionsController = new TrainingSessionsController(
                this::handleNavigation,
                this.trainerService,
                this.trainerScheduleService);
        setContent(trainingSessionsController);
    }

    /**
     * Show equipment inventory view (includes add equipment)
     */
    private void showEquipmentInventory() {
        // Pass the service to the controller
        EquipmentInventoryController controller = new EquipmentInventoryController(this::handleNavigation,
                this.equipmentService);
        setContent(controller);
    }

    /**
     * Show maintenance schedule view
     */
    private void showEquipmentMaintenance() {
        MaintenanceScheduleController maintenanceScheduleController = new MaintenanceScheduleController(
                this::handleNavigation);
        setContent(maintenanceScheduleController);
    }

    /**
     * Show financial reports view
     */
    private void showReportsFinancial() {
        // PASS THE SERVICE
        FinancialReportsController financialReportsController = new FinancialReportsController(this::handleNavigation,
                this.memberService, this.exportService);
        setContent(financialReportsController); // Add .getView() if your controller requires it
    }

    private void showReportsOperational() {
        // PASS THE SERVICE
        OperationalReportsController controller = new OperationalReportsController(this::handleNavigation,
                this.memberService);
        setContent(controller);
    }

    /**
     * Show a placeholder view for screens not yet implemented
     */
    private void showPlaceholder(String screenName) {
        VBox placeholder = new VBox(20);
        placeholder.getStyleClass().add("content-area");
        placeholder.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label(screenName);
        titleLabel.getStyleClass().add("text-h1");

        Label subtitleLabel = new Label("This screen is under construction");
        subtitleLabel.getStyleClass().add("text-body");

        Label instructionLabel = new Label("Click on 'Dashboard' in the sidebar to return");
        instructionLabel.getStyleClass().add("text-caption");

        placeholder.getChildren().addAll(titleLabel, subtitleLabel, instructionLabel);
        setContent(placeholder);
    }

    /**
     * Set the content area to a new view
     */
    private void setContent(javafx.scene.layout.Region view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
        VBox.setVgrow(view, Priority.ALWAYS);
    }

    /**
     * Get the root view
     */
    public BorderPane getView() {
        return rootView;
    }

    private void handleTopNavAction(String action) {
        switch (action) {
            case "logo":
                showDashboard();
                break;
            case "logout":
                if (logoutHandler != null) {
                    logoutHandler.accept(staffId);
                } else {
                    showTopNavMessage("Logout", "Logout handler not wired yet.");
                }
                break;
            case "notifications":
                showTopNavMessage("Notifications",
                        "You're all caught up. We'll alert you when something needs attention.");
                break;
            case "profile":
                showTopNavMessage("Profile", String.format("Signed in as %s (%s)", staffName, staffRole));
                break;
            case "settings":
                showTopNavMessage("Settings", "Staff settings are under construction.");
                break;
            default:
                showTopNavMessage("Action", "Feature coming soon.");
        }
    }

    private void showTopNavMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
