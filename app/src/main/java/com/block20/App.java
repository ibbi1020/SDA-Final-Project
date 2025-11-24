/*
 * Block20 Gym Management System
 * Main Application Entry Point
 */
package com.block20;

import com.block20.models.Member;
import com.block20.models.Trainer;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class App extends Application {
    
    private Stage primaryStage;
    private Scene scene;
    
    // Services
    private MemberService memberService; 
    private EquipmentService equipmentService;
    private ExportService exportService;
    private TrainerService trainerService;
    private TrainerScheduleService trainerScheduleService;
    private PaymentService paymentService;
    private NotificationService notificationService;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // 0. Initialize Database (Creates block20_gym.db if missing)
        com.block20.utils.DatabaseConnection.initializeDatabase();

        // ============================================================
        // 1. INITIALIZE REPOSITORIES (All SQLite)
        // ============================================================
        MemberRepository memberRepo = new SqliteMemberRepository();
        AttendanceRepository attendanceRepo = new SqliteAttendanceRepository();
        TransactionRepository transactionRepo = new SqliteTransactionRepository();
        EquipmentRepository equipmentRepo = new SqliteEquipmentRepository();
        TrainerRepository trainerRepo = new SqliteTrainerRepository();
        TrainerAvailabilityRepository trainerAvailRepo = new SqliteTrainerAvailabilityRepository();
        TrainingSessionRepository trainingSessionRepo = new SqliteTrainingSessionRepository();
        AuditRepository auditRepo = new SqliteAuditRepository();
        NotificationRepository notifRepo = new SqliteNotificationRepository();
        
        // Keep in-memory for now (Low priority)
        PaymentPlanRepository paymentPlanRepo = new PaymentPlanRepositoryImpl();

        // ============================================================
        // 2. INITIALIZE SERVICES
        // ============================================================
        AuditService auditService = new AuditServiceImpl(auditRepo);
        this.notificationService = new NotificationServiceImpl(notifRepo); // Class var
        this.exportService = new ExportServiceImpl();                      // Class var
        PaymentGateway paymentGateway = new MockPaymentGateway();

        // Inject Dependencies
        this.memberService = new MemberServiceImpl(memberRepo, attendanceRepo, transactionRepo, auditService, notificationService);
        this.equipmentService = new EquipmentServiceImpl(equipmentRepo);
        this.trainerService = new TrainerServiceImpl(trainerRepo);
        this.trainerScheduleService = new TrainerScheduleServiceImpl(trainerService, trainerAvailRepo, trainingSessionRepo);
        this.paymentService = new PaymentServiceImpl(transactionRepo, paymentPlanRepo, paymentGateway);

        // ============================================================
        // 3. SEED ALL DATA
        // ============================================================
        seedCompleteDatabase();

        // ============================================================
        // 4. LAUNCH UI
        // ============================================================
        showLoginGateway();
        
        primaryStage.setTitle("Block20 - Gym Management System");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
/**
     * Master function to populate the database tables independently.
     */
    private void seedCompleteDatabase() {
        System.out.println("--- CHECKING DATABASE SEEDS ---");

        // 1. Seed Members (Only if empty)
        if (memberService.getAllMembers().isEmpty()) {
            System.out.println("Seeding Members...");
            try {
                // Pakistani Mock Data
                Member bilal = memberService.registerMember("Bilal Ahmed", "bilal.ahmed@nust.edu.pk", "300-123-4567", "Student", "H-12 Campus, Islamabad", "Ahmed Khan", "300-987-6543", "Father");
                Member ayesha = memberService.registerMember("Ayesha Malik", "ayesha.m@gmail.com", "321-555-7890", "Premium", "DHA Phase 6, Lahore", "Saad Malik", "321-555-0000", "Spouse");
                Member hamza = memberService.registerMember("Hamza Khan", "hamza.beast@hotmail.com", "333-444-5555", "Elite", "Clifton, Karachi", "Kamran Khan", "333-111-2222", "Brother");
                Member zainab = memberService.registerMember("Zainab Bibi", "zainab.b@outlook.com", "345-678-9012", "Basic", "Satellite Town, Rawalpindi", "Omar Farooq", "345-000-1111", "Cousin");

                // Seed Attendance History
                memberService.checkInMember(bilal.getMemberId()); // Active Now
                
                // Historical visit for Ayesha
                memberService.checkInMember(ayesha.getMemberId());
                memberService.checkOutMember(ayesha.getMemberId());
                
                System.out.println("✅ Members & Attendance Seeded.");
            } catch (Exception e) { 
                System.err.println("Error seeding members: " + e.getMessage());
            }
        } else {
            System.out.println("Skipping Members (Already exist).");
        }

        // 2. Seed Equipment (Only if empty)
        if (equipmentService.getInventory().isEmpty()) {
            System.out.println("Seeding Equipment...");
            try {
                equipmentService.addEquipment("Treadmill T-1000", "Cardio", "Functional");
                equipmentService.addEquipment("Dumbbell Set (5-50lbs)", "Strength", "Functional");
                equipmentService.addEquipment("Elliptical E-500", "Cardio", "Maintenance");
                equipmentService.addEquipment("Squat Rack Pro", "Strength", "Functional");
                System.out.println("✅ Equipment Seeded.");
            } catch (Exception e) {
                System.err.println("Error seeding equipment: " + e.getMessage());
            }
        } else {
            System.out.println("Skipping Equipment (Already exist).");
        }

        // 3. Seed Trainers (Only if empty)
        if (trainerService.getAllTrainers().isEmpty()) {
            System.out.println("Seeding Trainers...");
            try {
                trainerService.registerTrainer("Mike", "Johnson", "mike@block20.com", "555-111-2222", "Strength", "ACE-CPT", LocalDate.now().minusYears(2), "Head Coach");
                trainerService.registerTrainer("Sarah", "Williams", "sarah@block20.com", "555-333-4444", "Yoga", "RYT-200", LocalDate.now().minusYears(1), "Yoga Expert");
                trainerService.registerTrainer("David", "Chen", "david@block20.com", "555-555-6666", "HIIT", "CF-L2", LocalDate.now().minusMonths(6), "CrossFit Coach");
                
                // Seed Availability for these trainers
                List<Trainer> trainers = trainerService.getAllTrainers();
                for (Trainer t : trainers) {
                    // Add slots: Mon/Wed 9am-5pm
                    trainerScheduleService.addAvailabilitySlot(t.getTrainerId(), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
                    trainerScheduleService.addAvailabilitySlot(t.getTrainerId(), DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
                }
                System.out.println("✅ Trainers & Schedule Seeded.");
            } catch (Exception e) {
                System.err.println("Error seeding trainers: " + e.getMessage());
            }
        } else {
            System.out.println("Skipping Trainers (Already exist).");
        }
    }
    // --- NAVIGATION METHODS (Keep exactly as they were) ---

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
        // Try to find real name
        com.block20.models.Member m = memberService.getMemberById(memberId);
        if (m != null) memberName = m.getFullName();

        MemberPortalView memberPortal = new MemberPortalView(
            memberId,
            memberName,
            this::handleLogout,
            this.memberService,
            this.paymentService,
            this.trainerService,
            this.trainerScheduleService
        );
        scene = new Scene(memberPortal.getView(), 1400, 900);
        String css = getClass().getResource("/com/block20/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }
    
    private void showStaffPortal(String staffId) {
        String staffName = "Staff " + staffId;
        String staffRole = "STAFF";
        
        StaffPortalView staffPortal = new StaffPortalView(
            staffId,
            staffName, 
            staffRole, 
            this.memberService, 
            this.paymentService,
            this.equipmentService,
            this.exportService,
            this.trainerService,
            this.trainerScheduleService,
            this::handleLogout,
            this.notificationService
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