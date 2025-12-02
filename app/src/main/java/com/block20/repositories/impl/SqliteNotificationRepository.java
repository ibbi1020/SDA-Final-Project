package com.block20.repositories.impl;

import com.block20.models.AppNotification;
import com.block20.repositories.NotificationRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqliteNotificationRepository implements NotificationRepository {

    @Override
    public void save(AppNotification n) {
        String sql = "INSERT INTO notifications (id, title, message, timestamp, is_read) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String id = n.getId() != null ? n.getId() : UUID.randomUUID().toString();
            stmt.setString(1, id);
            stmt.setString(2, n.getTitle());
            stmt.setString(3, n.getMessage());
            stmt.setString(4, n.getTimestamp().toString());
            stmt.setInt(5, n.isRead() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<AppNotification> getRecentNotifications() {
        List<AppNotification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY timestamp DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDateTime ts = null;
                String rawTimestamp = rs.getString("timestamp");
                if (rawTimestamp != null) {
                    try {
                        ts = LocalDateTime.parse(rawTimestamp);
                    } catch (DateTimeParseException ignored) {
                        // Legacy rows stored only HH:mm; fall back to now so UI still works
                        ts = LocalDateTime.now();
                    }
                }
                boolean isRead = rs.getInt("is_read") == 1;
                AppNotification n = new AppNotification(
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("message"),
                    ts,
                    isRead
                );
                list.add(n);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int getUnreadCount() {
        String sql = "SELECT COUNT(*) FROM notifications WHERE is_read = 0";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public void markAsRead(String notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}