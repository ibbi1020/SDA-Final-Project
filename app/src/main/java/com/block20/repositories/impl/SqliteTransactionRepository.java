package com.block20.repositories.impl;

import com.block20.models.Transaction;
import com.block20.repositories.TransactionRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqliteTransactionRepository implements TransactionRepository {

    @Override
    public void save(Transaction t) {
        String sql = "INSERT INTO transactions (transaction_id, member_id, type, amount, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getTransactionId());
            stmt.setString(2, t.getMemberId());
            stmt.setString(3, t.getType());
            stmt.setDouble(4, t.getAmount());
            stmt.setString(5, t.getDate().toString());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM transactions")) {
            while (rs.next()) {
                // Note: This assumes your Transaction Model has this constructor. 
                // If not, update Transaction.java to accept Date!
                // For now, we use the standard constructor and assume date is NOW (limitation of current model).
                // To fix properly: Add setDate() to Transaction model.
                Transaction t = new Transaction(
                    rs.getString("transaction_id"), 
                    rs.getString("member_id"), 
                    rs.getString("type"), 
                    rs.getDouble("amount")
                );
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}