package com.block20.services.impl;

import com.block20.models.AuditLog;
import com.block20.repositories.AuditRepository;
import com.block20.services.AuditService;
import java.util.List;
import java.util.UUID;

public class AuditServiceImpl implements AuditService {
    private AuditRepository auditRepo;

    public AuditServiceImpl(AuditRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Override
    public void logAction(String targetId, String action, String details) {
        String id = "LOG" + UUID.randomUUID().toString().substring(0, 8);
        AuditLog log = new AuditLog(id, targetId, action, details);
        auditRepo.save(log);
        System.out.println("Audit: " + action + " on " + targetId);
    }

    @Override
    public List<AuditLog> getLogsForMember(String memberId) {
        return auditRepo.findByTargetId(memberId);
    }
}