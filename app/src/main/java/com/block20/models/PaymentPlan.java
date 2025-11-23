package com.block20.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a simple installment-based payment arrangement.
 */
public class PaymentPlan {
    private final String planId;
    private final String memberId;
    private final double totalAmount;
    private final List<Installment> installments;
    private final LocalDate createdOn;
    private String status; // ACTIVE, COMPLETED, CANCELLED

    public PaymentPlan(String planId,
                       String memberId,
                       double totalAmount,
                       List<Installment> installments,
                       LocalDate createdOn) {
        this.planId = planId;
        this.memberId = memberId;
        this.totalAmount = totalAmount;
        this.installments = new ArrayList<>(installments);
        this.createdOn = createdOn;
        this.status = "ACTIVE";
    }

    public String getPlanId() { return planId; }
    public String getMemberId() { return memberId; }
    public double getTotalAmount() { return totalAmount; }
    public List<Installment> getInstallments() { return Collections.unmodifiableList(installments); }
    public LocalDate getCreatedOn() { return createdOn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getOutstandingAmount() {
        return installments.stream()
            .filter(i -> !i.isPaid())
            .mapToDouble(Installment::getAmount)
            .sum();
    }

    public List<Installment> getOverdueInstallments(LocalDate today) {
        List<Installment> overdue = new ArrayList<>();
        for (Installment installment : installments) {
            if (!installment.isPaid() && installment.getDueDate().isBefore(today)) {
                overdue.add(installment);
            }
        }
        return overdue;
    }

    public static class Installment {
        private final String installmentId;
        private final LocalDate dueDate;
        private final double amount;
        private boolean paid;
        private LocalDate paidOn;

        public Installment(String installmentId, LocalDate dueDate, double amount) {
            this.installmentId = installmentId;
            this.dueDate = dueDate;
            this.amount = amount;
            this.paid = false;
        }

        public String getInstallmentId() { return installmentId; }
        public LocalDate getDueDate() { return dueDate; }
        public double getAmount() { return amount; }
        public boolean isPaid() { return paid; }
        public LocalDate getPaidOn() { return paidOn; }

        public void markPaid(LocalDate paidOn) {
            this.paid = true;
            this.paidOn = paidOn;
        }
    }
}
