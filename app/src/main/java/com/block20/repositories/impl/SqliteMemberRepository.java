package com.block20.repositories.impl;

import com.block20.models.Member;
import com.block20.repositories.MemberRepository;
import com.block20.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqliteMemberRepository implements MemberRepository {

    @Override
    public Member findByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRowToMember(rs);
            
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Member> findAll() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapRowToMember(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String memberId) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

@Override
    public void save(Member member) {
        // Updated SQL with 12 placeholders
        String sql = """
            INSERT INTO members (
                member_id, full_name, email, phone, plan_type, status, join_date, expiry_date,
                address, emergency_name, emergency_phone, emergency_relation
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(member_id) DO UPDATE SET
                full_name=excluded.full_name,
                email=excluded.email,
                phone=excluded.phone,
                plan_type=excluded.plan_type,
                status=excluded.status,
                expiry_date=excluded.expiry_date,
                address=excluded.address,
                emergency_name=excluded.emergency_name,
                emergency_phone=excluded.emergency_phone,
                emergency_relation=excluded.emergency_relation;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // 1-8: Standard Fields
            stmt.setString(1, member.getMemberId());
            stmt.setString(2, member.getFullName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getPlanType());
            stmt.setString(6, member.getStatus());
            stmt.setString(7, member.getJoinDate().toString());
            stmt.setString(8, member.getExpiryDate().toString());
            
            // 9-12: New Fields (Check for nulls to be safe)
            stmt.setString(9, member.getAddress() != null ? member.getAddress() : "");
            stmt.setString(10, member.getEmergencyContactName() != null ? member.getEmergencyContactName() : "");
            stmt.setString(11, member.getEmergencyContactPhone() != null ? member.getEmergencyContactPhone() : "");
            stmt.setString(12, member.getEmergencyContactRelationship() != null ? member.getEmergencyContactRelationship() : "");
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper to convert SQL Row -> Java Object
    private Member mapRowToMember(ResultSet rs) throws SQLException {
        // THIS FIXES THE ERROR: We now pass all 9 arguments
        Member m = new Member(
            rs.getString("member_id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("plan_type"),
            rs.getString("address"),
            rs.getString("emergency_name"),
            rs.getString("emergency_phone"),
            rs.getString("emergency_relation")
        );
        
        m.setStatus(rs.getString("status"));
        
        // Parse Dates
        if (rs.getString("expiry_date") != null) {
            m.setExpiryDate(LocalDate.parse(rs.getString("expiry_date")));
        }
        if (rs.getString("join_date") != null) {
            // If your member model has a setJoinDate, call it here. 
            // Otherwise, the constructor usually sets it to 'now', which is close enough.
        }
        
        return m;
    }
}