package com.block20.repositories.impl;

import com.block20.models.AuditLog;
import com.block20.repositories.AuditRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditRepositoryImpl implements AuditRepository {
    private List<AuditLog> logs = new ArrayList<>();

    @Override
    public void save(AuditLog log) {
        logs.add(log);
    }

    @Override
    public List<AuditLog> findByTargetId(String targetId) {
        return logs.stream()
                .filter(l -> l.getTargetId().equals(targetId))
                .sorted((a, b) -> b.getTimestampFormatted().compareTo(a.getTimestampFormatted())) // Newest first (simple string sort)
                .collect(Collectors.toList());
    }
}