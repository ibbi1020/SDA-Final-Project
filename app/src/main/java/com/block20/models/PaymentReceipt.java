package com.block20.models;

import java.time.LocalDateTime;

/**
 * Represents the confirmation generated after a successful payment.
 */
public class PaymentReceipt {
    private final String transactionId;
    private final String memberId;
    private final String description;
    private final double subtotal;
    private final double taxAmount;
    private final double total;
    private final String method;
    private final String referenceCode;
    private final String status;
    private final LocalDateTime processedAt;

    public PaymentReceipt(String transactionId,
                          String memberId,
                          String description,
                          double subtotal,
                          double taxAmount,
                          String method,
                          String referenceCode,
                          String status,
                          LocalDateTime processedAt) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.description = description;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.total = subtotal + taxAmount;
        this.method = method;
        this.referenceCode = referenceCode;
        this.status = status;
        this.processedAt = processedAt;
    }

    public String getTransactionId() { return transactionId; }
    public String getMemberId() { return memberId; }
    public String getDescription() { return description; }
    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotal() { return total; }
    public String getMethod() { return method; }
    public String getReferenceCode() { return referenceCode; }
    public String getStatus() { return status; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
