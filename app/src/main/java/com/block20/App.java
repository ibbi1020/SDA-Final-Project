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
    private TrainerService trainerService;
    private TrainerScheduleService trainerScheduleService;
    private PaymentService paymentService;
    private NotificationService notificationService;
    
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
        TrainerRepository trainerRepo = new TrainerRepositoryImpl();
        PaymentPlanRepository paymentPlanRepository = new PaymentPlanRepositoryImpl();
        AuditRepository auditRepo = new AuditRepositoryImpl();
        TrainerAvailabilityRepository trainerAvailabilityRepo = new TrainerAvailabilityRepositoryImpl();
        TrainingSessionRepository trainingSessionRepo = new TrainingSessionRepositoryImpl();
        NotificationRepository notifRepo = new NotificationRepositoryImpl();

        // 2. Create Services
        AuditService auditService = new AuditServiceImpl(auditRepo);
        this.notificationService = new NotificationServiceImpl(notifRepo);
        this.exportService = new ExportServiceImpl(); 
        PaymentGateway paymentGateway = new MockPaymentGateway();

        // 3. Inject Dependencies into Main Services
        this.memberService = new MemberServiceImpl(memberRepo, attendanceRepo, transactionRepo, auditService, notificationService);
        this.paymentService = new PaymentServiceImpl(transactionRepo, paymentPlanRepository, paymentGateway);
        this.equipmentService = new EquipmentServiceImpl(equipmentRepo);
        this.trainerService = new TrainerServiceImpl(trainerRepo);
        this.trainerScheduleService = new TrainerScheduleServiceImpl(trainerService, trainerAvailabilityRepo, trainingSessionRepo);

        // 4. Add Mock Data (UPDATED TO PASS VALIDATION)
        try {
            // FIX: Phone numbers must be valid (XXX-XXX-XXXX)
            memberService.registerMember(
                "John Doe",
                "john@example.com",
                "555-123-0101",
                "Premium",
                "10 Wellness Way",
                "Jane Doe",
                "555-321-0001",
                "Spouse"
            );
            memberService.registerMember(
                "Jane Smith",
                "jane@example.com",
                "555-123-0102",
                "Basic",
                "200 Strength St",
                "Tom Smith",
                "555-888-0002",
                "Brother"
            );
            
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

        // 6. Seed Trainer Data (temporary mock data)
        seedTrainerData();
        seedTrainerScheduleData();
        
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
        
        // PASS ALL 3 SERVICES
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
        System.out.println("Logout: " + userId);
        showLoginGateway();
    }

    private void seedTrainerData() {
        try {
            if (trainerService.getAllTrainers().isEmpty()) {
                trainerService.registerTrainer("Mike", "Johnson", "mike.johnson@block20gym.com", "555-111-2222", "Personal Training", "ACE-CPT", java.time.LocalDate.of(2022, 1, 15), "Lead strength coach");
                trainerService.registerTrainer("Sarah", "Williams", "sarah.williams@block20gym.com", "555-333-4444", "Yoga", "RYT-200", java.time.LocalDate.of(2021, 6, 20), "Morning yoga specialist");
                trainerService.registerTrainer("David", "Chen", "david.chen@block20gym.com", "555-555-6666", "CrossFit", "CF-L2", java.time.LocalDate.of(2020, 3, 10), "CrossFit and HIIT");
            }
        } catch (Exception e) {
            System.err.println("Error seeding trainers: " + e.getMessage());
        }
    }

    private void seedTrainerScheduleData() {
        if (trainerScheduleService == null) {
            return;
        }
        try {
            if (!trainerScheduleService.getAllSessions().isEmpty()) {
                return;
            }
            java.util.List<com.block20.models.Trainer> trainers = trainerService.getAllTrainers();
            java.util.List<java.time.DayOfWeek> defaultDays = java.util.List.of(java.time.DayOfWeek.MONDAY, java.time.DayOfWeek.WEDNESDAY, java.time.DayOfWeek.FRIDAY);
            for (com.block20.models.Trainer trainer : trainers) {
                if (!trainerScheduleService.getAvailabilityForTrainer(trainer.getTrainerId()).isEmpty()) {
                    continue;
                }
                for (java.time.DayOfWeek day : defaultDays) {
                    trainerScheduleService.addAvailabilitySlot(trainer.getTrainerId(), day, java.time.LocalTime.of(8, 0), java.time.LocalTime.of(12, 0));
                    trainerScheduleService.addAvailabilitySlot(trainer.getTrainerId(), day, java.time.LocalTime.of(13, 0), java.time.LocalTime.of(18, 0));
                }
            }

            if (!trainers.isEmpty()) {
                java.time.LocalDate today = java.time.LocalDate.now();
                java.time.LocalDate nextMonday = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.MONDAY));
                java.time.LocalDate nextWednesday = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.WEDNESDAY));
                java.time.LocalDate nextFriday = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY));

                trainerScheduleService.scheduleSession("M1001", "John Smith", trainers.get(0).getTrainerId(), "Personal Training", nextMonday, java.time.LocalTime.of(9, 0), 60, "Strength assessment");
                trainerScheduleService.scheduleSession("M1002", "Sarah Johnson", trainers.get(1 % trainers.size()).getTrainerId(), "Yoga Session", nextWednesday, java.time.LocalTime.of(10, 0), 60, "Morning flow");
                trainerScheduleService.scheduleSession("M1003", "Mike Chen", trainers.get(2 % trainers.size()).getTrainerId(), "CrossFit Training", nextFriday, java.time.LocalTime.of(11, 0), 45, "WOD prep");
            }
        } catch (Exception e) {
            System.err.println("Error seeding trainer schedule: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}