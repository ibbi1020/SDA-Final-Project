package com.block20.models;

import java.util.Objects;

/**
 * Immutable value object describing an inbound payment attempt.
 */
public class PaymentRequest {
    private final String memberId;
    private final double subtotal;
    private final double taxAmount;
    private final String description;
    private final String method; // Card, Cash, etc.
    private final CardDetails cardDetails;

    public PaymentRequest(String memberId,
                          double subtotal,
                          double taxAmount,
                          String description,
                          String method,
                          CardDetails cardDetails) {
        this.memberId = Objects.requireNonNull(memberId, "memberId");
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.description = description == null ? "" : description;
        this.method = method == null ? "Unknown" : method;
        this.cardDetails = cardDetails;
    }

    public String getMemberId() { return memberId; }
    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotal() { return subtotal + taxAmount; }
    public String getDescription() { return description; }
    public String getMethod() { return method; }
    public CardDetails getCardDetails() { return cardDetails; }

    public boolean isCardPayment() {
        return "Card".equalsIgnoreCase(method);
    }

    public static class CardDetails {
        private final String cardNumber;
        private final String cardholderName;
        private final String expiry;
        private final String cvv;

        public CardDetails(String cardNumber, String cardholderName, String expiry, String cvv) {
            this.cardNumber = cardNumber;
            this.cardholderName = cardholderName;
            this.expiry = expiry;
            this.cvv = cvv;
        }

        public String getCardNumber() { return cardNumber; }
        public String getCardholderName() { return cardholderName; }
        public String getExpiry() { return expiry; }
        public String getCvv() { return cvv; }
    }
}
