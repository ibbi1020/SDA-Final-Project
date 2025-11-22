package com.block20.models;

import java.time.LocalDate;

public class Transaction {
    private String transactionId;
    private String memberId;
    private String type; // "Enrollment" or "Renewal"
    private double amount;
    private LocalDate date;

    public Transaction(String transactionId, String memberId, String type, double amount) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.type = type;
        this.amount = amount;
        this.date = LocalDate.now();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getMemberId() { return memberId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}