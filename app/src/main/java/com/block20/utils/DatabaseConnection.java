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

            // 1. MEMBERS TABLE (Already done)
            String createMembers = """
                        CREATE TABLE IF NOT EXISTS members (
                            member_id TEXT PRIMARY KEY, full_name TEXT, email TEXT UNIQUE, phone TEXT,
                            plan_type TEXT, status TEXT, join_date TEXT, expiry_date TEXT,
                            address TEXT, emergency_name TEXT, emergency_phone TEXT, emergency_relation TEXT
                        );
                    """;
            stmt.execute(createMembers);

            // 2. ATTENDANCE TABLE (New)
            String createAttendance = """
                        CREATE TABLE IF NOT EXISTS attendance (
                            visit_id TEXT PRIMARY KEY,
                            member_id TEXT,
                            member_name TEXT,
                            check_in_time TEXT,
                            check_out_time TEXT
                        );
                    """;
            stmt.execute(createAttendance);

            // 3. TRANSACTIONS TABLE (New)
            String createTransactions = """
                        CREATE TABLE IF NOT EXISTS transactions (
                            transaction_id TEXT PRIMARY KEY,
                            member_id TEXT,
                            type TEXT,
                            amount REAL,
                            date TEXT
                        );
                    """;
            stmt.execute(createTransactions);

            // 4. EQUIPMENT TABLE (New)
            String createEquipment = """
                        CREATE TABLE IF NOT EXISTS equipment (
                            equipment_id TEXT PRIMARY KEY,
                            name TEXT,
                            category TEXT,
                            status TEXT,
                            purchase_date TEXT
                        );
                    """;
            stmt.execute(createEquipment);
            // 5. AUDIT LOGS
            String createAudit = """
                        CREATE TABLE IF NOT EXISTS audit_logs (
                            log_id TEXT PRIMARY KEY,
                            target_id TEXT,
                            action TEXT,
                            details TEXT,
                            timestamp TEXT
                        );
                    """;
            stmt.execute(createAudit);

            // 6. NOTIFICATIONS
            String createNotifs = """
                        CREATE TABLE IF NOT EXISTS notifications (
                            id TEXT PRIMARY KEY,
                            title TEXT,
                            message TEXT,
                            timestamp TEXT,
                            is_read INTEGER -- 0 for false, 1 for true
                        );
                    """;
            stmt.execute(createNotifs);
            
            System.out.println("Database: All tables initialized.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}