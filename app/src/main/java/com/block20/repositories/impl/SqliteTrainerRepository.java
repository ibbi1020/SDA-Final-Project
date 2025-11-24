package com.block20.repositories.impl;

import com.block20.models.Trainer;
import com.block20.repositories.TrainerRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTrainerRepository implements TrainerRepository {

    @Override
    public void save(Trainer t) {
        String sql = """
            INSERT INTO trainers (
                trainer_id, first_name, last_name, email, phone, specialization, 
                certification, status, hire_date, sessions_per_month, active_clients, total_sessions, notes
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(trainer_id) DO UPDATE SET
                first_name=excluded.first_name,
                last_name=excluded.last_name,
                email=excluded.email,
                phone=excluded.phone,
                specialization=excluded.specialization,
                certification=excluded.certification,
                status=excluded.status,
                sessions_per_month=excluded.sessions_per_month,
                active_clients=excluded.active_clients,
                total_sessions=excluded.total_sessions,
                notes=excluded.notes;
        """;
        
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getTrainerId());
            stmt.setString(2, t.getFirstName());
            stmt.setString(3, t.getLastName());
            stmt.setString(4, t.getEmail());
            stmt.setString(5, t.getPhone());
            stmt.setString(6, t.getSpecialization());
            stmt.setString(7, t.getCertification());
            stmt.setString(8, t.getStatus());
            stmt.setString(9, t.getHireDate().toString());
            stmt.setInt(10, t.getSessionsPerMonth());
            stmt.setInt(11, t.getActiveClients());
            stmt.setInt(12, t.getTotalSessions());
            stmt.setString(13, t.getNotes());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public Optional<Trainer> findById(String trainerId) {
        String sql = "SELECT * FROM trainers WHERE trainer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Trainer> findAll() {
        return query("SELECT * FROM trainers");
    }

    @Override
    public List<Trainer> search(String keyword) {
        String search = "%" + keyword + "%";
        String sql = "SELECT * FROM trainers WHERE first_name LIKE ? OR last_name LIKE ? OR specialization LIKE ?";
        List<Trainer> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, search);
            stmt.setString(2, search);
            stmt.setString(3, search);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String trainerId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM trainers WHERE trainer_id = ?")) {
            stmt.setString(1, trainerId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void updateStatus(String trainerId, String status) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE trainers SET status = ? WHERE trainer_id = ?")) {
            stmt.setString(1, status);
            stmt.setString(2, trainerId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private List<Trainer> query(String sql) {
        List<Trainer> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Trainer mapRow(ResultSet rs) throws SQLException {
        return new Trainer(
            rs.getString("trainer_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("specialization"),
            rs.getString("certification"),
            rs.getString("status"),
            LocalDate.parse(rs.getString("hire_date")),
            rs.getInt("sessions_per_month"),
            rs.getInt("active_clients"),
            rs.getInt("total_sessions"),
            rs.getString("notes")
        );
    }
}