package com.block20.repositories;

import com.block20.models.AuditLog;
import java.util.List;

public interface AuditRepository {
    void save(AuditLog log);
    List<AuditLog> findByTargetId(String targetId);
}