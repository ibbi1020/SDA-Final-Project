package com.block20.services.impl;

import com.block20.models.PaymentGatewayResponse;
import com.block20.models.PaymentPlan;
import com.block20.models.PaymentPlan.Installment;
import com.block20.models.PaymentReceipt;
import com.block20.models.PaymentRequest;
import com.block20.models.Transaction;
import com.block20.repositories.PaymentPlanRepository;
import com.block20.repositories.TransactionRepository;
import com.block20.services.PaymentGateway;
import com.block20.services.PaymentService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentServiceImpl implements PaymentService {
    private final TransactionRepository transactionRepository;
    private final PaymentPlanRepository paymentPlanRepository;
    private final PaymentGateway paymentGateway;
    private final List<PaymentReceipt> receipts = new ArrayList<>();

    public PaymentServiceImpl(TransactionRepository transactionRepository,
                              PaymentPlanRepository paymentPlanRepository,
                              PaymentGateway paymentGateway) {
        this.transactionRepository = transactionRepository;
        this.paymentPlanRepository = paymentPlanRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    public PaymentReceipt processPayment(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request is required");
        }

        PaymentGatewayResponse gatewayResponse = paymentGateway.charge(request);
        if (!gatewayResponse.isSuccess()) {
            throw new IllegalStateException("Payment declined: " + gatewayResponse.getMessage());
        }

        String transactionId = "TXN" + System.currentTimeMillis();
        Transaction txn = new Transaction(transactionId,
            request.getMemberId(),
            request.getDescription(),
            request.getTotal());
        transactionRepository.save(txn);

        PaymentReceipt receipt = new PaymentReceipt(
            transactionId,
            request.getMemberId(),
            request.getDescription(),
            request.getSubtotal(),
            request.getTaxAmount(),
            request.getMethod(),
            gatewayResponse.getReferenceId(),
            "SUCCESS",
            LocalDateTime.now()
        );
        receipts.add(receipt);
        return receipt;
    }

    @Override
    public List<PaymentReceipt> getPaymentsForMember(String memberId) {
        if (memberId == null) {
            return Collections.emptyList();
        }
        return receipts.stream()
            .filter(receipt -> memberId.equalsIgnoreCase(receipt.getMemberId()))
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public PaymentPlan createPaymentPlan(String memberId,
                                         double totalAmount,
                                         int installments,
                                         LocalDate firstDueDate) {
        if (installments <= 0) {
            throw new IllegalArgumentException("Installments must be greater than zero");
        }
        if (firstDueDate == null) {
            firstDueDate = LocalDate.now().plusMonths(1);
        }

        double roundedInstallment = Math.round((totalAmount / installments) * 100.0) / 100.0;
        List<Installment> schedule = new ArrayList<>();
        LocalDate dueDate = firstDueDate;
        double accumulated = 0.0;
        for (int i = 0; i < installments; i++) {
            double amount = (i == installments - 1)
                ? Math.round((totalAmount - accumulated) * 100.0) / 100.0
                : roundedInstallment;
            accumulated += amount;
            schedule.add(new Installment("INST" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(), dueDate, amount));
            dueDate = dueDate.plusMonths(1);
        }

        PaymentPlan plan = new PaymentPlan(
            "PLAN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            memberId,
            totalAmount,
            schedule,
            LocalDate.now()
        );
        paymentPlanRepository.save(plan);
        return plan;
    }

    @Override
    public void recordInstallmentPayment(String planId,
                                          String installmentId,
                                          PaymentRequest request) {
        PaymentPlan plan = paymentPlanRepository.findById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Payment plan not found");
        }

        Optional<Installment> installmentOpt = plan.getInstallments().stream()
            .filter(inst -> inst.getInstallmentId().equalsIgnoreCase(installmentId))
            .findFirst();

        if (installmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Installment not found");
        }

        Installment installment = installmentOpt.get();
        if (installment.isPaid()) {
            throw new IllegalStateException("Installment already paid");
        }

        PaymentRequest installmentRequest = request;
        if (installmentRequest == null) {
            installmentRequest = new PaymentRequest(
                plan.getMemberId(),
                installment.getAmount(),
                0.0,
                "Payment Plan Installment",
                "Cash",
                null
            );
        }

        processPayment(installmentRequest);
        installment.markPaid(LocalDate.now());

        if (plan.getOutstandingAmount() == 0.0) {
            plan.setStatus("COMPLETED");
        }

        paymentPlanRepository.save(plan);
    }

    @Override
    public List<PaymentPlan> getActivePlans(String memberId) {
        return paymentPlanRepository.findByMemberId(memberId);
    }

    @Override
    public double getOutstandingBalance(String memberId) {
        return paymentPlanRepository.findByMemberId(memberId).stream()
            .filter(plan -> !"COMPLETED".equalsIgnoreCase(plan.getStatus()))
            .mapToDouble(PaymentPlan::getOutstandingAmount)
            .sum();
    }
}
