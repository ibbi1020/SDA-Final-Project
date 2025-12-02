package com.block20.repositories.impl;

import com.block20.models.PaymentPlan;
import com.block20.repositories.PaymentPlanRepository;
import com.block20.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlitePaymentPlanRepository implements PaymentPlanRepository {

    @Override
    public void save(PaymentPlan plan) {
        if (plan == null) {
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return;
            }
            conn.setAutoCommit(false);
            upsertPlan(conn, plan);
            replaceInstallments(conn, plan);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void upsertPlan(Connection conn, PaymentPlan plan) throws SQLException {
        String sql = """
            INSERT INTO payment_plans (plan_id, member_id, total_amount, created_on, status)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(plan_id) DO UPDATE SET
                member_id = excluded.member_id,
                total_amount = excluded.total_amount,
                created_on = excluded.created_on,
                status = excluded.status;
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plan.getPlanId());
            stmt.setString(2, plan.getMemberId());
            stmt.setDouble(3, plan.getTotalAmount());
            stmt.setString(4, plan.getCreatedOn().toString());
            stmt.setString(5, plan.getStatus());
            stmt.executeUpdate();
        }
    }

    private void replaceInstallments(Connection conn, PaymentPlan plan) throws SQLException {
        try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM payment_plan_installments WHERE plan_id = ?")) {
            deleteStmt.setString(1, plan.getPlanId());
            deleteStmt.executeUpdate();
        }

        String insertSql = """
            INSERT INTO payment_plan_installments (installment_id, plan_id, due_date, amount, paid, paid_on)
            VALUES (?, ?, ?, ?, ?, ?);
        """;
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (PaymentPlan.Installment installment : plan.getInstallments()) {
                insertStmt.setString(1, installment.getInstallmentId());
                insertStmt.setString(2, plan.getPlanId());
                insertStmt.setString(3, installment.getDueDate().toString());
                insertStmt.setDouble(4, installment.getAmount());
                insertStmt.setInt(5, installment.isPaid() ? 1 : 0);
                insertStmt.setString(6, installment.getPaidOn() != null ? installment.getPaidOn().toString() : null);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    @Override
    public PaymentPlan findById(String planId) {
        if (planId == null) {
            return null;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return null;
            }
            String sql = "SELECT * FROM payment_plans WHERE plan_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, planId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapPlan(conn, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<PaymentPlan> findByMemberId(String memberId) {
        if (memberId == null) {
            return Collections.emptyList();
        }
        List<PaymentPlan> plans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return plans;
            }
            String sql = "SELECT * FROM payment_plans WHERE member_id = ? ORDER BY created_on DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, memberId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    plans.add(mapPlan(conn, rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }

    private PaymentPlan mapPlan(Connection conn, ResultSet rs) throws SQLException {
        String planId = rs.getString("plan_id");
        List<PaymentPlan.Installment> installments = loadInstallments(conn, planId);
        PaymentPlan plan = new PaymentPlan(
                planId,
                rs.getString("member_id"),
                rs.getDouble("total_amount"),
                installments,
                LocalDate.parse(rs.getString("created_on"))
        );
        plan.setStatus(rs.getString("status"));
        return plan;
    }

    private List<PaymentPlan.Installment> loadInstallments(Connection conn, String planId) throws SQLException {
        List<PaymentPlan.Installment> installments = new ArrayList<>();
        String sql = "SELECT * FROM payment_plan_installments WHERE plan_id = ? ORDER BY due_date";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, planId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PaymentPlan.Installment installment = new PaymentPlan.Installment(
                        rs.getString("installment_id"),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getDouble("amount")
                );
                if (rs.getInt("paid") == 1) {
                    String paidOn = rs.getString("paid_on");
                    installment.markPaid(paidOn != null ? LocalDate.parse(paidOn) : LocalDate.now());
                }
                installments.add(installment);
            }
        }
        return installments;
    }
}
