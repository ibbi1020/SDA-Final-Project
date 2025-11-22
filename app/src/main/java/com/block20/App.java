/*
 * Block20 Gym Management System
 * Main Application Entry Point
 */
package com.block20;

// --- IMPORTS ---
import com.block20.repositories.*;
import com.block20.repositories.impl.*;
import com.block20.services.*;
import com.block20.services.impl.*;
import com.block20.views.LoginGatewayView;
import com.block20.views.MemberPortalView;
import com.block20.views.StaffPortalView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    
    private Stage primaryStage;
    private Scene scene;
    
    // Services
    private MemberService memberService; 
    private EquipmentService equipmentService;
    private ExportService exportService;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // ============================================================
        // INITIALIZE BACKEND (Clean Architecture)
        // ============================================================
        
        // 1. Create Repositories
        MemberRepository memberRepo = new MemberRepositoryImpl();
        AttendanceRepository attendanceRepo = new AttendanceRepositoryImpl();
        TransactionRepository transactionRepo = new TransactionRepositoryImpl();
        EquipmentRepository equipmentRepo = new EquipmentRepositoryImpl();
        AuditRepository auditRepo = new AuditRepositoryImpl();

        // 2. Create Services
        AuditService auditService = new AuditServiceImpl(auditRepo);
        NotificationService notificationService = new NotificationServiceImpl();
        this.exportService = new ExportServiceImpl(); 

        // 3. Inject Dependencies into Main Services
        this.memberService = new MemberServiceImpl(memberRepo, attendanceRepo, transactionRepo, auditService, notificationService);
        this.equipmentService = new EquipmentServiceImpl(equipmentRepo);

        // 4. Add Mock Data (UPDATED TO PASS VALIDATION)
        try {
            // FIX: Phone numbers must be valid (XXX-XXX-XXXX)
            memberService.registerMember("John Doe", "john@example.com", "555-123-0101", "Premium");
            memberService.registerMember("Jane Smith", "jane@example.com", "555-123-0102", "Basic");
            
            // Add fake check-in
            com.block20.models.Member john = memberRepo.findByEmail("john@example.com");
            if (john != null) {
                memberService.checkInMember(john.getMemberId());
            }
            
            System.out.println("Backend System Started: Mock members loaded.");
            
        } catch (Exception e) {
            System.err.println("Error loading mock members: " + e.getMessage());
            e.printStackTrace();
        }

        // 5. Add Equipment Data
        try {
            equipmentService.addEquipment("Treadmill T-1000", "Cardio", "Functional");
            equipmentService.addEquipment("Dumbbell Set (5-50lbs)", "Strength", "Functional");
            equipmentService.addEquipment("Elliptical E-500", "Cardio", "Maintenance");
            System.out.println("Equipment System Started: Dummy inventory added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // ============================================================

        showLoginGateway();
        
        primaryStage.setTitle("Block20 - Gym Management System");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    // --- NAVIGATION METHODS ---

    private void showLoginGateway() {
        LoginGatewayView loginView = new LoginGatewayView(this::handleLogin);
        scene = new Scene(loginView, 1400, 900);
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }
    
    private void handleLogin(String userType, String userId) {
        System.out.println("Login successful: " + userType + " - " + userId);
        if (userType.equals("Member")) {
            showMemberPortal(userId);
        } else if (userType.equals("Staff")) {
            showStaffPortal(userId);
        }
    }
    
    private void showMemberPortal(String memberId) {
        String memberName = "Member " + memberId;
        MemberPortalView memberPortal = new MemberPortalView(memberId, memberName, this::handleLogout);
        scene = new Scene(memberPortal.getView(), 1400, 900);
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }
    
    private void showStaffPortal(String staffId) {
        String staffName = "Staff " + staffId;
        String staffRole = "STAFF";
        
        // PASS ALL 3 SERVICES
        StaffPortalView staffPortal = new StaffPortalView(
            staffName, 
            staffRole, 
            this.memberService, 
            this.equipmentService,
            this.exportService
        );
        
        scene = new Scene(staffPortal.getView(), 1400, 900);
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }
    
    private void handleLogout(String userId) {
        System.out.println("Logout: " + userId);
        showLoginGateway();
    }

    public static void main(String[] args) {
        launch(args);
    }
}