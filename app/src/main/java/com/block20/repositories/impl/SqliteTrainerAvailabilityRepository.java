package com.block20.repositories.impl;

import com.block20.models.TrainerAvailabilitySlot;
import com.block20.repositories.TrainerAvailabilityRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SqliteTrainerAvailabilityRepository implements TrainerAvailabilityRepository {

    @Override
    public void save(TrainerAvailabilitySlot slot) {
        String sql = "INSERT INTO trainer_availability (slot_id, trainer_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT(slot_id) DO UPDATE SET start_time=excluded.start_time, end_time=excluded.end_time";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slot.getSlotId());
            stmt.setString(2, slot.getTrainerId());
            stmt.setString(3, slot.getDayOfWeek().name());
            stmt.setString(4, slot.getStartTime().toString());
            stmt.setString(5, slot.getEndTime().toString());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<TrainerAvailabilitySlot> findByTrainer(String trainerId) {
        String sql = "SELECT * FROM trainer_availability WHERE trainer_id = ?";
        return query(sql, trainerId);
    }

    // --- NEW METHOD: Required by Interface ---
    @Override
    public List<TrainerAvailabilitySlot> findByTrainerAndDay(String trainerId, DayOfWeek day) {
        String sql = "SELECT * FROM trainer_availability WHERE trainer_id = ? AND day_of_week = ?";
        List<TrainerAvailabilitySlot> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trainerId);
            stmt.setString(2, day.name()); // Convert Enum to String
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String slotId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM trainer_availability WHERE slot_id = ?")) {
            stmt.setString(1, slotId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    // Removed @Override tag here because the Interface likely doesn't have it
    public List<TrainerAvailabilitySlot> findAll() {
        List<TrainerAvailabilitySlot> list = new ArrayList<>();
        String sql = "SELECT * FROM trainer_availability";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Helper to avoid code duplication
    private List<TrainerAvailabilitySlot> query(String sql, String param) {
        List<TrainerAvailabilitySlot> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private TrainerAvailabilitySlot mapRow(ResultSet rs) throws SQLException {
        return new TrainerAvailabilitySlot(
            rs.getString("slot_id"),
            rs.getString("trainer_id"),
            DayOfWeek.valueOf(rs.getString("day_of_week")),
            LocalTime.parse(rs.getString("start_time")),
            LocalTime.parse(rs.getString("end_time"))
        );
    }
}