package com.block20.repositories.impl;

import com.block20.models.PaymentPlan;
import com.block20.repositories.PaymentPlanRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentPlanRepositoryImpl implements PaymentPlanRepository {
    private final Map<String, PaymentPlan> plans = new HashMap<>();

    @Override
    public void save(PaymentPlan plan) {
        plans.put(plan.getPlanId(), plan);
    }

    @Override
    public PaymentPlan findById(String planId) {
        return plans.get(planId);
    }

    @Override
    public List<PaymentPlan> findByMemberId(String memberId) {
        return plans.values().stream()
            .filter(plan -> plan.getMemberId().equalsIgnoreCase(memberId))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
