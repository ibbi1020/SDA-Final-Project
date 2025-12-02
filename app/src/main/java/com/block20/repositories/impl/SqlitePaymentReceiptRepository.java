package com.block20.repositories.impl;

import com.block20.models.PaymentReceipt;
import com.block20.repositories.PaymentReceiptRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlitePaymentReceiptRepository implements PaymentReceiptRepository {

    @Override
    public void save(PaymentReceipt receipt) {
        if (receipt == null) {
            return;
        }

        String sql = """
            INSERT INTO payment_receipts (
                transaction_id, member_id, description, subtotal,
                tax_amount, method, reference_code, status, processed_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(transaction_id) DO UPDATE SET
                member_id = excluded.member_id,
                description = excluded.description,
                subtotal = excluded.subtotal,
                tax_amount = excluded.tax_amount,
                method = excluded.method,
                reference_code = excluded.reference_code,
                status = excluded.status,
                processed_at = excluded.processed_at;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, receipt.getTransactionId());
            stmt.setString(2, receipt.getMemberId());
            stmt.setString(3, receipt.getDescription());
            stmt.setDouble(4, receipt.getSubtotal());
            stmt.setDouble(5, receipt.getTaxAmount());
            stmt.setString(6, receipt.getMethod());
            stmt.setString(7, receipt.getReferenceCode());
            stmt.setString(8, receipt.getStatus());
            stmt.setString(9, receipt.getProcessedAt().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PaymentReceipt> findByMemberId(String memberId) {
        List<PaymentReceipt> receipts = new ArrayList<>();
        if (memberId == null || memberId.isBlank()) {
            return receipts;
        }

        String sql = """
            SELECT transaction_id, member_id, description, subtotal,
                   tax_amount, method, reference_code, status, processed_at
            FROM payment_receipts
            WHERE member_id = ?
            ORDER BY processed_at DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    receipts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receipts;
    }

    private PaymentReceipt mapRow(ResultSet rs) throws SQLException {
        return new PaymentReceipt(
            rs.getString("transaction_id"),
            rs.getString("member_id"),
            rs.getString("description"),
            rs.getDouble("subtotal"),
            rs.getDouble("tax_amount"),
            rs.getString("method"),
            rs.getString("reference_code"),
            rs.getString("status"),
            LocalDateTime.parse(rs.getString("processed_at"))
        );
    }
}
