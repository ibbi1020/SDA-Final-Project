package com.block20.services.impl;

import com.block20.models.PaymentGatewayResponse;
import com.block20.models.PaymentRequest;
import com.block20.services.PaymentGateway;
import java.util.UUID;

/**
 * Simple gateway adapter that simulates approvals and declines.
 */
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResponse charge(PaymentRequest request) {
        if (!request.isCardPayment()) {
            return new PaymentGatewayResponse(true, "CASH-" + System.currentTimeMillis(), "Cash payment accepted");
        }

        PaymentRequest.CardDetails details = request.getCardDetails();
        if (details == null) {
            return new PaymentGatewayResponse(false, null, "Missing card details");
        }

        if (details.getCardNumber() == null || details.getCardNumber().replaceAll("\\s", "").length() < 12) {
            return new PaymentGatewayResponse(false, null, "Invalid card number");
        }

        if (details.getCvv() == null || details.getCvv().length() < 3) {
            return new PaymentGatewayResponse(false, null, "Invalid CVV");
        }

        String reference = "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentGatewayResponse(true, reference, "Approved");
    }
}
