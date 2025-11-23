package com.block20.repositories;

import com.block20.models.PaymentPlan;
import java.util.List;

public interface PaymentPlanRepository {
    void save(PaymentPlan plan);
    PaymentPlan findById(String planId);
    List<PaymentPlan> findByMemberId(String memberId);
}
