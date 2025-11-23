package com.block20.repositories.impl;

import com.block20.models.AuditLog;
import com.block20.repositories.AuditRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteAuditRepository implements AuditRepository {

    @Override
    public void save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (log_id, target_id, action, details, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "LOG" + System.nanoTime()); // Simple ID generation
            stmt.setString(2, log.getTargetId());
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getDetails());
            stmt.setString(5, log.getTimestampFormatted()); // Storing as string for simplicity
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<AuditLog> findByTargetId(String targetId) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE target_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, targetId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Note: You might need to adjust your AuditLog constructor to accept a timestamp string if strict parsing is needed
                // For MVP/Student project, treating timestamp as a display string is often acceptable
                list.add(new AuditLog(
                    rs.getString("log_id"),
                    rs.getString("target_id"),
                    rs.getString("action"),
                    rs.getString("details")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}