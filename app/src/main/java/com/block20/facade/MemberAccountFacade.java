package com.block20.facade;

import com.block20.models.PaymentPlan;
import com.block20.models.PaymentReceipt;
import com.block20.models.PaymentRequest;
import com.block20.models.Transaction;
import com.block20.services.MemberService;
import com.block20.services.PaymentService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Facade consolidating member account lookups (transactions, receipts,
 * balances, and installment plans) so UI controllers hit a single API.
 */
public class MemberAccountFacade {

    private final MemberService memberService;
    private final PaymentService paymentService;

    public MemberAccountFacade(MemberService memberService, PaymentService paymentService) {
        this.memberService = Objects.requireNonNull(memberService, "memberService");
        this.paymentService = Objects.requireNonNull(paymentService, "paymentService");
    }

    public PaymentSnapshot getPaymentSnapshot(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            return PaymentSnapshot.empty();
        }

        List<Transaction> transactions = memberService.getTransactionsForMember(memberId);
        List<PaymentPlan> plans = paymentService.getActivePlans(memberId);
        List<PaymentReceipt> receipts = paymentService.getPaymentsForMember(memberId);
        double outstandingBalance = paymentService.getOutstandingBalance(memberId);
        return new PaymentSnapshot(transactions, receipts, plans, outstandingBalance);
    }

    public void renewMembership(String memberId, String planType) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("memberId is required");
        }
        if (planType == null || planType.isBlank()) {
            throw new IllegalArgumentException("planType is required");
        }
        memberService.renewMembership(memberId, planType);
    }

    public void recordInstallmentPayment(String planId,
                                         String installmentId,
                                         PaymentRequest request) {
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("planId is required");
        }
        if (installmentId == null || installmentId.isBlank()) {
            throw new IllegalArgumentException("installmentId is required");
        }
        paymentService.recordInstallmentPayment(planId, installmentId, request);
    }

    public static final class PaymentSnapshot {
        private static final PaymentSnapshot EMPTY = new PaymentSnapshot(
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            0.0
        );

        private final List<Transaction> transactions;
        private final List<PaymentReceipt> receipts;
        private final List<PaymentPlan> activePlans;
        private final double outstandingBalance;

        private PaymentSnapshot(List<Transaction> transactions,
                                List<PaymentReceipt> receipts,
                                List<PaymentPlan> activePlans,
                                double outstandingBalance) {
            this.transactions = transactions != null ? List.copyOf(transactions) : Collections.emptyList();
            this.receipts = receipts != null ? List.copyOf(receipts) : Collections.emptyList();
            this.activePlans = activePlans != null ? List.copyOf(activePlans) : Collections.emptyList();
            this.outstandingBalance = outstandingBalance;
        }

        public static PaymentSnapshot empty() {
            return EMPTY;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public List<PaymentReceipt> getReceipts() {
            return receipts;
        }

        public List<PaymentPlan> getActivePlans() {
            return activePlans;
        }

        public double getOutstandingBalance() {
            return outstandingBalance;
        }

        public double getTotalPaid() {
            return receipts.stream().mapToDouble(PaymentReceipt::getTotal).sum();
        }
    }
}
