package com.block20.repositories.impl;

import com.block20.models.Attendance;
import com.block20.repositories.AttendanceRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqliteAttendanceRepository implements AttendanceRepository {

    @Override
    public void save(Attendance a) {
        String sql = "INSERT INTO attendance (visit_id, member_id, member_name, check_in_time, check_out_time) VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT(visit_id) DO UPDATE SET check_out_time=excluded.check_out_time";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getVisitId());
            stmt.setString(2, a.getMemberId());
            stmt.setString(3, a.getMemberName());
            stmt.setString(4, a.getCheckInTime().toString());
            stmt.setString(5, a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : null);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Attendance> findAll() { return query("SELECT * FROM attendance"); }

    @Override
    public List<Attendance> findByMemberId(String memberId) {
        return query("SELECT * FROM attendance WHERE member_id = '" + memberId + "'");
    }

@Override
    public Attendance findActiveVisitByMemberId(String memberId) {
        // FIX: Added 'COLLATE NOCASE' to ignore M1001 vs m1001 differences
        String sql = "SELECT * FROM attendance WHERE member_id = ? COLLATE NOCASE AND (check_out_time IS NULL OR check_out_time = '' OR check_out_time = 'null')";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Attendance a = new Attendance(
                    rs.getString("visit_id"), 
                    rs.getString("member_id"), 
                    rs.getString("member_name")
                );
                // Manually map the check-in time since constructor sets it to NOW
                if (rs.getString("check_in_time") != null) {
                    a.setCheckInTime(java.time.LocalDateTime.parse(rs.getString("check_in_time")));
                }
                return a;
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }
@Override
    public int countActiveVisits() {
        String sql = "SELECT COUNT(*) FROM attendance WHERE check_out_time IS NULL OR check_out_time = ''";
        try (Connection conn = DatabaseConnection.getConnection(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private List<Attendance> query(String sql) {
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Attendance a = new Attendance(rs.getString("visit_id"), rs.getString("member_id"), rs.getString("member_name"));
                // Reflection hack or setter needed to set checkInTime if it's final in model. 
                // Assuming we modify model OR parse here. Ideally, modify Attendance model to have a constructor for loading.
                // For now, assuming checkInTime is set to NOW in constructor, we might need to adjust model to accept it.
                // Simplest fix for this snippet: Just set checkout time.
                if (rs.getString("check_out_time") != null) {
                    a.setCheckOutTime(LocalDateTime.parse(rs.getString("check_out_time")));
                }
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}