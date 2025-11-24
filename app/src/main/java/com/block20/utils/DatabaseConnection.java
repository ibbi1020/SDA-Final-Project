package com.block20.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:block20_gym.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("DB Connection Failed: " + e.getMessage());
            return null;
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

            // 5. AUDIT & NOTIFS (Existing)
            stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs (log_id TEXT PRIMARY KEY, target_id TEXT, action TEXT, details TEXT, timestamp TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (id TEXT PRIMARY KEY, title TEXT, message TEXT, timestamp TEXT, is_read INTEGER);");

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