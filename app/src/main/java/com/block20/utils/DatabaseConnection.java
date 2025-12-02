package com.block20.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final Path DB_PATH = resolveDatabasePath();
    private static final String URL = "jdbc:sqlite:" + DB_PATH.toString();

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("DB Connection Failed: " + e.getMessage());
            return null;
        }
    }

    private static Path resolveDatabasePath() {
        String configured = System.getenv("BLOCK20_DB_PATH");
        Path target = (configured != null && !configured.isBlank())
            ? Paths.get(configured)
            : locateProjectRoot().resolve("app").resolve("block20_gym.db");

        Path absolute = target.toAbsolutePath();
        ensureParentDirectory(absolute);
        return absolute;
    }

    private static Path locateProjectRoot() {
        Path current = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        for (int depth = 0; depth < 8 && current != null; depth++) {
            if (Files.exists(current.resolve("settings.gradle"))) {
                return current;
            }
            current = current.getParent();
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    private static void ensureParentDirectory(Path absolute) {
        try {
            Path parent = absolute.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (Exception e) {
            System.err.println("Unable to prepare database directory: " + e.getMessage());
        }
    }

public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. MEMBERS (Existing)
            String createMembers = """
                CREATE TABLE IF NOT EXISTS members (
                    member_id TEXT PRIMARY KEY, full_name TEXT, email TEXT UNIQUE, phone TEXT,
                    plan_type TEXT, status TEXT, join_date TEXT, expiry_date TEXT,
                    address TEXT, emergency_name TEXT, emergency_phone TEXT, emergency_relation TEXT
                );
            """;
            stmt.execute(createMembers);

            // 2. ATTENDANCE (Existing)
            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (visit_id TEXT PRIMARY KEY, member_id TEXT, member_name TEXT, check_in_time TEXT, check_out_time TEXT);");

            // 3. TRANSACTIONS (Existing)
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (transaction_id TEXT PRIMARY KEY, member_id TEXT, type TEXT, amount REAL, date TEXT);");

            // 4. EQUIPMENT (Existing)
            stmt.execute("CREATE TABLE IF NOT EXISTS equipment (equipment_id TEXT PRIMARY KEY, name TEXT, category TEXT, status TEXT, purchase_date TEXT);");

            // Enable FK constraints for this session
            stmt.execute("PRAGMA foreign_keys = ON;");

            // 5. AUDIT & NOTIFS (Existing)
            stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs (log_id TEXT PRIMARY KEY, target_id TEXT, action TEXT, details TEXT, timestamp TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (id TEXT PRIMARY KEY, title TEXT, message TEXT, timestamp TEXT, is_read INTEGER);");

            // 6. PAYMENTS - PLANS & RECEIPTS
            String createPaymentPlans = """
                CREATE TABLE IF NOT EXISTS payment_plans (
                    plan_id TEXT PRIMARY KEY,
                    member_id TEXT NOT NULL,
                    total_amount REAL NOT NULL,
                    created_on TEXT NOT NULL,
                    status TEXT NOT NULL
                );
            """;
            stmt.execute(createPaymentPlans);

            String createPlanInstallments = """
                CREATE TABLE IF NOT EXISTS payment_plan_installments (
                    installment_id TEXT PRIMARY KEY,
                    plan_id TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    amount REAL NOT NULL,
                    paid INTEGER NOT NULL,
                    paid_on TEXT,
                    FOREIGN KEY(plan_id) REFERENCES payment_plans(plan_id) ON DELETE CASCADE
                );
            """;
            stmt.execute(createPlanInstallments);

            String createGatewayReceipts = """
                CREATE TABLE IF NOT EXISTS payment_gateway_receipts (
                    receipt_id TEXT PRIMARY KEY,
                    member_id TEXT,
                    method TEXT,
                    amount REAL,
                    reference TEXT,
                    status TEXT,
                    message TEXT,
                    card_last4 TEXT,
                    created_at TEXT
                );
            """;
            stmt.execute(createGatewayReceipts);

            String createPaymentReceipts = """
                CREATE TABLE IF NOT EXISTS payment_receipts (
                    transaction_id TEXT PRIMARY KEY,
                    member_id TEXT NOT NULL,
                    description TEXT,
                    subtotal REAL NOT NULL,
                    tax_amount REAL NOT NULL,
                    method TEXT,
                    reference_code TEXT,
                    status TEXT,
                    processed_at TEXT NOT NULL
                );
            """;
            stmt.execute(createPaymentReceipts);

            // --- NEW: TRAINER TABLES (MATCHING YOUR PARTNER'S MODELS) ---

            // 7. TRAINERS (Updated to match Trainer.java 13 fields)
            String createTrainers = """
                CREATE TABLE IF NOT EXISTS trainers (
                    trainer_id TEXT PRIMARY KEY,
                    first_name TEXT,
                    last_name TEXT,
                    email TEXT,
                    phone TEXT,
                    specialization TEXT,
                    certification TEXT,
                    status TEXT,
                    hire_date TEXT,
                    sessions_per_month INTEGER,
                    active_clients INTEGER,
                    total_sessions INTEGER,
                    notes TEXT
                );
            """;
            stmt.execute(createTrainers);

            // 8. AVAILABILITY (Matching TrainerAvailabilitySlot.java)
            String createAvailability = """
                CREATE TABLE IF NOT EXISTS trainer_availability (
                    slot_id TEXT PRIMARY KEY,
                    trainer_id TEXT,
                    day_of_week TEXT,
                    start_time TEXT,
                    end_time TEXT,
                    FOREIGN KEY(trainer_id) REFERENCES trainers(trainer_id)
                );
            """;
            stmt.execute(createAvailability);

            // 9. SESSIONS (Matching TrainingSession.java)
            String createSessions = """
                CREATE TABLE IF NOT EXISTS training_sessions (
                    session_id TEXT PRIMARY KEY,
                    trainer_id TEXT,
                    trainer_name TEXT,
                    member_id TEXT,
                    member_name TEXT,
                    session_type TEXT,
                    session_date TEXT,
                    start_time TEXT,
                    duration_minutes INTEGER,
                    status TEXT,
                    notes TEXT
                );
            """;
            stmt.execute(createSessions);

            System.out.println("Database: All tables initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}