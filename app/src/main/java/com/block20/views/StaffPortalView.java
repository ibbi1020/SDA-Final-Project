/*
 * Block20 Gym Management System
 * Staff Portal - Main Layout
 */
package com.block20.views;

import com.block20.services.MemberService;
import com.block20.components.SidebarNavigation;
import com.block20.components.TopNavigation;
import com.block20.controllers.staff.StaffDashboardController;
import com.block20.controllers.members.MemberRegistryController;
import com.block20.controllers.members.CheckInController;
import com.block20.controllers.enrollment.EnrollmentController;
import com.block20.controllers.renewals.RenewalsController;
import com.block20.controllers.trainers.TrainerRegistryController;
import com.block20.controllers.trainers.TrainingSessionsController;
import com.block20.controllers.equipment.EquipmentInventoryController;
import com.block20.controllers.equipment.MaintenanceScheduleController;
import com.block20.controllers.FinancialReportsController;
import com.block20.controllers.OperationalReportsController;
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
    
    private final String staffName;
    private final String staffRole;
    
    private final MemberService memberService; 
    
    public StaffPortalView(String staffName, String staffRole, MemberService memberService) {
        this.staffName = staffName;
        this.staffRole = staffRole;
        this.memberService = memberService;
        initializeView();
    }
    
    /**
     * Initialize the main portal view
     */
    private void initializeView() {
        rootView = new BorderPane();
        rootView.getStyleClass().add("main-container");
        
        // Create top navigation
        topNav = new TopNavigation(staffRole);
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
                showPlaceholder(navItem);
        }
    }
    
    /**
     * Show the dashboard view
     */
    private void showDashboard() {
        StaffDashboardController dashboard = new StaffDashboardController(staffName);
        setContent(dashboard);
    }
    
    /**
     * Show check-in/check-out view
     */
    private void showCheckIn() {
        CheckInController checkInController = new CheckInController();
        setContent(checkInController);
    }
    
    /**
     * Show member registry view (combines search, create, attendance)
     */
private void showMembersRegistry() {
    // Pass the service (the "Brain") to the controller
    MemberRegistryController memberRegistry = new MemberRegistryController(this::handleNavigation, this.memberService);
    setContent(memberRegistry);
}
    
    /**
     * Show new enrollment view
     */
    private void showEnrollmentNew() {
        EnrollmentController enrollmentController = new EnrollmentController(this::handleNavigation);
        setContent(enrollmentController);
    }
    
    /**
     * Show renewals view (pending renewals + renewal processing)
     */
    private void showRenewals() {
        RenewalsController renewalsController = new RenewalsController(this::handleNavigation);
        setContent(renewalsController.getView());
    }
    
    /**
     * Show trainer registry view (combines register and manage trainers)
     */
    private void showTrainersRegistry() {
        TrainerRegistryController trainerRegistryController = new TrainerRegistryController(this::handleNavigation);
        setContent(trainerRegistryController);
    }
    
    /**
     * Show training sessions view (combines view sessions and schedule)
     */
    private void showTrainersSessions() {
        TrainingSessionsController trainingSessionsController = new TrainingSessionsController(this::handleNavigation);
        setContent(trainingSessionsController);
    }
    
    /**
     * Show equipment inventory view (includes add equipment)
     */
    private void showEquipmentInventory() {
        EquipmentInventoryController equipmentInventoryController = new EquipmentInventoryController(this::handleNavigation);
        setContent(equipmentInventoryController);
    }
    
    /**
     * Show maintenance schedule view
     */
    private void showEquipmentMaintenance() {
        MaintenanceScheduleController maintenanceScheduleController = new MaintenanceScheduleController(this::handleNavigation);
        setContent(maintenanceScheduleController);
    }
    
    /**
     * Show financial reports view
     */
    private void showReportsFinancial() {
        FinancialReportsController financialReportsController = new FinancialReportsController(this::handleNavigation);
        setContent(financialReportsController);
    }
    
    /**
     * Show operational reports view
     */
    private void showReportsOperational() {
        OperationalReportsController operationalReportsController = new OperationalReportsController(this::handleNavigation);
        setContent(operationalReportsController);
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
}
