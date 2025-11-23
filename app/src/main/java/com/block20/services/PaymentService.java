package com.block20.services;

import com.block20.models.PaymentPlan;
import com.block20.models.PaymentReceipt;
import com.block20.models.PaymentRequest;
import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    PaymentReceipt processPayment(PaymentRequest request);
    List<PaymentReceipt> getPaymentsForMember(String memberId);

    PaymentPlan createPaymentPlan(String memberId,
                                  double totalAmount,
                                  int installments,
                                  LocalDate firstDueDate);

    void recordInstallmentPayment(String planId,
                                   String installmentId,
                                   PaymentRequest request);

    List<PaymentPlan> getActivePlans(String memberId);

    double getOutstandingBalance(String memberId);
}
