package com.block20.repositories.impl;

import com.block20.models.Equipment;
import com.block20.repositories.EquipmentRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteEquipmentRepository implements EquipmentRepository {

    @Override
    public void save(Equipment e) {
        String sql = "INSERT INTO equipment (equipment_id, name, category, status, purchase_date) VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT(equipment_id) DO UPDATE SET status=excluded.status";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getEquipmentId());
            stmt.setString(2, e.getName());
            stmt.setString(3, e.getCategory());
            stmt.setString(4, e.getStatus());
            stmt.setString(5, e.getPurchaseDate().toString());
            stmt.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    @Override
    public List<Equipment> findAll() {
        return query("SELECT * FROM equipment");
    }

    @Override
    public Equipment findById(String id) {
        List<Equipment> list = query("SELECT * FROM equipment WHERE equipment_id = '" + id + "'");
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Equipment> query(String sql) {
        List<Equipment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Equipment(
                    rs.getString("equipment_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}