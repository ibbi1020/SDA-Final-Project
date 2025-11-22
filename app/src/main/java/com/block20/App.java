/*
 * Block20 Gym Management System
 * Main Application Entry Point
 */
package com.block20;

// IMPORTS (Pointing to the new files)
import com.block20.repositories.MemberRepository;
import com.block20.repositories.impl.MemberRepositoryImpl; // <--- New File
import com.block20.services.MemberService;
import com.block20.services.impl.MemberServiceImpl;       // <--- New File
import com.block20.views.LoginGatewayView;
import com.block20.views.MemberPortalView;
import com.block20.views.StaffPortalView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.block20.repositories.AttendanceRepository;
import com.block20.repositories.impl.AttendanceRepositoryImpl;
import com.block20.repositories.TransactionRepository;
import com.block20.repositories.impl.TransactionRepositoryImpl;
import com.block20.repositories.EquipmentRepository;
import com.block20.repositories.impl.EquipmentRepositoryImpl;
import com.block20.services.EquipmentService;
import com.block20.services.impl.EquipmentServiceImpl;
import com.block20.services.AuditService;
import com.block20.services.impl.AuditServiceImpl;
import com.block20.repositories.AuditRepository;
import com.block20.repositories.impl.AuditRepositoryImpl;

public class App extends Application {
    
    private Stage primaryStage;
    private Scene scene;
    private MemberService memberService; 
    private EquipmentService equipmentService;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // ============================================================
        // INITIALIZE BACKEND (Using the Clean Architecture)
        // ============================================================
        
    // 1. Create Repositories (The "Databases")
        MemberRepository memberRepo = new MemberRepositoryImpl();
        AttendanceRepository attendanceRepo = new AttendanceRepositoryImpl(); // <--- NEW!
        TransactionRepository transactionRepo = new TransactionRepositoryImpl(); // <--- NEW
        EquipmentRepository equipmentRepo = new EquipmentRepositoryImpl();
        AuditRepository auditRepo = new AuditRepositoryImpl();

    // 2. Services
        AuditService auditService = new AuditServiceImpl(auditRepo);
        this.memberService = new MemberServiceImpl(memberRepo, attendanceRepo, transactionRepo, auditService);
        this.equipmentService = new EquipmentServiceImpl(equipmentRepo); // <--- NEW
        // 3. Add Mock Data
        try {
            memberService.registerMember("John Doe", "john@example.com", "555-0101", "Premium");
            memberService.registerMember("Jane Smith", "jane@example.com", "555-0102", "Basic");
            
            // Add a fake check-in for John Doe so we can see history immediately
            // (We need to find him first to get his generated ID)
            com.block20.models.Member john = memberRepo.findByEmail("john@example.com");
            if (john != null) {
                memberService.checkInMember(john.getMemberId());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    // ... (Keep your existing navigation methods: showLoginGateway, handleLogin, etc.) ...
    // ... (Paste them here if you deleted them by accident) ...
    
    private void showLoginGateway() {
        LoginGatewayView loginView = new LoginGatewayView(this::handleLogin);
        scene = new Scene(loginView, 1400, 900);
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }
    
    private void handleLogin(String userType, String userId) {
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
        
        // PASS BOTH SERVICES HERE
        StaffPortalView staffPortal = new StaffPortalView(
            staffName, 
            staffRole, 
            this.memberService, 
            this.equipmentService // <--- NEW ARGUMENT
        );
        
        scene = new Scene(staffPortal.getView(), 1400, 900);
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
    }
    
    private void handleLogout(String userId) {
        showLoginGateway();
    }

    public static void main(String[] args) {
        launch(args);
    }
}