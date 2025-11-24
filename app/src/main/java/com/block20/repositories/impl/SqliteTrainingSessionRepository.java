package com.block20.repositories.impl;

import com.block20.models.TrainingSession;
import com.block20.repositories.TrainingSessionRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTrainingSessionRepository implements TrainingSessionRepository {

    @Override
    public void save(TrainingSession s) {
        String sql = """
            INSERT INTO training_sessions (
                session_id, trainer_id, trainer_name, member_id, member_name, 
                session_type, session_date, start_time, duration_minutes, status, notes
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(session_id) DO UPDATE SET 
                status=excluded.status, notes=excluded.notes;
        """;
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getSessionId());
            stmt.setString(2, s.getTrainerId());
            stmt.setString(3, s.getTrainerName());
            stmt.setString(4, s.getMemberId());
            stmt.setString(5, s.getMemberName());
            stmt.setString(6, s.getSessionType());
            stmt.setString(7, s.getSessionDate().toString());
            stmt.setString(8, s.getStartTime().toString());
            stmt.setInt(9, s.getDurationMinutes());
            stmt.setString(10, s.getStatus());
            stmt.setString(11, s.getNotes());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<TrainingSession> findAll() {
        return query("SELECT * FROM training_sessions");
    }

    @Override
    public Optional<TrainingSession> findById(String sessionId) {
        String sql = "SELECT * FROM training_sessions WHERE session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<TrainingSession> findByTrainerAndDate(String trainerId, LocalDate date) {
        String sql = "SELECT * FROM training_sessions WHERE trainer_id = ? AND session_date = ?";
        List<TrainingSession> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trainerId);
            stmt.setString(2, date.toString());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<TrainingSession> findByMemberAndDate(String memberId, LocalDate date) {
        String sql = "SELECT * FROM training_sessions WHERE member_id = ? AND session_date = ?";
        List<TrainingSession> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.setString(2, date.toString());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String sessionId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM training_sessions WHERE session_id = ?")) {
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private List<TrainingSession> query(String sql) {
        List<TrainingSession> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private TrainingSession mapRow(ResultSet rs) throws SQLException {
        return new TrainingSession(
            rs.getString("session_id"),
            rs.getString("trainer_id"),
            rs.getString("trainer_name"),
            rs.getString("member_id"),
            rs.getString("member_name"),
            rs.getString("session_type"),
            LocalDate.parse(rs.getString("session_date")),
            LocalTime.parse(rs.getString("start_time")),
            rs.getInt("duration_minutes"),
            rs.getString("status"),
            rs.getString("notes")
        );
    }
}