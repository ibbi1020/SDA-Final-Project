package com.block20.services.impl;

import com.block20.models.PaymentGatewayResponse;
import com.block20.models.PaymentRequest;
import com.block20.services.PaymentGateway;
import com.block20.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Local gateway adapter that validates basic card data and writes
 * each attempt to SQLite for downstream reconciliation.
 */
public class LocalPaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResponse charge(PaymentRequest request) {
        if (request == null) {
            return new PaymentGatewayResponse(false, null, "Invalid payment request");
        }

        boolean approved = validateRequest(request);
        String reference = approved ? generateReference() : null;
        String message = approved ? "Approved" : "Declined";

        persistReceipt(request, reference, approved, message);
        return new PaymentGatewayResponse(approved, reference, message);
    }

    private boolean validateRequest(PaymentRequest request) {
        if (!request.isCardPayment()) {
            return request.getTotal() >= 0;
        }
        PaymentRequest.CardDetails details = request.getCardDetails();
        if (details == null) {
            return false;
        }
        String cardNumber = sanitize(details.getCardNumber());
        if (cardNumber.length() < 12) {
            return false;
        }
        if (details.getCvv() == null || details.getCvv().length() < 3) {
            return false;
        }
        return true;
    }

    private void persistReceipt(PaymentRequest request,
                                String reference,
                                boolean approved,
                                String message) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return;
            }
            String sql = """
                INSERT INTO payment_gateway_receipts (
                    receipt_id, member_id, method, amount, reference,
                    status, message, card_last4, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, request.getMemberId());
                stmt.setString(3, request.getMethod());
                stmt.setDouble(4, request.getTotal());
                stmt.setString(5, reference);
                stmt.setString(6, approved ? "APPROVED" : "DECLINED");
                stmt.setString(7, message);
                stmt.setString(8, maskCard(request));
                stmt.setString(9, LocalDateTime.now().toString());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String maskCard(PaymentRequest request) {
        if (!request.isCardPayment() || request.getCardDetails() == null) {
            return null;
        }
        String cardNumber = sanitize(request.getCardDetails().getCardNumber());
        if (cardNumber.length() < 4) {
            return cardNumber;
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replaceAll("\\s", "");
    }

    private String generateReference() {
        return "PG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
